package com.studyapp.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建评论请求
 */
@Data
@Schema(description = "创建评论请求")
public class CreateCommentRequest {

    @NotNull(message = "帖子ID不能为空")
    @Schema(description = "帖子ID", required = true)
    private Long postId;

    @Schema(description = "父评论ID（回复评论时传）")
    private Long parentId;

    @Schema(description = "回复的用户ID")
    private Long replyUserId;

    @NotBlank(message = "内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    @Schema(description = "评论内容", required = true)
    private String content;
}
