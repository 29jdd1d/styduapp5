package com.studyapp.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新用户请求
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateRequest {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "考研年份")
    private Integer examYear;
}
