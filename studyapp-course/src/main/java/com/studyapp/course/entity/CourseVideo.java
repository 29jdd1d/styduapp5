package com.studyapp.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程视频实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_video")
public class CourseVideo extends BaseEntity {

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频地址（腾讯云COS）
     */
    private String videoUrl;

    /**
     * 视频key（用于生成签名URL）
     */
    private String videoKey;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 是否试看 0否 1是
     */
    private Integer isFree;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0禁用 1启用
     */
    private Integer status;
}
