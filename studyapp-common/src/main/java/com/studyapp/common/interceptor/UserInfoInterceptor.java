package com.studyapp.common.interceptor;

import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.utils.JwtUtils;
import com.studyapp.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息拦截器
 * 优先从网关传递的Header中获取用户信息
 * 如果没有，则尝试从Token中解析（支持直接访问微服务调试）
 */
@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public UserInfoInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        log.info("UserInfoInterceptor 初始化, jwtUtils={}", jwtUtils != null ? "已注入" : "未注入");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        log.info("=== 拦截请求: {} ===", path);
        
        // 优先从网关传递的Header获取用户ID
        String userId = request.getHeader(CommonConstants.HEADER_USER_ID);
        if (userId != null && !userId.isEmpty()) {
            UserContext.setUserId(Long.valueOf(userId));
            log.info("从Header获取userId: {}", userId);
        }

        // 优先从网关传递的Header获取管理员ID
        String adminId = request.getHeader(CommonConstants.HEADER_ADMIN_ID);
        if (adminId != null && !adminId.isEmpty()) {
            UserContext.setAdminId(Long.valueOf(adminId));
            log.info("从Header获取adminId: {}", adminId);
        }

        // 如果Header中没有，尝试从Token解析（支持直接访问微服务调试）
        if (UserContext.getUserId() == null && UserContext.getAdminId() == null) {
            String token = getTokenFromRequest(request);
            log.info("从Authorization获取token: {}", token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");
            if (token != null) {
                parseTokenAndSetContext(token);
            }
        }
        
        log.info("最终 UserContext: userId={}, adminId={}", UserContext.getUserId(), UserContext.getAdminId());

        return true;
    }

    /**
     * 从Token解析用户信息并设置到上下文
     */
    private void parseTokenAndSetContext(String token) {
        // 优先使用注入的 JwtUtils
        if (jwtUtils != null) {
            try {
                boolean isAdmin = jwtUtils.isAdminToken(token);
                log.info("Token类型判断: isAdmin={}", isAdmin);
                
                if (isAdmin) {
                    Long parsedAdminId = jwtUtils.getAdminId(token);
                    log.info("解析adminId: {}", parsedAdminId);
                    if (parsedAdminId != null) {
                        UserContext.setAdminId(parsedAdminId);
                    }
                } else {
                    Long parsedUserId = jwtUtils.getUserId(token);
                    log.info("解析userId: {}", parsedUserId);
                    if (parsedUserId != null) {
                        UserContext.setUserId(parsedUserId);
                    }
                }
            } catch (Exception e) {
                log.error("Token解析失败: {}", e.getMessage(), e);
            }
        } else {
            log.warn("jwtUtils为null，尝试静态方法");
            // 使用静态方法（备用方案）
            try {
                if (jwtUtils.isAdminToken(token)) {
                    Long parsedAdminId = jwtUtils.getAdminId(token);
                    if (parsedAdminId != null) {
                        UserContext.setAdminId(parsedAdminId);
                        log.info("从Token解析到adminId: {}", parsedAdminId);
                    }
                } else {
                    Long parsedUserId = jwtUtils.getUserId(token);
                    if (parsedUserId != null) {
                        UserContext.setUserId(parsedUserId);
                        log.info("从Token解析到userId: {}", parsedUserId);
                    }
                }
            } catch (Exception e) {
                log.error("Token解析失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
