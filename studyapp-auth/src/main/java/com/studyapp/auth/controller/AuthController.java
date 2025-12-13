package com.studyapp.auth.controller;

import com.studyapp.auth.dto.LoginResponse;
import com.studyapp.auth.dto.WxLoginRequest;
import com.studyapp.auth.service.AuthService;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * <p>
 * 提供考研学习小程序的用户认证相关接口，包括微信小程序登录、Token刷新和退出登录功能。
 * 所有认证接口都通过JWT Token进行身份验证和授权。
 * </p>
 *
 * @author StudyApp
 * @since 1.0.0
 */
@Tag(name = "认证接口", description = "提供微信小程序登录、Token刷新、退出登录等认证相关功能")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 微信小程序登录
     * <p>
     * 使用微信小程序获取的临时登录凭证code，换取用户身份信息和访问令牌。
     * 如果用户首次登录，系统将自动创建新用户账号。
     * </p>
     *
     * @param request 微信登录请求参数，包含code、昵称和头像
     * @return 登录响应，包含token、用户信息等
     */
    @Operation(
            summary = "微信小程序登录",
            description = "使用微信小程序获取的临时登录凭证code，调用微信服务端接口换取openid，" +
                    "并生成系统访问令牌(JWT Token)。如果是新用户将自动注册，返回用户基本信息和专业选择状态。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "登录成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误，code不能为空",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "服务器内部错误，微信服务调用失败",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/wx/login")
    public Result<LoginResponse> wxLogin(
            @Parameter(
                    description = "微信登录请求参数",
                    required = true,
                    content = @Content(schema = @Schema(implementation = WxLoginRequest.class))
            )
            @Valid @RequestBody WxLoginRequest request) {
        return Result.success(authService.wxLogin(request));
    }

    /**
     * 刷新访问令牌
     * <p>
     * 当访问令牌即将过期时，可使用此接口获取新的访问令牌，延长登录有效期。
     * 需要在请求头中携带当前有效的Token。
     * </p>
     *
     * @param token 当前的访问令牌（Bearer Token格式）
     * @return 新的访问令牌
     */
    @Operation(
            summary = "刷新Token",
            description = "使用当前有效的访问令牌换取新的访问令牌。当Token即将过期时调用此接口可以延长登录状态，" +
                    "避免用户重新登录。新Token生成后，旧Token将在短时间内失效。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "刷新成功，返回新的访问令牌",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token无效或已过期，需要重新登录",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "服务器内部错误",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/token/refresh")
    public Result<String> refreshToken(
            @Parameter(
                    description = "当前的访问令牌，格式为 'Bearer {token}'",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader("Authorization") String token) {
        return Result.success(authService.refreshToken(token));
    }

    /**
     * 退出登录
     * <p>
     * 使当前访问令牌失效，用户需要重新登录才能访问受保护的资源。
     * 建议在用户主动退出或切换账号时调用此接口。
     * </p>
     *
     * @param token 当前的访问令牌（Bearer Token格式）
     * @return 空响应，表示退出成功
     */
    @Operation(
            summary = "退出登录",
            description = "使当前用户的访问令牌失效，清除服务端的登录状态。退出后用户需要重新进行微信登录才能访问系统。" +
                    "此操作会将Token加入黑名单，确保已发放的Token无法继续使用。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "退出成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token无效或已过期",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "服务器内部错误",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/logout")
    public Result<Void> logout(
            @Parameter(
                    description = "当前的访问令牌，格式为 'Bearer {token}'",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Result.success();
    }
}
