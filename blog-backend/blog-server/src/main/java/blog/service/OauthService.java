package blog.service;

import java.util.Map;

/**
 * GitHub OAuth 服务接口
 * 不需要用户表，仅用于获取 GitHub 用户信息（昵称+头像）
 */
public interface OauthService {

    /**
     * 构建 GitHub 授权登录 URL
     */
    String buildGitHubLoginUrl(String state);

    /**
     * 处理 GitHub 回调，用 code 换取用户信息
     * 返回 nickname + avatar，前端直接存 localStorage
     */
    Map<String, Object> handleGitHubCallback(String code);
}
