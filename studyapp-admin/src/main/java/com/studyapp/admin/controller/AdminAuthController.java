package com.studyapp.admin.controller;

import com.studyapp.admin.dto.*;
import com.studyapp.admin.service.AdminAuthService;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证控制器
 */
@Tag(name = "管理员认证接口")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                            HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        return Result.success(adminAuthService.login(request, ip));
    }

    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/info")
    public Result<AdminLoginResponse> getCurrentAdmin() {
        Long adminId = UserContext.getAdminId();
        return Result.success(adminAuthService.getCurrentAdmin(adminId));
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword) {
        Long adminId = UserContext.getAdminId();
        adminAuthService.changePassword(adminId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 清除上下文
        UserContext.clear();
        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
