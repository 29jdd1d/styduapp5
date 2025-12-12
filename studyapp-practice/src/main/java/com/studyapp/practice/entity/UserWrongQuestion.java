package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 错题本实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_wrong_question")
public class UserWrongQuestion extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 错误次数
     */
    private Integer wrongCount;

    /**
     * 正确次数（用于自动移出）
     */
    private Integer correctCount;

    /**
     * 最近错误时间
     */
    private LocalDateTime lastWrongTime;

    /**
     * 是否移出 0否 1是
     */
    private Integer isRemoved;
}
