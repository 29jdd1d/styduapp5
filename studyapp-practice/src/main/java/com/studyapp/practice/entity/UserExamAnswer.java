package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户考试答题详情实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_exam_answer")
public class UserExamAnswer extends BaseEntity {

    /**
     * 考试记录ID
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
     * 得分
     */
    private Integer score;
}
