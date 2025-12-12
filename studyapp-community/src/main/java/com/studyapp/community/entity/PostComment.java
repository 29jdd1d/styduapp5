package com.studyapp.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("post_comment")
public class PostComment extends BaseEntity {

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父评论ID（0为一级评论）
     */
    private Long parentId;

    /**
     * 回复的用户ID
     */
    private Long replyUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态 0待审核 1正常 2已删除
     */
    private Integer status;
}
