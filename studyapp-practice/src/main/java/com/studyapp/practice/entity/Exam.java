package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam")
public class Exam extends BaseEntity {

    /**
     * 试卷名称
     */
    private String title;

    /**
     * 所属专业ID
     */
    private Long majorId;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 及格分
     */
    private Integer passScore;

    /**
     * 考试时长（分钟）
     */
    private Integer duration;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 类型 1模拟卷 2真题卷
     */
    private Integer type;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 状态 0下架 1上架
     */
    private Integer status;
}
