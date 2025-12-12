package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交答案请求
 */
@Data
@Schema(description = "提交答案请求")
public class SubmitAnswerRequest {

    @NotNull(message = "练习记录ID不能为空")
    @Schema(description = "练习记录ID", required = true)
    private Long recordId;

    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID", required = true)
    private Long questionId;

    @NotNull(message = "答案不能为空")
    @Schema(description = "用户答案", required = true)
    private String answer;

    @Schema(description = "用时（秒）")
    private Integer timeSpent;
}
