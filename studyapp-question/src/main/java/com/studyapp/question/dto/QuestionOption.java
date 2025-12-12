package com.studyapp.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 题目选项
 */
@Data
@Schema(description = "题目选项")
public class QuestionOption {

    @Schema(description = "选项键，如A、B、C、D")
    private String key;

    @Schema(description = "选项内容")
    private String value;
}
