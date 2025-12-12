package com.studyapp.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论点赞实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment_like")
public class CommentLike extends BaseEntity {

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 用户ID
     */
    private Long userId;
}
