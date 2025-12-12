package com.studyapp.checkin.controller;

import com.studyapp.checkin.dto.*;
import com.studyapp.checkin.service.CheckinService;
import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打卡控制器
 */
@Tag(name = "打卡接口")
@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @Operation(summary = "打卡")
    @PostMapping
    public Result<CheckinResponse> checkin(@Valid @RequestBody CheckinRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.checkin(userId, request));
    }

    @Operation(summary = "获取打卡统计")
    @GetMapping("/stats")
    public Result<CheckinStatsResponse> getStats() {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getStats(userId));
    }

    @Operation(summary = "获取打卡日历")
    @GetMapping("/calendar")
    public Result<List<String>> getCalendar(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getCalendar(userId, year, month));
    }

    @Operation(summary = "获取打卡记录")
    @GetMapping("/records")
    public Result<PageResult<CheckinRecordResponse>> getRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getRecords(userId, pageNum, pageSize));
    }

    @Operation(summary = "获取今日打卡")
    @GetMapping("/today")
    public Result<CheckinRecordResponse> getTodayCheckin() {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getTodayCheckin(userId));
    }

    @Operation(summary = "获取连续打卡排行")
    @GetMapping("/ranking/streak")
    public Result<List<CheckinRankResponse>> getStreakRanking(
            @RequestParam(defaultValue = "50") Integer limit) {
        return Result.success(checkinService.getStreakRanking(limit));
    }

    @Operation(summary = "获取总天数排行")
    @GetMapping("/ranking/total")
    public Result<List<CheckinRankResponse>> getTotalDaysRanking(
            @RequestParam(defaultValue = "50") Integer limit) {
        return Result.success(checkinService.getTotalDaysRanking(limit));
    }

    @Operation(summary = "获取我的排名")
    @GetMapping("/ranking/my")
    public Result<Integer> getMyRank(@RequestParam(defaultValue = "streak") String type) {
        Long userId = UserContext.getUserId();
        return Result.success(checkinService.getUserRank(userId, type));
    }
}
