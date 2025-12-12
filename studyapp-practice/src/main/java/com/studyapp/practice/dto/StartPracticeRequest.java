package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 开始练习请求
 */
@Data
@Schema(description = "开始练习请求")
public class StartPracticeRequest {

    @NotNull(message = "专业ID不能为空")
    @Schema(description = "专业ID", required = true)
    private Long majorId;

    @Schema(description = "分类ID（不传则全专业随机）")
    private Long categoryId;

    @Schema(description = "练习模式 1顺序 2随机 3错题 4收藏", defaultValue = "2")
    private Integer mode = 2;

    @Schema(description = "题目数量", defaultValue = "10")
    private Integer count = 10;
}
