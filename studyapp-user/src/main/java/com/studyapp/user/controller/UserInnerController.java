package com.studyapp.user.controller;

import com.studyapp.common.result.Result;
import com.studyapp.user.entity.User;
import com.studyapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Result<Map<String, Object>> getByOpenid(@RequestParam String openid) {
        User user = userService.getByOpenid(openid);
        if (user == null) {
            return Result.success(null);
        }
        return Result.success(userService.getUserInfoMap(user));
    }

    @Operation(summary = "创建用户")
    @PostMapping("/create")
    public Result<Map<String, Object>> createUser(
            @RequestParam String openid,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar
    ) {
        User user = userService.createUser(openid, nickname, avatar);
        return Result.success(userService.getUserInfoMap(user));
    }

    @Operation(summary = "增加学习天数")
    @PostMapping("/incrementStudyDays")
    public Result<Void> incrementStudyDays(@RequestParam Long userId) {
        userService.incrementStudyDays(userId);
        return Result.success();
    }

    @Operation(summary = "增加做题数")
    @PostMapping("/incrementTotalQuestions")
    public Result<Void> incrementTotalQuestions(
            @RequestParam Long userId,
            @RequestParam Integer count
    ) {
        userService.incrementTotalQuestions(userId, count);
        return Result.success();
    }

    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/getById")
    public Result<Map<String, Object>> getById(@RequestParam Long userId) {
        User user = userService.getByOpenid(null);
        // 这里需要修改为按ID查询
        return Result.success(null);
    }
}
