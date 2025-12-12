package com.studyapp.admin.service;

import com.studyapp.admin.dto.*;

/**
 * 管理员认证服务接口
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     */
    AdminLoginResponse login(AdminLoginRequest request, String ip);

    /**
     * 获取当前管理员信息
     */
    AdminLoginResponse getCurrentAdmin(Long adminId);

    /**
     * 修改密码
     */
    void changePassword(Long adminId, String oldPassword, String newPassword);
}
