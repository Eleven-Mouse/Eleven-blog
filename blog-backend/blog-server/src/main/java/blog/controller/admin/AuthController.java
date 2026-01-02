package blog.controller.admin;

import blog.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * 认证控制器
 * 处理登录、登出等认证相关操作
 * 
 * @author Eleven
 * @version 1.0
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AuthController {
    
    // 硬编码的用户名和密码（实际项目中应该从数据库查询）
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";
    
    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session)
    {
        // 如果已经登录，直接跳转到仪表盘
        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
        if (isLogin != null && isLogin) {
            return "redirect:/admin/index";
        }
        return "admin/login";
    }
    
    /**
     * 处理登录请求
     */
    @PostMapping("/login")
    @ResponseBody
    public Result<String> login(@RequestBody Map<String, Object> loginData, HttpSession session)
    {
        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        Boolean remember = (Boolean) loginData.get("remember");
        
        log.info("用户尝试登录，用户名：{}", username);
        
        // 验证用户名和密码
        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password))
        {
            // 登录成功，将用户信息存入session
            session.setAttribute("username", username);
            session.setAttribute("isLogin", true);
            
            // 如果选择记住我，设置session超时时间为7天
            if (remember != null && remember)
            {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60);
            }
            else
            {
                session.setMaxInactiveInterval(30 * 60);
            }
            
            log.info("用户登录成功：{}", username);
            return Result.success("登录成功");
        }
        else
        {
            log.warn("用户登录失败，用户名：{}", username);
            return Result.error("用户名或密码错误");
        }
    }
    
    /**
     * 处理登出请求
     */
    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        String username = (String) session.getAttribute("username");
        session.invalidate();
        log.info("用户退出登录：{}", username);
        return "redirect:/admin/login";
    }
    
    /**
     * 检查登录状态
     */
    @GetMapping("/checkLogin")
    @ResponseBody
    public Result<Map<String, Object>> checkLogin(HttpSession session)
    {
        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
        String username = (String) session.getAttribute("username");
        
        if (isLogin != null && isLogin)
        {
            return Result.success(Map.of(
                "isLogin", true,
                "username", username));
        }
        else
        {
            return Result.error("未登录");
        }
    }
    
    /**
     * 验证token（兼容前端）
     */
    @PostMapping("/auth")
    @ResponseBody
    public Result<Map<String, Object>> authToken(@RequestBody Map<String, Object> tokenData, HttpSession session)
    {
        String token = (String) tokenData.get("token");
        log.info("验证token：{}", token);
        
        // 简单的token验证逻辑（实际项目中应该使用JWT等）
        if ("admin_token_123456".equals(token))
        {
            session.setAttribute("username", "admin");
            session.setAttribute("isLogin", true);
            session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7天
            
            return Result.success(Map.of(
                "isLogin", true,
                "username", "admin",
                "token", token));
        }
        else
        {
            return Result.error("Token无效");
        }
    }
}
