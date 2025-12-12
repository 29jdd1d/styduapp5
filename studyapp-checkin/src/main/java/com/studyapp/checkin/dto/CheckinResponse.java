package com.studyapp.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 打卡响应
 */
@Data
@Schema(description = "打卡响应")
public class CheckinResponse {

    @Schema(description = "是否打卡成功")
    private Boolean success;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "当前连续天数")
    private Integer currentStreak;

    @Schema(description = "总打卡天数")
    private Integer totalDays;

    @Schema(description = "是否为新纪录")
    private Boolean isNewRecord;
}
