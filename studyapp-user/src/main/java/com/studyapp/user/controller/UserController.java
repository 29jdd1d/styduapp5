package com.studyapp.user.controller;

import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.user.dto.MajorResponse;
import com.studyapp.user.dto.UserInfoResponse;
import com.studyapp.user.dto.UserUpdateRequest;
import com.studyapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * <p>
 * 提供考研学习小程序用户模块的核心功能，包括用户信息管理和专业选择等功能。
 * </p>
 *
 * @author StudyApp
 * @since 1.0.0
 */
@Tag(name = "用户接口", description = "用户信息管理相关接口，包括获取用户信息、更新用户资料、专业选择等功能")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户的详细信息
     *
     * @return 用户信息响应对象
     */
    @Operation(
            summary = "获取当前用户信息",
            description = "根据当前登录用户的Token获取用户的详细信息，包括用户ID、昵称、头像、性别、考研年份、所选专业等信息。需要用户已登录并携带有效的认证Token。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "用户未登录或Token已过期",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "用户不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo() {
        Long userId = UserContext.getUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    /**
     * 更新当前登录用户的信息
     *
     * @param request 用户更新请求对象
     * @return 操作结果
     */
    @Operation(
            summary = "更新用户信息",
            description = "更新当前登录用户的个人资料信息，支持修改昵称、头像URL、性别、考研目标年份等字段。只更新请求中非空的字段，未传递的字段保持原值不变。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误，如昵称长度超限、性别值非法等",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "用户未登录或Token已过期",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("/update")
    public Result<Void> updateUser(
            @Parameter(
                    description = "用户更新请求对象，包含需要修改的用户信息字段",
                    required = true,
                    schema = @Schema(implementation = UserUpdateRequest.class)
            )
            @RequestBody UserUpdateRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateUser(userId, request);
        return Result.success();
    }

    /**
     * 获取系统中所有可选的考研专业列表
     *
     * @return 专业列表
     */
    @Operation(
            summary = "获取专业列表",
            description = "获取系统中所有可供选择的考研目标专业列表，包含专业ID、专业名称、所属学科门类、专业代码等信息。用户可从列表中选择自己的考研目标专业。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功，返回专业列表",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MajorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "用户未登录或Token已过期",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/major/list")
    public Result<List<MajorResponse>> getMajorList() {
        return Result.success(userService.getMajorList());
    }

    /**
     * 为当前用户选择或更换考研目标专业
     *
     * @param majorId 专业ID
     * @return 操作结果
     */
    @Operation(
            summary = "选择专业",
            description = "为当前登录用户选择或更换考研目标专业。选择专业后，系统将根据所选专业推荐相关的学习课程、题库和资料。如果用户之前已选择过专业，调用此接口将更换为新的目标专业。"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "专业选择成功",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误，如专业ID为空",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "用户未登录或Token已过期",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "指定的专业不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/major/select")
    public Result<Void> selectMajor(
            @Parameter(
                    name = "majorId",
                    description = "要选择的目标专业ID，可通过获取专业列表接口获得",
                    required = true,
                    example = "1"
            )
            @RequestParam(name = "majorId") Long majorId) {
        Long userId = UserContext.getUserId();
        userService.selectMajor(userId, majorId);
        return Result.success();
    }
}
