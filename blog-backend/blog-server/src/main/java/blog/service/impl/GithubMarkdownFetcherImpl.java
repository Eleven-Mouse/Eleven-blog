package blog.service.impl;

import blog.service.GithubMarkdownFetcher;
import blog.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

/**
 * GitHub Markdown 文件获取器实现
 * 通过 GitHub Raw URL 拉取 Markdown，内置重试、超时和 Token 认证
 */
@Service
@Slf4j
public class GithubMarkdownFetcherImpl implements GithubMarkdownFetcher {

    private static final String CONFIG_KEY_TOKEN = "github_sync_token";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    private final RestTemplate restTemplate;
    private final SystemConfigService configService;

    public GithubMarkdownFetcherImpl(SystemConfigService configService) {
        this.configService = configService;
        this.restTemplate = new RestTemplate();
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(30));
        this.restTemplate.setRequestFactory(factory);
    }

    @Override
    public String fetchMarkdown(String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) {
            return null;
        }

        String rawUrl = toRawUrl(githubUrl);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpHeaders headers = buildHeaders();

                ResponseEntity<String> response = restTemplate.exchange(
                        rawUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    log.info("成功获取 GitHub Markdown，URL: {}，长度: {}", rawUrl, response.getBody().length());
                    return response.getBody();
                }

                log.warn("GitHub 返回非200状态: {}，URL: {}", response.getStatusCode(), rawUrl);
            } catch (Exception e) {
                log.warn("获取 GitHub Markdown 失败（第{}/{}次），URL: {}，原因: {}",
                        attempt, MAX_RETRIES, rawUrl, e.getMessage());
            }

            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }

        log.error("获取 GitHub Markdown 最终失败，已重试{}次，URL: {}", MAX_RETRIES, rawUrl);
        return null;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "text/plain");
        headers.set("User-Agent", "ElevenBlog-Sync/1.0");

        String token = getToken();
        if (token != null && !token.isBlank()) {
            headers.set("Authorization", "Bearer " + token);
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

    private String toRawUrl(String url) {
        if (url.contains("://github.com/") && url.contains("/blob/")) {
            return url.replace("://github.com/", "://raw.githubusercontent.com/")
                      .replace("/blob/", "/");
        }
        return url;
    }
}
