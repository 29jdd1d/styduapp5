package com.studyapp.checkin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 打卡统计实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_stats")
public class CheckinStats extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总打卡天数
     */
    private Integer totalDays;

    /**
     * 当前连续天数
     */
    private Integer currentStreak;

    /**
     * 最长连续天数
     */
    private Integer maxStreak;

    /**
     * 总学习时长（分钟）
     */
    private Integer totalMinutes;

    /**
     * 最近打卡日期
     */
    private LocalDate lastCheckinDate;
}
