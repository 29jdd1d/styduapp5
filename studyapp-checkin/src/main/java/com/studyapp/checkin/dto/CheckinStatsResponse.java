package com.studyapp.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 打卡统计响应
 */
@Data
@Schema(description = "打卡统计响应")
public class CheckinStatsResponse {

    @Schema(description = "总打卡天数")
    private Integer totalDays;

    @Schema(description = "当前连续天数")
    private Integer currentStreak;

    @Schema(description = "最长连续天数")
    private Integer maxStreak;

    @Schema(description = "总学习时长（分钟）")
    private Integer totalMinutes;

    @Schema(description = "今日是否已打卡")
    private Boolean checkedToday;

    @Schema(description = "本月打卡日期列表")
    private List<String> monthlyCheckins;
}
