package com.studyapp.auth.service;

import com.studyapp.auth.dto.LoginResponse;
import com.studyapp.auth.dto.WxLoginRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 微信登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse wxLogin(WxLoginRequest request);

    /**
     * 刷新Token
     *
     * @param oldToken 旧Token
     * @return 新Token
     */
    String refreshToken(String oldToken);

    /**
     * 退出登录
     *
     * @param token Token
     */
    void logout(String token);
}
