package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 考试结果响应
 */
@Data
@Schema(description = "考试结果响应")
public class ExamResultResponse {

    @Schema(description = "考试记录ID")
    private Long recordId;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "及格分")
    private Integer passScore;

    @Schema(description = "是否及格")
    private Boolean isPassed;

    @Schema(description = "正确数")
    private Integer correctCount;

    @Schema(description = "错误数")
    private Integer wrongCount;

    @Schema(description = "用时（秒）")
    private Integer timeSpent;

    @Schema(description = "答题详情")
    private List<AnswerDetail> details;

    @Data
    @Schema(description = "答题详情")
    public static class AnswerDetail {
        @Schema(description = "题目ID")
        private Long questionId;

        @Schema(description = "用户答案")
        private String userAnswer;

        @Schema(description = "正确答案")
        private String correctAnswer;

        @Schema(description = "是否正确")
        private Boolean isCorrect;

        @Schema(description = "得分")
        private Integer score;
    }
}
