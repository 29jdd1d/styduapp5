package com.studyapp.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 保存学习进度请求
 */
@Data
@Schema(description = "保存学习进度请求")
public class SaveProgressRequest {

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true)
    private Long courseId;

    @NotNull(message = "视频ID不能为空")
    @Schema(description = "视频ID", required = true)
    private Long videoId;

    @NotNull(message = "播放进度不能为空")
    @Schema(description = "播放进度（秒）", required = true)
    private Integer progress;

    @Schema(description = "是否看完")
    private Boolean isCompleted;
}
