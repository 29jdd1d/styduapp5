package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户考试记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_exam_record")
public class UserExamRecord extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 试卷ID
     */
    private Long examId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 正确数
     */
    private Integer correctCount;

    /**
     * 错误数
     */
    private Integer wrongCount;

    /**
     * 用时（秒）
     */
    private Integer timeSpent;

    /**
     * 状态 0进行中 1已交卷
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 交卷时间
     */
    private LocalDateTime submitTime;
}
