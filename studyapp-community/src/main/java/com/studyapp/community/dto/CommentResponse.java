package com.studyapp.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 评论响应
 */
@Data
@Schema(description = "评论响应")
public class CommentResponse {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "回复的用户昵称")
    private String replyNickname;

    @Schema(description = "子评论列表")
    private List<CommentResponse> children;
}
