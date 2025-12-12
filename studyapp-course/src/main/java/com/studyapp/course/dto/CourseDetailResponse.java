package com.studyapp.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 课程详情响应
 */
@Data
@Schema(description = "课程详情响应")
public class CourseDetailResponse {

    @Schema(description = "课程ID")
    private Long id;

    @Schema(description = "课程标题")
    private String title;

    @Schema(description = "封面图")
    private String cover;

    @Schema(description = "课程简介")
    private String description;

    @Schema(description = "讲师名称")
    private String teacherName;

    @Schema(description = "讲师头像")
    private String teacherAvatar;

    @Schema(description = "是否免费")
    private Integer isFree;

    @Schema(description = "观看次数")
    private Integer viewCount;

    @Schema(description = "章节列表")
    private List<ChapterResponse> chapters;
}
