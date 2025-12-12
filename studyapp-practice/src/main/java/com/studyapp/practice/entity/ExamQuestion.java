package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷题目关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_question")
public class ExamQuestion extends BaseEntity {

    /**
     * 试卷ID
     */
    private Long examId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 该题分值
     */
    private Integer score;

    /**
     * 排序
     */
    private Integer sort;
}
