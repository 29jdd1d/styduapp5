package com.studyapp.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 视频响应
 */
@Data
@Schema(description = "视频响应")
public class VideoResponse {

    @Schema(description = "视频ID")
    private Long id;

    @Schema(description = "视频标题")
    private String title;

    @Schema(description = "时长（秒）")
    private Integer duration;

    @Schema(description = "是否试看")
    private Integer isFree;

    @Schema(description = "格式化时长（如 10:30）")
    private String durationText;
}
