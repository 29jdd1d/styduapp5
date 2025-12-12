package com.studyapp.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 题目响应
 */
@Data
@Schema(description = "题目响应")
public class QuestionResponse {

    @Schema(description = "题目ID")
    private Long id;

    @Schema(description = "题型 1单选 2多选 3判断 4填空 5简答")
    private Integer type;

    @Schema(description = "难度 1简单 2中等 3困难")
    private Integer difficulty;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "选项列表")
    private List<QuestionOption> options;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "年份")
    private Integer year;
}
