package com.studyapp.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户学习进度实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_course_progress")
public class UserCourseProgress extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 当前观看视频ID
     */
    private Long videoId;

    /**
     * 播放进度（秒）
     */
    private Integer progress;

    /**
     * 是否看完 0否 1是
     */
    private Integer isCompleted;
}
