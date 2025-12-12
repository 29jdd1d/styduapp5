package com.studyapp.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("post")
public class Post extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片（JSON数组）
     */
    private String images;

    /**
     * 专业ID（可空，表示全站）
     */
    private Long majorId;

    /**
     * 类型 1经验分享 2问题求助 3资料分享 4闲聊灌水
     */
    private Integer type;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否置顶 0否 1是
     */
    private Integer isTop;

    /**
     * 是否精华 0否 1是
     */
    private Integer isEssence;

    /**
     * 状态 0待审核 1正常 2已删除
     */
    private Integer status;
}
