package com.studyapp.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 专业信息响应
 */
@Data
@Schema(description = "专业信息响应")
public class MajorResponse {

    @Schema(description = "专业ID")
    private Long id;

    @Schema(description = "专业名称")
    private String name;

    @Schema(description = "专业代码")
    private String code;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "描述")
    private String description;
}
