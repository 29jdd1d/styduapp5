package com.studyapp.practice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试卷列表响应
 */
@Data
@Schema(description = "试卷列表响应")
public class ExamListResponse {

    @Schema(description = "试卷ID")
    private Long id;

    @Schema(description = "试卷名称")
    private String title;

    @Schema(description = "类型 1模拟卷 2真题卷")
    private Integer type;

    @Schema(description = "年份")
    private Integer year;

    @Schema(description = "题目数量")
    private Integer questionCount;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "考试时长（分钟）")
    private Integer duration;

    @Schema(description = "是否做过")
    private Boolean hasDone;
}
