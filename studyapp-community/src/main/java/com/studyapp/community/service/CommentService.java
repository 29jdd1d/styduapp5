package com.studyapp.community.service;

import com.studyapp.common.result.PageResult;
import com.studyapp.community.dto.*;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 创建评论
     */
    Long createComment(Long userId, CreateCommentRequest request);

    /**
     * 获取评论列表
     */
    PageResult<CommentResponse> getCommentList(Long userId, Long postId, Integer pageNum, Integer pageSize);

    /**
     * 删除评论
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * 点赞评论
     */
    void likeComment(Long userId, Long commentId);

    /**
     * 取消点赞评论
     */
    void unlikeComment(Long userId, Long commentId);
}
