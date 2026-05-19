package blog.controller.user;

import blog.service.ArticleSyncService;
import blog.service.SystemConfigService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.net.URI;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GitHub Webhook 控制器
 * 接收 GitHub Push 事件，触发文章同步
 * 安全机制：HMAC-SHA256 签名校验 + 仓库白名单 + 防风暴 + payload 大小限制
 */
@RestController
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {

    private static final long MIN_INTERVAL_MS = 30_000;
    private static final int MAX_PAYLOAD_SIZE = 256 * 1024; // 256KB

    private final AtomicLong lastSyncTime = new AtomicLong(0);

    private final ArticleSyncService articleSyncService;
    private final SystemConfigService configService;

    public WebhookController(ArticleSyncService articleSyncService, SystemConfigService configService) {
        this.articleSyncService = articleSyncService;
        this.configService = configService;
    }

    /**
     * GitHub Webhook 回调端点
     * POST /webhook/github
     */
    @PostMapping("/github")
    public ResponseEntity<String> handleGithubWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Delivery", required = false) String deliveryId,
            @RequestBody String payload) {

        log.info("收到 Webhook：event={}, delivery={}", event, deliveryId);

        // 1. Payload 大小检查（防止超大 body 攻击）
        if (payload != null && payload.length() > MAX_PAYLOAD_SIZE) {
            log.warn("Webhook payload 过大：{} bytes，delivery={}", payload.length(), deliveryId);
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("payload too large");
        }

        // 2. Secret 校验（必须在解析 payload 之前）
        if (!verifySignature(payload, signature)) {
            log.warn("Webhook 签名校验失败，delivery={}", deliveryId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid signature");
        }

        // 3. 只处理 push 事件
        if (!"push".equals(event)) {
            log.debug("忽略非 push 事件: {}", event);
            return ResponseEntity.ok("ignored");
        }

        // 4. 仓库白名单校验
        if (!isFromConfiguredRepo(payload)) {
            log.warn("Webhook 来源仓库不匹配，delivery={}", deliveryId);
            return ResponseEntity.ok("repo not matched");
        }

        // 5. 防风暴：30 秒内不重复同步
        long now = System.currentTimeMillis();
        long last = lastSyncTime.get();
        if (now - last < MIN_INTERVAL_MS) {
            log.info("防风暴：距上次同步不足30秒，跳过（上次={}ms前）", now - last);
            return ResponseEntity.ok("rate limited");
        }
        lastSyncTime.set(now);

        // 6. 异步触发同步（快速响应 GitHub，避免超时重试）
        triggerSyncAsync();

        return ResponseEntity.ok("ok");
    }

    @Async
    protected void triggerSyncAsync() {
        try {
            Map<String, Integer> result = articleSyncService.autoDiscover();
            log.info("Webhook 同步完成：匹配={}, 新建={}, 失败={}",
                    result.get("matched"), result.get("created"), result.get("failed"));
        } catch (Exception e) {
            log.error("Webhook 同步异常", e);
        }
    }

    /**
     * 校验 Webhook 是否来自配置的仓库
     */
    private boolean isFromConfiguredRepo(String payload) {
        try {
            Map<String, String> config = configService.getAllConfigAsMap();
            OwnerRepo ownerRepo = resolveOwnerRepo(config);
            String configOwner = ownerRepo.owner();
            String configRepo = ownerRepo.repo();
            if (configOwner == null || configOwner.isBlank() || configRepo == null || configRepo.isBlank()) {
                return true; // 未配置则不过滤
            }

            JSONObject json = JSON.parseObject(payload);
            JSONObject repo = json.getJSONObject("repository");
            if (repo == null) return false;

            JSONObject owner = repo.getJSONObject("owner");
            String repoOwner = owner != null ? owner.getString("login") : null;
            String repoName = repo.getString("name");

            boolean matched = configOwner.equalsIgnoreCase(repoOwner) && configRepo.equalsIgnoreCase(repoName);
            if (!matched) {
                log.info("仓库不匹配：期望={}/{}, 实际={}/{}", configOwner, configRepo, repoOwner, repoName);
            }
            return matched;
        } catch (Exception e) {
            log.warn("仓库校验异常，放行", e);
            return true;
        }
    }

    /**
     * HMAC-SHA256 签名校验
     */
    private boolean verifySignature(String payload, String signature) {
        String secret = getWebhookSecret();

        // Secret 未配置：开发模式，跳过校验
        if (secret == null || secret.isEmpty()) {
            if (signature == null || signature.isEmpty()) {
                log.debug("Webhook Secret 未配置，跳过签名校验");
                return true;
            }
            log.warn("收到带签名的 Webhook 但 Secret 未配置");
            return false;
        }

        // Secret 已配置但请求无签名
        if (signature == null || signature.isEmpty()) {
            log.warn("Webhook 缺少签名");
            return false;
        }

        try {
            String expected = "sha256=" + hmacSha256(secret, payload);
            return MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("签名校验异常", e);
            return false;
        }
    }

    private String getWebhookSecret() {
        try {
            Map<String, String> config = configService.getAllConfigAsMap();
            String configured = trimToNull(config.get("webhook_secret"));
            if (configured != null) {
                return configured;
            }
            return trimToNull(System.getenv("WEBHOOK_SECRET"));
        } catch (Exception e) {
            return trimToNull(System.getenv("WEBHOOK_SECRET"));
        }
    }

    private OwnerRepo resolveOwnerRepo(Map<String, String> config) {
        String owner = trimToNull(config.get("github_sync_owner"));
        String repoValue = trimToNull(config.get("github_sync_repo"));
        if (owner == null) {
            owner = trimToNull(System.getenv("GITHUB_SYNC_OWNER"));
        }
        if (repoValue == null) {
            repoValue = trimToNull(System.getenv("GITHUB_SYNC_REPO"));
        }

        OwnerRepo parsed = parseOwnerRepo(repoValue);
        if (owner == null) {
            owner = parsed.owner();
        }
        String repo = parsed.repo();
        return new OwnerRepo(owner, repo);
    }

    private OwnerRepo parseOwnerRepo(String repoValue) {
        if (repoValue == null) {
            return new OwnerRepo(null, null);
        }
        String raw = repoValue;
        if (raw.startsWith("http://") || raw.startsWith("https://")) {
            try {
                URI uri = URI.create(raw);
                if (uri.getHost() != null && uri.getHost().contains("github.com")) {
                    String path = uri.getPath();
                    if (path != null) {
                        String[] seg = path.replaceAll("^/+", "").split("/");
                        if (seg.length >= 2) {
                            return new OwnerRepo(trimToNull(seg[0]), trimToNull(seg[1]));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            return new OwnerRepo(null, raw);
        }
        if (raw.contains("/")) {
            String[] parts = raw.split("/", 2);
            return new OwnerRepo(trimToNull(parts[0]), trimToNull(parts[1]));
        }
        return new OwnerRepo(null, raw);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String hmacSha256(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private record OwnerRepo(String owner, String repo) {
    }
}
