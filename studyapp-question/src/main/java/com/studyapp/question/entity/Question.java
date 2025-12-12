package com.studyapp.question.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question")
public class Question extends BaseEntity {

    /**
     * 所属专业ID
     */
    private Long majorId;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 题型 1单选 2多选 3判断 4填空 5简答
     */
    private Integer type;

    /**
     * 难度 1简单 2中等 3困难
     */
    private Integer difficulty;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 选项（JSON格式）
     */
    private String options;

    /**
     * 答案
     */
    private String answer;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 来源
     */
    private String source;

    /**
     * 年份（真题用）
     */
    private Integer year;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0禁用 1启用
     */
    private Integer status;
}
