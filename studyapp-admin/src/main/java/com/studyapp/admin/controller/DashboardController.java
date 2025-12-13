package com.studyapp.admin.controller;

import com.studyapp.admin.dto.DashboardStatsResponse;
import com.studyapp.admin.service.DashboardService;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 仪表盘控制器
 */
@Tag(name = "仪表盘接口")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "获取仪表盘统计",
            description = "获取管理后台仪表盘的综合统计数据，包括：用户总数、题目总数、课程总数、帖子总数、今日打卡人数、今日练习次数等关键指标。用于管理员快速了解平台整体运营状况。"
    )
    @GetMapping("/stats")
    public Result<DashboardStatsResponse> getStats() {
        return Result.success(dashboardService.getStats());
    }
}
