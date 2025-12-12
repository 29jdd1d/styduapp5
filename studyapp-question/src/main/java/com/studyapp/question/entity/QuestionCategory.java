package com.studyapp.question.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目分类实体（树形结构）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question_category")
public class QuestionCategory extends BaseEntity {

    /**
     * 所属专业ID
     */
    private Long majorId;

    /**
     * 父分类ID（0表示一级分类）
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 题目数量（冗余字段）
     */
    private Integer questionCount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0禁用 1启用
     */
    private Integer status;
}
