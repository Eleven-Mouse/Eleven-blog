//package blog.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
///**
// * 登录拦截器
// * 拦截需要登录才能访问的请求
// *
// * @author Eleven
// * @version 1.0
// */
//@Component
//@Slf4j
//public class LoginInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(@NonNull HttpServletRequest request,
//                            @NonNull HttpServletResponse response,
//                            @NonNull Object handler) throws Exception {
//        HttpSession session = request.getSession();
//        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
//
//        String requestURI = request.getRequestURI();
//        log.debug("拦截请求: {}, 登录状态: {}", requestURI, isLogin);
//
//        // 检查Session是否已登录
//        if (isLogin != null && isLogin) {
//            return true;
//        }
//
//        // 检查Authorization header中的token
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null) {
//            log.debug("检查Authorization header: {}", authHeader);
//            // 简单的token验证逻辑（与AuthController中的逻辑保持一致）
//            if ("admin_token_123456".equals(authHeader)) {
//                // Token有效，设置session
//                session.setAttribute("username", "admin");
//                session.setAttribute("isLogin", true);
//                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7天
//                log.info("通过token验证成功");
//                return true;
//            }
//        }
//
//        // 未登录，判断是否是AJAX请求
//        String requestType = request.getHeader("X-Requested-With");
//        String accept = request.getHeader("Accept");
//
//        if ("XMLHttpRequest".equals(requestType) || (accept != null && accept.contains("application/json"))) {
//            // AJAX请求，返回JSON
//            response.setContentType("application/json;charset=UTF-8");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.getWriter().write("{\"code\":0,\"msg\":\"未登录，请先登录\"}");
//        }
//        else
//        {
//            // 普通请求，重定向到登录页
//            log.info("用户未登录，重定向到登录页");
//            response.sendRedirect("/admin/login?error=unauthorized");
//        }
//
//        return false;
//    }
//}

