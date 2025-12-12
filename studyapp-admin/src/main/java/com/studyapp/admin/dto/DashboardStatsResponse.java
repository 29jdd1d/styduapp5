package com.studyapp.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 仪表盘统计响应
 */
@Data
@Schema(description = "仪表盘统计响应")
public class DashboardStatsResponse {

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "今日新增用户")
    private Long todayNewUsers;

    @Schema(description = "总题目数")
    private Long totalQuestions;

    @Schema(description = "总课程数")
    private Long totalCourses;

    @Schema(description = "总帖子数")
    private Long totalPosts;

    @Schema(description = "今日打卡人数")
    private Long todayCheckins;

    @Schema(description = "今日做题数")
    private Long todayPracticeCount;

    @Schema(description = "活跃用户数")
    private Long activeUsers;
}
