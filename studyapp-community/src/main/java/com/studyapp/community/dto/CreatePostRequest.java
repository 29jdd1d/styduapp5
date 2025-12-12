package com.studyapp.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建帖子请求
 */
@Data
@Schema(description = "创建帖子请求")
public class CreatePostRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过100字")
    @Schema(description = "标题", required = true)
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(max = 10000, message = "内容不能超过10000字")
    @Schema(description = "内容", required = true)
    private String content;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "专业ID（不传则全站可见）")
    private Long majorId;

    @NotNull(message = "类型不能为空")
    @Schema(description = "类型 1经验分享 2问题求助 3资料分享 4闲聊灌水", required = true)
    private Integer type;
}
