package blog.controller.user;

import blog.result.Result;
import blog.service.OauthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * GitHub OAuth 控制器
 *
 * GET /api/oauth/github/url       → 前端获取 GitHub 授权跳转 URL
 * GET /api/oauth/github/callback  → GitHub 回调，返回昵称+头像
 */
@RestController
@RequestMapping("/api/oauth")
@Slf4j
public class OauthController {

    @Autowired
    private OauthService oauthService;

    @GetMapping("/github/url")
    public Result<String> getGitHubLoginUrl() {
        String state = UUID.randomUUID().toString().replace("-", "");
        String url = oauthService.buildGitHubLoginUrl(state);
        log.info("生成 GitHub 登录 URL, state={}", state);
        return Result.success(url);
    }

    @GetMapping("/github/callback")
    public Result<Map<String, Object>> githubCallback(@RequestParam String code,
                                                       @RequestParam(required = false) String state) {
        log.info("收到 GitHub 回调, code={}", code);
        try {
            Map<String, Object> userInfo = oauthService.handleGitHubCallback(code);
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("GitHub 登录处理失败: {}", e.getMessage(), e);
            return Result.error("GitHub 登录失败: " + e.getMessage());
        }
    }
}
