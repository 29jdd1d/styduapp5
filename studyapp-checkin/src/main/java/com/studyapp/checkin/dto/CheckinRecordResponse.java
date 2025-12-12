package com.studyapp.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 打卡记录响应
 */
@Data
@Schema(description = "打卡记录响应")
public class CheckinRecordResponse {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "打卡日期")
    private String checkinDate;

    @Schema(description = "学习时长（分钟）")
    private Integer studyMinutes;

    @Schema(description = "做题数")
    private Integer questionCount;

    @Schema(description = "学习内容")
    private String content;

    @Schema(description = "心情")
    private Integer mood;
}
