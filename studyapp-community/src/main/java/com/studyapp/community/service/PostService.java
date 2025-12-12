package com.studyapp.community.service;

import com.studyapp.common.result.PageResult;
import com.studyapp.community.dto.*;

/**
 * 帖子服务接口
 */
public interface PostService {

    /**
     * 创建帖子
     */
    Long createPost(Long userId, CreatePostRequest request);

    /**
     * 获取帖子列表
     */
    PageResult<PostListResponse> getPostList(Long majorId, Integer type, String keyword,
                                              String orderBy, Integer pageNum, Integer pageSize);

    /**
     * 获取帖子详情
     */
    PostDetailResponse getPostDetail(Long userId, Long postId);

    /**
     * 删除帖子
     */
    void deletePost(Long userId, Long postId);

    /**
     * 点赞帖子
     */
    void likePost(Long userId, Long postId);

    /**
     * 取消点赞
     */
    void unlikePost(Long userId, Long postId);

    /**
     * 是否已点赞
     */
    Boolean isLiked(Long userId, Long postId);

    /**
     * 获取我的帖子
     */
    PageResult<PostListResponse> getMyPosts(Long userId, Integer pageNum, Integer pageSize);
}
