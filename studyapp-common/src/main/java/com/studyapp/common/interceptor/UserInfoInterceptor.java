package com.studyapp.common.interceptor;

import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息拦截器（从网关传递的Header中获取用户信息）
 */
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取用户ID
        String userId = request.getHeader(CommonConstants.HEADER_USER_ID);
        if (userId != null && !userId.isEmpty()) {
            UserContext.setUserId(Long.valueOf(userId));
        }

        // 获取管理员ID
        String adminId = request.getHeader(CommonConstants.HEADER_ADMIN_ID);
        if (adminId != null && !adminId.isEmpty()) {
            UserContext.setAdminId(Long.valueOf(adminId));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除ThreadLocal
        UserContext.clear();
    }
}
