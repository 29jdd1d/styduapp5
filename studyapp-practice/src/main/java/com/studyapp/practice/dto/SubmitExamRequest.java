package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 提交考试请求
 */
@Data
@Schema(description = "提交考试请求")
public class SubmitExamRequest {

    @NotNull(message = "考试记录ID不能为空")
    @Schema(description = "考试记录ID", required = true)
    private Long recordId;

    @Schema(description = "答案列表")
    private List<AnswerItem> answers;

    @Data
    @Schema(description = "答案项")
    public static class AnswerItem {
        @Schema(description = "题目ID")
        private Long questionId;

        @Schema(description = "用户答案")
        private String answer;
    }
}
