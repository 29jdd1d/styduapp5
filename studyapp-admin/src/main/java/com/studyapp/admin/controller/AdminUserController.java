package com.studyapp.admin.controller;

import com.studyapp.admin.feign.UserFeignClient;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserFeignClient userFeignClient;

    /**
     * 分页获取用户列表
     */
    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "majorId", required = false) Long majorId
    ) {
        return userFeignClient.getUserList(page, size, keyword, status, majorId);
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getUserDetail(@PathVariable(name = "id") Long id) {
        return userFeignClient.getUserDetail(id);
    }

    /**
     * 更新用户状态(启用/禁用)
     */
    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    ) {
        return userFeignClient.updateUserStatus(id, status);
    }
}
