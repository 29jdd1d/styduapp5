package com.studyapp.auth.controller;

import com.studyapp.auth.dto.LoginResponse;
import com.studyapp.auth.dto.WxLoginRequest;
import com.studyapp.auth.service.AuthService;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "微信登录")
    @PostMapping("/wx/login")
    public Result<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        return Result.success(authService.wxLogin(request));
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/token/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String token) {
        return Result.success(authService.refreshToken(token));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Result.success();
    }
}
