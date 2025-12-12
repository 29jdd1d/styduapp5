package com.studyapp.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学习进度响应
 */
@Data
@Schema(description = "学习进度响应")
public class ProgressResponse {

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "当前视频ID")
    private Long videoId;

    @Schema(description = "播放进度（秒）")
    private Integer progress;

    @Schema(description = "是否看完")
    private Integer isCompleted;
}
