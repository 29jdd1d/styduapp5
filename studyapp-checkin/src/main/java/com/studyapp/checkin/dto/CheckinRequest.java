package com.studyapp.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 打卡请求
 */
@Data
@Schema(description = "打卡请求")
public class CheckinRequest {

    @Schema(description = "学习时长（分钟）")
    @Min(value = 0, message = "学习时长不能为负")
    private Integer studyMinutes = 0;

    @Schema(description = "做题数")
    @Min(value = 0, message = "做题数不能为负")
    private Integer questionCount = 0;

    @Schema(description = "学习内容描述")
    private String content;

    @Schema(description = "打卡心情 1-5")
    @Min(value = 1, message = "心情值范围为1-5")
    @Max(value = 5, message = "心情值范围为1-5")
    private Integer mood;
}
