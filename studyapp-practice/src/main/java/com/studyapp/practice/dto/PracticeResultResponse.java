package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 练习结果响应
 */
@Data
@Schema(description = "练习结果响应")
public class PracticeResultResponse {

    @Schema(description = "练习记录ID")
    private Long recordId;

    @Schema(description = "总题数")
    private Integer totalCount;

    @Schema(description = "正确数")
    private Integer correctCount;

    @Schema(description = "错误数")
    private Integer wrongCount;

    @Schema(description = "正确率")
    private Double accuracy;

    @Schema(description = "用时（秒）")
    private Integer timeSpent;
}
