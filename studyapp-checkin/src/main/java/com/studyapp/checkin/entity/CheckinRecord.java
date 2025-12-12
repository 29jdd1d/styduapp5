package com.studyapp.checkin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 打卡记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_record")
public class CheckinRecord extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 打卡日期
     */
    private LocalDate checkinDate;

    /**
     * 学习时长（分钟）
     */
    private Integer studyMinutes;

    /**
     * 做题数
     */
    private Integer questionCount;

    /**
     * 学习内容描述
     */
    private String content;

    /**
     * 打卡心情 1-5
     */
    private Integer mood;
}
