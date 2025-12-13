package com.studyapp.checkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.checkin.entity.CheckinRecord;
import com.studyapp.checkin.mapper.CheckinRecordMapper;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 打卡统计内部接口
 */
@Tag(name = "打卡统计内部接口")
@RestController
@RequestMapping("/checkin/inner/stats")
@RequiredArgsConstructor
public class CheckinStatsController {

    private final CheckinRecordMapper checkinRecordMapper;

    @Operation(summary = "获取今日打卡人数")
    @GetMapping("/today")
    public Result<Long> getTodayCount() {
        Long count = checkinRecordMapper.selectCount(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getCheckinDate, LocalDate.now()));
        return Result.success(count);
    }
}
