package blog.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtFilter jwtFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    /**
     * 1. 密码加密器
     * Spring Security 要求必须配置，这里使用最标准的 BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 2. 暴露 AuthenticationManager
     * 在 Login 接口中需要用它来手动触发认证（验证账号密码）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 3. 核心安全过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF (因为我们使用 Token，不需要 Cookie/Session，所以不需要防 CSRF)
                .csrf(AbstractHttpConfigurer::disable)

                // 启用 CORS (允许跨域，配置见下文 corsConfigurationSource)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 设置 Session 管理为无状态 (Stateless)
                // 这意味着服务器不会创建 Session，每次请求都必须带 Token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置异常处理
                // 当用户未携带 Token 或 Token 无效时，不跳转登录页，而是返回 401 JSON
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // 配置路径拦截规则
                .authorizeHttpRequests(auth -> auth
                        // 允许静态资源 (如果有 swagger 或 静态图片)
                        .requestMatchers("/images/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 放行登录接口、刷新 Token 接口
                        .requestMatchers("/admin/auth/**").permitAll()

                        // 3. 公共只读接口 (GET 请求) - 所有人(包括游客)可访问
                        .requestMatchers(HttpMethod.GET,
                                "/api/articles/**",
                                "/api/categories/**",
                                "/api/tags/**",
                                "/api/moments/**",
                                "/api/friendlinks/**",
                                "/api/archive/**",
                                "/api/comments/**"
                        ).permitAll()
                        // 评论接口 (post 请求)
                        .requestMatchers(HttpMethod.POST,
                                "/api/comments/**"
                        ).permitAll()

                        // 4. 管理员写操作 (POST/PUT/DELETE) - 只有 ADMIN 角色可访问
                        // 包含了新增(POST)、修改(PUT)、删除(DELETE)、上传(POST)
                        .requestMatchers(HttpMethod.POST, "/articles/**", "/moment/**", "/friendlinks/**", "/upload/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/articles/**", "/moment/**", "/friendlinks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/articles/**", "/moment/**", "/friendlinks/**").hasRole("ADMIN")

                        // 5. 后台管理页面通用拦截 (兜底策略)
                        // 只要是 /admin 开头的其他路径，都必须是管理员
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 将我们自定义的 JwtFilter 添加到 UsernamePasswordAuthenticationFilter 之前
                // 这样请求进来会先检查 Header 里的 Token
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);




        return http.build();
    }

    /**
     * 4. CORS 跨域配置源
     * 解决前端 (localhost:3000) 访问后端 (localhost:8080) 时的跨域报错
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的前端域名 (上线时建议修改为具体的域名，开发环境可用 *)
        // 如果 allowCredentials 为 true，这里不能写 "*"，必须写具体域名
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000" ));

        // 允许的方法
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的 Header
        configuration.setAllowedHeaders(List.of("*"));

        // 是否允许携带 Cookie (虽然 JWT 主要放在 Header，但开启这个是个好习惯)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口应用该配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
