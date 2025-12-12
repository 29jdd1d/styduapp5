package com.studyapp.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程章节实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_chapter")
public class CourseChapter extends BaseEntity {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0禁用 1启用
     */
    private Integer status;
}
