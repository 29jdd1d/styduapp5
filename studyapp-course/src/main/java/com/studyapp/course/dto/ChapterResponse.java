package com.studyapp.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 章节响应
 */
@Data
@Schema(description = "章节响应")
public class ChapterResponse {

    @Schema(description = "章节ID")
    private Long id;

    @Schema(description = "章节标题")
    private String title;

    @Schema(description = "视频列表")
    private List<VideoResponse> videos;
}
