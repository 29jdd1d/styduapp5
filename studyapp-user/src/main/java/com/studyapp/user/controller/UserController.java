package com.studyapp.user.controller;

import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.user.dto.MajorResponse;
import com.studyapp.user.dto.UserInfoResponse;
import com.studyapp.user.dto.UserUpdateRequest;
import com.studyapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo() {
        Long userId = UserContext.getUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public Result<Void> updateUser(@RequestBody UserUpdateRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateUser(userId, request);
        return Result.success();
    }

    @Operation(summary = "获取专业列表")
    @GetMapping("/major/list")
    public Result<List<MajorResponse>> getMajorList() {
        return Result.success(userService.getMajorList());
    }

    @Operation(summary = "选择专业")
    @PostMapping("/major/select")
    public Result<Void> selectMajor(@RequestParam Long majorId) {
        Long userId = UserContext.getUserId();
        userService.selectMajor(userId, majorId);
        return Result.success();
    }
}
