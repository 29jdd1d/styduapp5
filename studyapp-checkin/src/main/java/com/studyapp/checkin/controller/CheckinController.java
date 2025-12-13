package com.studyapp.checkin.controller;

import com.studyapp.checkin.dto.*;
import com.studyapp.checkin.service.CheckinService;
import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打卡控制器
 */
@Tag(name = "打卡接口", description = "学习打卡相关接口，包括签到、统计、日历、排行榜等功能")
@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @Operation(summary = "签到打卡", description = "用户进行每日学习打卡签到，记录学习时长和学习内容，支持添加打卡心得")
    @PostMapping
    public Result<CheckinResponse> checkin(
            @Parameter(description = "打卡请求参数，包含学习时长、学习内容等信息", required = true)
            @Valid @RequestBody CheckinRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.checkin(userId, request));
    }

    @Operation(summary = "获取打卡统计", description = "获取当前用户的打卡统计信息，包括总打卡天数、连续打卡天数、累计学习时长等")
    @GetMapping("/stats")
    public Result<CheckinStatsResponse> getStats() {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getStats(userId));
    }

    @Operation(summary = "获取打卡日历", description = "获取指定年月的打卡日历数据，返回该月份中用户已打卡的日期列表")
    @GetMapping("/calendar")
    public Result<List<String>> getCalendar(
            @Parameter(description = "年份，如2025", required = true, example = "2025")
            @RequestParam(name = "year") Integer year,
            @Parameter(description = "月份，1-12", required = true, example = "12")
            @RequestParam(name = "month") Integer month) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getCalendar(userId, year, month));
    }

    @Operation(summary = "获取打卡记录", description = "分页获取当前用户的历史打卡记录列表，按打卡时间倒序排列")
    @GetMapping("/records")
    public Result<PageResult<CheckinRecordResponse>> getRecords(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getRecords(userId, pageNum, pageSize));
    }

    @Operation(summary = "获取今日打卡", description = "获取当前用户今天的打卡记录，如果今日未打卡则返回空")
    @GetMapping("/today")
    public Result<CheckinRecordResponse> getTodayCheckin() {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getTodayCheckin(userId));
    }

    @Operation(summary = "获取连续打卡排行", description = "获取按连续打卡天数排序的排行榜，展示坚持学习最久的用户")
    @GetMapping("/ranking/streak")
    public Result<List<CheckinRankResponse>> getStreakRanking(
            @Parameter(description = "返回排行榜的数量限制，默认50人", example = "50")
            @RequestParam(name = "limit", defaultValue = "50") Integer limit) {
        return Result.success(checkinService.getStreakRanking(limit));
    }

    @Operation(summary = "获取总天数排行", description = "获取按累计打卡总天数排序的排行榜，展示总学习天数最多的用户")
    @GetMapping("/ranking/total")
    public Result<List<CheckinRankResponse>> getTotalDaysRanking(
            @Parameter(description = "返回排行榜的数量限制，默认50人", example = "50")
            @RequestParam(name = "limit", defaultValue = "50") Integer limit) {
        return Result.success(checkinService.getTotalDaysRanking(limit));
    }

    @Operation(summary = "获取我的排名", description = "获取当前用户在排行榜中的排名位置，支持连续打卡排名和总天数排名两种类型")
    @GetMapping("/ranking/my")
    public Result<Integer> getMyRank(
            @Parameter(description = "排名类型：streak-连续打卡排名，total-总天数排名", example = "streak")
            @RequestParam(name = "type", defaultValue = "streak") String type) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getUserRank(userId, type));
    }
}
