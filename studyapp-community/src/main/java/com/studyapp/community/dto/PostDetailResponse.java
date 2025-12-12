package com.studyapp.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 帖子详情响应
 */
@Data
@Schema(description = "帖子详情响应")
public class PostDetailResponse {

    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "作者ID")
    private Long userId;

    @Schema(description = "作者昵称")
    private String nickname;

    @Schema(description = "作者头像")
    private String avatar;

    @Schema(description = "浏览数")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "是否精华")
    private Boolean isEssence;

    @Schema(description = "创建时间")
    private String createTime;
}
