package com.studyapp.admin.controller;

import com.studyapp.admin.feign.UserFeignClient;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 专业管理控制器
 */
@Tag(name = "专业管理")
@RestController
@RequestMapping("/admin/major")
@RequiredArgsConstructor
public class AdminMajorController {

    private final UserFeignClient userFeignClient;

    /**
     * 获取专业列表
     */
    @Operation(summary = "获取专业列表")
    @GetMapping("/list")
    public Result<Object> getMajorList() {
        return userFeignClient.getMajorList();
    }

    /**
     * 添加专业
     */
    @Operation(summary = "添加专业")
    @PostMapping
    public Result<Void> addMajor(@RequestBody Map<String, Object> major) {
        return userFeignClient.addMajor(major);
    }

    /**
     * 更新专业
     */
    @Operation(summary = "更新专业")
    @PutMapping("/{id}")
    public Result<Void> updateMajor(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> major
    ) {
        return userFeignClient.updateMajor(id, major);
    }

    /**
     * 删除专业
     */
    @Operation(summary = "删除专业")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMajor(@PathVariable(name = "id") Long id) {
        return userFeignClient.deleteMajor(id);
    }

    /**
     * 更新专业状态
     */
    @Operation(summary = "更新专业状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateMajorStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    ) {
        return userFeignClient.updateMajorStatus(id, status);
    }
}
