package com.studyapp.user.controller;

import com.studyapp.common.result.Result;
import com.studyapp.user.entity.User;
import com.studyapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内部调用控制器（供其他微服务调用）
 */
@Tag(name = "内部调用接口")
@RestController
@RequestMapping("/user/inner")
@RequiredArgsConstructor
public class UserInnerController {

    private final UserService userService;

    @Operation(summary = "根据openid获取用户")
    @GetMapping("/getByOpenid")
    public Result<Map<String, Object>> getByOpenid(@RequestParam(name = "openid") String openid) {
        User user = userService.getByOpenid(openid);
        if (user == null) {
            return Result.success(null);
        }
        return Result.success(userService.getUserInfoMap(user));
    }

    @Operation(summary = "创建用户")
    @PostMapping("/create")
    public Result<Map<String, Object>> createUser(
            @RequestParam(name = "openid") String openid,
            @RequestParam(name = "nickname", required = false) String nickname,
            @RequestParam(name = "avatar", required = false) String avatar
    ) {
        User user = userService.createUser(openid, nickname, avatar);
        return Result.success(userService.getUserInfoMap(user));
    }

    @Operation(summary = "增加学习天数")
    @PostMapping("/incrementStudyDays")
    public Result<Void> incrementStudyDays(@RequestParam(name = "userId") Long userId) {
        userService.incrementStudyDays(userId);
        return Result.success();
    }

    @Operation(summary = "增加做题数")
    @PostMapping("/incrementTotalQuestions")
    public Result<Void> incrementTotalQuestions(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "count") Integer count
    ) {
        userService.incrementTotalQuestions(userId, count);
        return Result.success();
    }

    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/getById")
    public Result<Map<String, Object>> getById(@RequestParam(name = "userId") Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.success(null);
        }
        return Result.success(userService.getUserInfoMap(user));
    }

    @Operation(summary = "获取用户基本信息（供其他服务调用）")
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam(name = "userId") Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.success(null);
        }
        return Result.success(userService.getUserInfoMap(user));
    }

    @Operation(summary = "批量获取用户信息")
    @GetMapping("/batch")
    public Result<List<Map<String, Object>>> getBatchUserInfo(@RequestParam(name = "userIds") List<Long> userIds) {
        return Result.success(userService.getBatchUserInfoMap(userIds));
    }

    @Operation(summary = "获取用户总数")
    @GetMapping("/stats/total")
    public Result<Long> getTotalCount() {
        return Result.success(userService.getTotalCount());
    }

    @Operation(summary = "获取今日新增用户数")
    @GetMapping("/stats/today")
    public Result<Long> getTodayNewCount() {
        return Result.success(userService.getTodayNewCount());
    }
}
