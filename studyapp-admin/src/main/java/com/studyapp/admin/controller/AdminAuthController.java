package com.studyapp.admin.controller;

import com.studyapp.admin.dto.*;
import com.studyapp.admin.service.AdminAuthService;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "管理员登录",
            description = "管理员通过用户名和密码进行登录认证，登录成功后返回管理员信息和访问令牌。系统会记录登录IP地址用于安全审计。"
    )
    @PostMapping("/login")
    public Result<AdminLoginResponse> login(
            @Parameter(description = "管理员登录请求参数，包含用户名和密码", required = true)
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        return Result.success(adminAuthService.login(request, ip));
    }

    @Operation(
            summary = "获取当前管理员信息",
            description = "根据当前登录状态获取管理员的详细信息，包括管理员ID、用户名、角色、权限等。需要管理员已登录并携带有效的访问令牌。"
    )
    @GetMapping("/info")
    public Result<AdminLoginResponse> getCurrentAdmin() {
        Long adminId = UserContext.getAdminId();
        return Result.success(adminAuthService.getCurrentAdmin(adminId));
    }

    @Operation(
            summary = "修改密码",
            description = "管理员修改自己的登录密码。需要提供原密码进行身份验证，验证通过后将密码更新为新密码。新密码应符合安全要求。"
    )
    @PostMapping("/password")
    public Result<Void> changePassword(
            @Parameter(description = "原密码，用于验证管理员身份", required = true)
            @RequestParam("oldPassword") String oldPassword,
            @Parameter(description = "新密码，将替换原密码作为新的登录凭证", required = true)
            @RequestParam("newPassword") String newPassword) {
        Long adminId = UserContext.getAdminId();
        adminAuthService.changePassword(adminId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(
            summary = "管理员登出",
            description = "管理员退出登录，清除当前会话的登录状态和用户上下文信息。退出后需要重新登录才能访问管理后台。"
    )
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
