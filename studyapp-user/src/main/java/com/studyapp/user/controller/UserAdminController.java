package com.studyapp.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyapp.common.result.Result;
import com.studyapp.user.entity.Major;
import com.studyapp.user.entity.User;
import com.studyapp.user.mapper.MajorMapper;
import com.studyapp.user.mapper.UserMapper;
import com.studyapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务管理接口（供Admin服务调用）
 */
@Tag(name = "用户管理内部接口")
@RestController
@RequestMapping("/user/inner/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserMapper userMapper;
    private final MajorMapper majorMapper;
    private final UserService userService;

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
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (majorId != null) {
            wrapper.eq(User::getMajorId, majorId);
        }
        wrapper.orderByDesc(User::getCreateTime);
        
        IPage<User> pageResult = userMapper.selectPage(new Page<>(page, size), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("list", pageResult.getRecords().stream().map(userService::getUserInfoMap).toList());
        
        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getUserDetail(@PathVariable(name = "id") Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Map<String, Object> userInfo = userService.getUserInfoMap(user);
        
        // 添加专业信息
        if (user.getMajorId() != null) {
            Major major = majorMapper.selectById(user.getMajorId());
            if (major != null) {
                userInfo.put("majorName", major.getName());
            }
        }
        
        return Result.success(userInfo);
    }

    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    ) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
        
        return Result.success();
    }

    /**
     * 获取专业列表
     */
    @Operation(summary = "获取专业列表")
    @GetMapping("/major/list")
    public Result<List<Major>> getMajorList() {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Major::getSort);
        List<Major> majors = majorMapper.selectList(wrapper);
        return Result.success(majors);
    }

    /**
     * 添加专业
     */
    @Operation(summary = "添加专业")
    @PostMapping("/major")
    public Result<Void> addMajor(@RequestBody Map<String, Object> majorData) {
        Major major = new Major();
        major.setName((String) majorData.get("name"));
        major.setCode((String) majorData.get("code"));
        major.setCategory((String) majorData.get("category"));
        major.setIcon((String) majorData.get("icon"));
        major.setDescription((String) majorData.get("description"));
        major.setSort(majorData.get("sort") != null ? ((Number) majorData.get("sort")).intValue() : 0);
        major.setStatus(majorData.get("status") != null ? ((Number) majorData.get("status")).intValue() : 1);
        
        majorMapper.insert(major);
        return Result.success();
    }

    /**
     * 更新专业
     */
    @Operation(summary = "更新专业")
    @PutMapping("/major/{id}")
    public Result<Void> updateMajor(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> majorData
    ) {
        Major major = majorMapper.selectById(id);
        if (major == null) {
            return Result.error("专业不存在");
        }
        
        if (majorData.containsKey("name")) {
            major.setName((String) majorData.get("name"));
        }
        if (majorData.containsKey("code")) {
            major.setCode((String) majorData.get("code"));
        }
        if (majorData.containsKey("category")) {
            major.setCategory((String) majorData.get("category"));
        }
        if (majorData.containsKey("icon")) {
            major.setIcon((String) majorData.get("icon"));
        }
        if (majorData.containsKey("description")) {
            major.setDescription((String) majorData.get("description"));
        }
        if (majorData.containsKey("sort")) {
            major.setSort(((Number) majorData.get("sort")).intValue());
        }
        if (majorData.containsKey("status")) {
            major.setStatus(((Number) majorData.get("status")).intValue());
        }
        
        majorMapper.updateById(major);
        return Result.success();
    }

    /**
     * 删除专业
     */
    @Operation(summary = "删除专业")
    @DeleteMapping("/major/{id}")
    public Result<Void> deleteMajor(@PathVariable(name = "id") Long id) {
        // 检查是否有用户使用该专业
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getMajorId, id);
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            return Result.error("该专业下有用户，无法删除");
        }
        
        majorMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 更新专业状态
     */
    @Operation(summary = "更新专业状态")
    @PutMapping("/major/{id}/status")
    public Result<Void> updateMajorStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    ) {
        Major major = majorMapper.selectById(id);
        if (major == null) {
            return Result.error("专业不存在");
        }
        
        major.setStatus(status);
        majorMapper.updateById(major);
        
        return Result.success();
    }
}
