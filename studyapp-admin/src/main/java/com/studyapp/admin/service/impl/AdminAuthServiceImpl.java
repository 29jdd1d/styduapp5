package com.studyapp.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.admin.dto.*;
import com.studyapp.admin.entity.AdminUser;
import com.studyapp.admin.mapper.AdminUserMapper;
import com.studyapp.admin.service.AdminAuthService;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

/**
 * 管理员认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request, String ip) {
        // 查询管理员
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getUsername, request.getUsername());
        AdminUser admin = adminUserMapper.selectOne(wrapper);

        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (admin.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 验证密码 (MD5加密)
        String encryptedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        if (!encryptedPassword.equals(admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 更新登录信息
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setLastLoginIp(ip);
        adminUserMapper.updateById(admin);

        // 生成Token
        String token = jwtUtils.generateAdminToken(admin.getId(), admin.getUsername());

        AdminLoginResponse response = new AdminLoginResponse();
        response.setToken(token);
        response.setUserId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setNickname(admin.getNickname());
        response.setRole(admin.getRole());

        return response;
    }

    @Override
    public AdminLoginResponse getCurrentAdmin(Long adminId) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        AdminLoginResponse response = new AdminLoginResponse();
        response.setUserId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setNickname(admin.getNickname());
        response.setRole(admin.getRole());

        return response;
    }

    @Override
    @Transactional
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 验证旧密码
        String encryptedOldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!encryptedOldPassword.equals(admin.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        admin.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        adminUserMapper.updateById(admin);
    }
}
