package blog.config;

//import blog.interceptor.LoginInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Web配置类
 * 配置CORS和前端路由支持
 *
 * @author Eleven
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //    @Autowired
//    private LoginInterceptor loginInterceptor;
//
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 配置CORS跨域支持
     */
    @Override
    public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(86400);
    }

//    /**
//     * 配置拦截器
//     */
//    @Override
//    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
//        registry.addInterceptor(loginInterceptor)
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns(
//                    "/admin/login",
//                    "/admin/auth",
//                    "/admin/checkLogin"
//                );
//    }

    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        // 配置静态资源路径（避免与API路径冲突）
        registry.addResourceHandler("/static/**", "/css/**", "/js/**", "/images/**", "/fonts/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/", "classpath:/META-INF/resources/")
                .setCachePeriod(31536000);

        // 配置favicon，确保可以正确访问
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/", "classpath:/public/", "classpath:/META-INF/resources/")
                .setCachePeriod(31536000);

        // 配置webjars（用于Bootstrap、jQuery等前端库）
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(31536000);

        // 让所有以 /upload/ 开头的请求，去你设置的硬盘目录下找文件
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadDir + "/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }

//    /**
//     * 配置视图控制器
//     */
//    @Override
//    public void addViewControllers(@org.springframework.lang.NonNull ViewControllerRegistry registry) {
//        // 管理后台重定向
//        registry.addViewController("/admin").setViewName("redirect:/admin/login");
//    }
}
