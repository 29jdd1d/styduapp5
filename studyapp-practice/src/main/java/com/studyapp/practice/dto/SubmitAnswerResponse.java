package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 提交答案响应
 */
@Data
@Schema(description = "提交答案响应")
public class SubmitAnswerResponse {

    @Schema(description = "是否正确")
    private Boolean isCorrect;

    @Schema(description = "正确答案")
    private String correctAnswer;

    @Schema(description = "解析")
    private String analysis;
}
