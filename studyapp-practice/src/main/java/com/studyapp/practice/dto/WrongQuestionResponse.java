package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 错题本列表响应
 */
@Data
@Schema(description = "错题本响应")
public class WrongQuestionResponse {

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题型")
    private Integer type;

    @Schema(description = "难度")
    private Integer difficulty;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "选项")
    private List<PracticeQuestionResponse.OptionItem> options;

    @Schema(description = "错误次数")
    private Integer wrongCount;

    @Schema(description = "最近错误时间")
    private String lastWrongTime;
}
