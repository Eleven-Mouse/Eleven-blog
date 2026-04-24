package blog.service.impl;

import blog.service.OauthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * GitHub OAuth 服务实现
 *
 * 流程 (比 QQ 简单很多):
 * 1. 构建授权 URL → 用户在 GitHub 页面授权
 * 2. GitHub 回调携带 code → 用 code 换 access_token
 * 3. 用 access_token 获取用户信息 (nickname, avatar)
 * 4. 直接返回给前端，不存数据库
 */
@Service
@Slf4j
public class OauthServiceImpl implements OauthService {

    @Value("${github.oauth.client-id:}")
    private String clientId;

    @Value("${github.oauth.client-secret:}")
    private String clientSecret;

    @Value("${github.oauth.redirect-uri:}")
    private String redirectUri;

    @Value("${blog.owner.github-id:0}")
    private Long ownerGithubId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String buildGitHubLoginUrl(String state) {
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=user:email" +
                "&state=" + state;
    }

    @Override
    public Map<String, Object> handleGitHubCallback(String code) {
        // Step 1: 用 code 换 access_token
        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, request, String.class);

        String accessToken = null;
        try {
            JsonNode tokenNode = objectMapper.readTree(tokenResponse.getBody());
            accessToken = tokenNode.has("access_token") ? tokenNode.get("access_token").asText() : null;
        } catch (Exception e) {
            log.error("解析 GitHub access_token 失败: {}", e.getMessage());
        }

        if (accessToken == null) {
            throw new RuntimeException("获取 GitHub access_token 失败");
        }
        log.info("获取 GitHub access_token 成功");

        // Step 2: 用 access_token 获取用户信息
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.set("Authorization", "Bearer " + accessToken);
        userHeaders.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<String> userResponse = restTemplate.exchange(
                "https://api.github.com/user", HttpMethod.GET, userRequest, String.class);

        String nickname = "GitHub用户";
        String avatar = "";
        long githubId = 0;

        try {
            JsonNode userNode = objectMapper.readTree(userResponse.getBody());
            // GitHub 用户名: name (可空) > login (必有)
            if (userNode.has("name") && !userNode.get("name").isNull() && !userNode.get("name").asText().isEmpty()) {
                nickname = userNode.get("name").asText();
            } else if (userNode.has("login")) {
                nickname = userNode.get("login").asText();
            }
            if (userNode.has("avatar_url")) {
                avatar = userNode.get("avatar_url").asText();
            }
            if (userNode.has("id")) {
                githubId = userNode.get("id").asLong();
            }
        } catch (Exception e) {
            log.warn("解析 GitHub 用户信息失败: {}", e.getMessage());
        }

        boolean isOwner = ownerGithubId != null && ownerGithubId > 0 && ownerGithubId == githubId;

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", nickname);
        result.put("avatar", avatar);
        result.put("githubId", githubId);
        result.put("isOwner", isOwner);
        return result;
    }
}
