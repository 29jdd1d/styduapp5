package com.studyapp.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专业实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("major")
public class Major extends BaseEntity {

    /**
     * 专业名称
     */
    private String name;

    /**
     * 专业代码
     */
    private String code;

    /**
     * 图标
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0禁用 1启用
     */
    private Integer status;
}
