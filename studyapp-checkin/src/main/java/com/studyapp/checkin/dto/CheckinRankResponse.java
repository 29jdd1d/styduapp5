package com.studyapp.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 打卡排行响应
 */
@Data
@Schema(description = "打卡排行响应")
public class CheckinRankResponse {

    @Schema(description = "排名")
    private Integer rank;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "连续打卡天数")
    private Integer streak;

    @Schema(description = "总打卡天数")
    private Integer totalDays;
}
