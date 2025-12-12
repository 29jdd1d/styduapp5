package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 练习题目响应
 */
@Data
@Schema(description = "练习题目响应")
public class PracticeQuestionResponse {

    @Schema(description = "练习记录ID")
    private Long recordId;

    @Schema(description = "题目列表")
    private List<QuestionItem> questions;

    @Data
    @Schema(description = "题目项")
    public static class QuestionItem {
        @Schema(description = "题目ID")
        private Long id;

        @Schema(description = "题型")
        private Integer type;

        @Schema(description = "难度")
        private Integer difficulty;

        @Schema(description = "题目内容")
        private String content;

        @Schema(description = "选项")
        private List<OptionItem> options;
    }

    @Data
    @Schema(description = "选项项")
    public static class OptionItem {
        @Schema(description = "选项键")
        private String key;

        @Schema(description = "选项值")
        private String value;
    }
}
