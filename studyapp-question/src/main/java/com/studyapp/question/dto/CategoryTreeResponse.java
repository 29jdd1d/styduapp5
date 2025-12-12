package com.studyapp.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分类树响应
 */
@Data
@Schema(description = "分类树响应")
public class CategoryTreeResponse {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "题目数量")
    private Integer questionCount;

    @Schema(description = "子分类")
    private List<CategoryTreeResponse> children;
}
