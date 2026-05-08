package blog.service.impl;

import blog.service.GithubRepoScanner;
import blog.service.SystemConfigService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GitHub 仓库扫描器实现
 * 通过 GitHub Contents API 列出目录下的 .md 文件
 * 支持递归扫描子目录，支持 GitHub Token 认证
 */
@Service
@Slf4j
public class GithubRepoScannerImpl implements GithubRepoScanner {

    private static final String CONFIG_KEY_TOKEN = "github_sync_token";
    private static final String GITHUB_API_BASE = "https://api.github.com/repos";
    private final RestTemplate restTemplate;
    private final SystemConfigService configService;

    public GithubRepoScannerImpl(SystemConfigService configService) {
        this.configService = configService;
        this.restTemplate = new RestTemplate();
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(30));
        this.restTemplate.setRequestFactory(factory);
    }

    @Override
    public List<MdFileInfo> scanMarkdownFiles(String owner, String repo, String branch, String path) {
        List<MdFileInfo> result = new ArrayList<>();
        scanDirectory(owner, repo, branch, path, result);
        return result;
    }

    private void scanDirectory(String owner, String repo, String branch, String path, List<MdFileInfo> result) {
        String url = buildApiUrl(owner, repo, branch, path);
        log.info("扫描 GitHub 目录: {}", url);

        try {
            HttpHeaders headers = buildHeaders();

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.warn("GitHub API 返回非200: {}，URL: {}", response.getStatusCode(), url);
                return;
            }

            // 检查速率限制
            checkRateLimit(response.getHeaders());

            JSONArray items = JSON.parseArray(response.getBody());
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String name = item.getString("name");
                String type = item.getString("type");
                String itemPath = item.getString("path");
                String sha = item.getString("sha");

                if ("dir".equals(type)) {
                    scanDirectory(owner, repo, branch, itemPath, result);
                } else if ("file".equals(type) && name != null && name.endsWith(".md")) {
                    String downloadUrl = item.getString("download_url");
                    if (downloadUrl == null || downloadUrl.isEmpty()) {
                        downloadUrl = String.format("https://raw.githubusercontent.com/%s/%s/%s/%s",
                                owner, repo, branch, itemPath);
                    }
                    String title = name.endsWith(".md") ? name.substring(0, name.length() - 3) : name;
                    result.add(new MdFileInfo(name, title, downloadUrl, itemPath, sha));
                    log.info("发现 Markdown 文件: {} (SHA: {})", name, sha != null ? sha.substring(0, Math.min(7, sha.length())) : "N/A");
                }
            }
        } catch (Exception e) {
            log.error("扫描 GitHub 目录失败: {}，原因: {}", url, e.getMessage());
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");
        headers.set("User-Agent", "ElevenBlog-Sync/1.0");

        String token = getToken();
        if (token != null && !token.isBlank()) {
            headers.set("Authorization", "Bearer " + token);
            log.debug("使用 GitHub Token 认证");
        }
        return headers;
    }

    private String getToken() {
        try {
            Map<String, String> config = configService.getAllConfigAsMap();
            return config.get(CONFIG_KEY_TOKEN);
        } catch (Exception e) {
            return null;
        }
    }

    private void checkRateLimit(HttpHeaders headers) {
        String remaining = headers.getFirst("X-RateLimit-Remaining");
        String reset = headers.getFirst("X-RateLimit-Reset");
        if (remaining != null && Integer.parseInt(remaining) < 10) {
            log.warn("GitHub API 速率限制即将耗尽：剩余 {} 次，重置时间: {}", remaining, reset);
        }
    }

    private String buildApiUrl(String owner, String repo, String branch, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(GITHUB_API_BASE).append("/").append(owner).append("/").append(repo);
        sb.append("/contents");
        if (path != null && !path.isEmpty()) {
            sb.append("/").append(path);
        }
        sb.append("?ref=").append(branch);
        return sb.toString();
    }
}
