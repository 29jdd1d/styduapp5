package com.studyapp.common.utils;

/**
 * 用户上下文（通过ThreadLocal存储当前用户信息）
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> ADMIN_ID = new ThreadLocal<>();

    /**
     * 设置用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置管理员ID
     */
    public static void setAdminId(Long adminId) {
        ADMIN_ID.set(adminId);
    }

    /**
     * 获取管理员ID
     */
    public static Long getAdminId() {
        return ADMIN_ID.get();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        USER_ID.remove();
        ADMIN_ID.remove();
    }
}
