package blog.config;

import blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter
{

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. 获取 Request Header 里的 Authorization
        String bearerToken = request.getHeader("Authorization");

        // 2. 判断 Token 是否存在且格式正确 (必须以 Bearer 开头)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            try {
                // 3. 解析 Token (如果没有报错，说明 Token 有效)
                // 注意：这里我们解析的是 Access Token
                Claims claims = jwtUtil.parseToken(token);

                // 4. 获取用户名
                String username = claims.getSubject();

                // 5. 获取权限信息 (我们在生成 Token 时把 roles 放进去了，现在取出来)
                // 这样就不用每次请求都去查数据库了，提升性能
                List<String> roles = claims.get("roles", List.class);

                // 将 List<String> 转为 Spring Security 需要的 List<SimpleGrantedAuthority>
                List<SimpleGrantedAuthority> authorities = null;
                if (roles != null) {
                    authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }

                // 6. 构建认证对象 (Principal, Credentials, Authorities)
                // 这里的 Credentials 传 null 即可，因为已经通过 Token 验证了
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 7. 【关键】将认证信息存入 SecurityContext
                // Spring Security 后续的过滤器看到这里有值，就会认为用户已登录
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Token 过期、签名错误等都会报错
                // 这里不要抛出异常，直接记录日志即可。
                // 如果 SecurityContext 为空，后续的 Filter 会自动处理为 "未登录"
                logger.error("Token 校验失败: " + e.getMessage());
            }
        }

        // 8. 继续执行过滤器链 (不管有没有 Token，都要放行，让后面的 Spring Security 决定是否拦截)
        chain.doFilter(request, response);
    }

}
