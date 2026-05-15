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
 * 使用 Git Trees API 一次性递归获取所有文件，避免逐目录调用的超时问题
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
        factory.setReadTimeout(Duration.ofSeconds(60));
        this.restTemplate.setRequestFactory(factory);
    }

    @Override
    public List<MdFileInfo> scanMarkdownFiles(String owner, String repo, String branch, String path) {
        List<MdFileInfo> result = new ArrayList<>();

        // 使用 Git Trees API，一次请求获取全部文件树
        String url = String.format("%s/%s/%s/git/trees/%s?recursive=1",
                GITHUB_API_BASE, owner, repo, branch);
        log.info("使用 Git Trees API 扫描: {}", url);

        try {
            HttpHeaders headers = buildHeaders();
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.warn("GitHub Trees API 返回非200: {}", response.getStatusCode());
                // 降级：使用旧版逐目录扫描
                log.info("降级为逐目录扫描模式");
                scanDirectoryFallback(owner, repo, branch, path, result);
                return result;
            }

            checkRateLimit(response.getHeaders());

            JSONObject treeResponse = JSON.parseObject(response.getBody());
            JSONArray tree = treeResponse.getJSONArray("tree");
            boolean truncated = treeResponse.getBooleanValue("truncated");

            if (truncated) {
                log.warn("GitHub Trees API 结果被截断，仓库过大，降级为逐目录扫描");
                scanDirectoryFallback(owner, repo, branch, path, result);
                return result;
            }

            String pathPrefix = (path != null && !path.isEmpty()) ? path : "";

            for (int i = 0; i < tree.size(); i++) {
                JSONObject item = tree.getJSONObject(i);
                String itemPath = item.getString("path");
                String type = item.getString("type");
                String sha = item.getString("sha");

                // 过滤路径前缀
                if (!pathPrefix.isEmpty()) {
                    if (!itemPath.startsWith(pathPrefix + "/") && !itemPath.equals(pathPrefix)) {
                        continue;
                    }
                }

                if ("blob".equals(type) && itemPath.endsWith(".md")) {
                    String downloadUrl = String.format("https://raw.githubusercontent.com/%s/%s/%s/%s",
                            owner, repo, branch, itemPath);
                    String name = itemPath.substring(itemPath.lastIndexOf('/') + 1);
                    String title = name.endsWith(".md") ? name.substring(0, name.length() - 3) : name;
                    result.add(new MdFileInfo(name, title, downloadUrl, itemPath, sha));
                    log.info("发现 Markdown 文件: {} (SHA: {})", itemPath,
                            sha != null ? sha.substring(0, Math.min(7, sha.length())) : "N/A");
                }
            }

            log.info("Trees API 扫描完成，共发现 {} 个 Markdown 文件", result.size());

        } catch (Exception e) {
            log.error("Trees API 扫描失败: {}，降级为逐目录扫描", e.getMessage());
            scanDirectoryFallback(owner, repo, branch, path, result);
        }

        return result;
    }

    /**
     * 降级方案：逐目录递归扫描（旧逻辑）
     */
    private void scanDirectoryFallback(String owner, String repo, String branch, String path, List<MdFileInfo> result) {
        String url = buildApiUrl(owner, repo, branch, path);
        log.info("逐目录扫描: {}", url);

        try {
            HttpHeaders headers = buildHeaders();
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.warn("GitHub API 返回非200: {}，URL: {}", response.getStatusCode(), url);
                return;
            }

            checkRateLimit(response.getHeaders());

            JSONArray items = JSON.parseArray(response.getBody());
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String name = item.getString("name");
                String type = item.getString("type");
                String itemPath = item.getString("path");
                String sha = item.getString("sha");

                if ("dir".equals(type)) {
                    scanDirectoryFallback(owner, repo, branch, itemPath, result);
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
            log.error("扫描目录失败: {}，原因: {}", url, e.getMessage());
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
