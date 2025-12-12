package com.studyapp.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 视频播放响应
 */
@Data
@Schema(description = "视频播放响应")
public class VideoPlayResponse {

    @Schema(description = "视频ID")
    private Long id;

    @Schema(description = "视频标题")
    private String title;

    @Schema(description = "视频播放地址（带签名的临时URL）")
    private String playUrl;

    @Schema(description = "时长（秒）")
    private Integer duration;

    @Schema(description = "上次播放进度（秒）")
    private Integer lastProgress;
}
