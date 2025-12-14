package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "studyapp-user", path = "/user")
public interface UserFeignClient {

    /**
     * 获取用户总数
     */
    @GetMapping("/inner/stats/total")
    Result<Long> getTotalCount();

    /**
     * 获取今日新增用户数
     */
    @GetMapping("/inner/stats/today")
    Result<Long> getTodayNewCount();

    /**
     * 分页获取用户列表
     */
    @GetMapping("/inner/admin/list")
    Result<Map<String, Object>> getUserList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "majorId", required = false) Long majorId
    );

    /**
     * 获取用户详情
     */
    @GetMapping("/inner/admin/detail/{id}")
    Result<Map<String, Object>> getUserDetail(@PathVariable(name = "id") Long id);

    /**
     * 更新用户状态
     */
    @PutMapping("/inner/admin/{id}/status")
    Result<Void> updateUserStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    );

    /**
     * 获取专业列表
     */
    @GetMapping("/inner/admin/major/list")
    Result<Object> getMajorList();

    /**
     * 添加专业
     */
    @PostMapping("/inner/admin/major")
    Result<Void> addMajor(@RequestBody Map<String, Object> major);

    /**
     * 更新专业
     */
    @PutMapping("/inner/admin/major/{id}")
    Result<Void> updateMajor(@PathVariable(name = "id") Long id, @RequestBody Map<String, Object> major);

    /**
     * 删除专业
     */
    @DeleteMapping("/inner/admin/major/{id}")
    Result<Void> deleteMajor(@PathVariable(name = "id") Long id);

    /**
     * 更新专业状态
     */
    @PutMapping("/inner/admin/major/{id}/status")
    Result<Void> updateMajorStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    );
}
