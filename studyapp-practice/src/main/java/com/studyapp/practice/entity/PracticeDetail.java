package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 练习详情实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("practice_detail")
public class PracticeDetail extends BaseEntity {

    /**
     * 练习记录ID
     */
    private Long recordId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 是否正确 0错 1对
     */
    private Integer isCorrect;

    /**
     * 用时（秒）
     */
    private Integer timeSpent;
}
