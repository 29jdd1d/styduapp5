package com.studyapp.practice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.common.result.Result;
import com.studyapp.practice.entity.PracticeRecord;
import com.studyapp.practice.mapper.PracticeRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 练习统计内部接口
 */
@Tag(name = "练习统计内部接口")
@RestController
@RequestMapping("/practice/inner/stats")
@RequiredArgsConstructor
public class PracticeStatsController {

    private final PracticeRecordMapper practiceRecordMapper;

    @Operation(summary = "获取今日练习次数")
    @GetMapping("/today")
    public Result<Long> getTodayCount() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long count = practiceRecordMapper.selectCount(new LambdaQueryWrapper<PracticeRecord>()
                .ge(PracticeRecord::getStartTime, todayStart));
        return Result.success(count);
    }
}
