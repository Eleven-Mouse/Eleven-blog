package blog.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API 缓存拦截器：为用户端 GET 请求添加 Cache-Control 头，
 * 使 Vercel 边缘节点和浏览器可以缓存响应，减少跨国回源。
 *
 * 缓存策略：
 * - categories/tags/config：长缓存（浏览器 10min，CDN 1h）
 * - articles 列表 / archive：中缓存（浏览器 3min，CDN 10min）
 * - articles 详情 / comments GET：短缓存（浏览器 1min，CDN 3min）
 * - 写操作（POST/PUT/DELETE）：不缓存
 */
@Component
@Slf4j
public class CacheControlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();

        if (!"GET".equalsIgnoreCase(method)) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            return true;
        }

        String uri = request.getRequestURI();

        // 长缓存：分类、标签、博客配置 — 很少变动
        if (uri.startsWith("/api/categories")
                || uri.startsWith("/api/tags")
                || uri.startsWith("/api/blog")) {
            applyCache(response, 600, 3600, 86400);
            return true;
        }

        // 文章详情：短缓存（包含浏览计数，不宜过长）
        if (uri.matches("/api/articles/\\d+.*")) {
            applyCache(response, 60, 180, 600);
            return true;
        }

        // 文章列表、归档：中缓存
        if (uri.startsWith("/api/articles")
                || uri.startsWith("/api/archive")) {
            applyCache(response, 180, 600, 3600);
            return true;
        }

        // 评论 GET：短缓存
        if (uri.startsWith("/api/comments")) {
            applyCache(response, 60, 180, 600);
            return true;
        }

        return true;
    }

    /**
     * @param maxAge           浏览器缓存时间（秒）
     * @param sMaxage          CDN / 代理缓存时间（秒）
     * @param staleWhileReval  过期后允许返回旧响应的时间（秒）
     */
    private void applyCache(HttpServletResponse response, int maxAge, int sMaxage, int staleWhileReval) {
        response.setHeader("Cache-Control",
                "public, max-age=" + maxAge
                        + ", s-maxage=" + sMaxage
                        + ", stale-while-revalidate=" + staleWhileReval);
        response.setHeader("Vary", "Accept-Encoding");
    }
}
