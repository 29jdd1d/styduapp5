package com.studyapp.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {

    /**
     * 所属专业ID
     */
    private Long majorId;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 封面图
     */
    private String cover;

    /**
     * 课程简介
     */
    private String description;

    /**
     * 讲师名称
     */
    private String teacherName;

    /**
     * 讲师头像
     */
    private String teacherAvatar;

    /**
     * 是否免费 0否 1是
     */
    private Integer isFree;

    /**
     * 观看次数
     */
    private Integer viewCount;

    /**
     * 章节数量
     */
    private Integer chapterCount;

    /**
     * 视频数量
     */
    private Integer videoCount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0下架 1上架
     */
    private Integer status;
}
