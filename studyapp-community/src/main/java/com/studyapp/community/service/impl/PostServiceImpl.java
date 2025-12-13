package com.studyapp.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.community.dto.*;
import com.studyapp.community.entity.Post;
import com.studyapp.community.entity.PostLike;
import com.studyapp.community.feign.UserFeignClient;
import com.studyapp.community.mapper.PostLikeMapper;
import com.studyapp.community.mapper.PostMapper;
import com.studyapp.community.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;
    private final UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Long createPost(Long userId, CreatePostRequest request) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setMajorId(request.getMajorId());
        post.setType(request.getType());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setIsTop(0);
        post.setIsEssence(0);
        post.setStatus(1);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                post.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize images", e);
            }
        }

        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public PageResult<PostListResponse> getPostList(Long majorId, Integer type, String keyword,
                                                     String orderBy, Integer pageNum, Integer pageSize) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1);

        if (majorId != null) {
            wrapper.and(w -> w.eq(Post::getMajorId, majorId).or().isNull(Post::getMajorId));
        }
        if (type != null) {
            wrapper.eq(Post::getType, type);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Post::getTitle, keyword).or().like(Post::getContent, keyword));
        }

        // 排序：置顶优先，然后按指定方式排序
        wrapper.orderByDesc(Post::getIsTop);
        if ("hot".equals(orderBy)) {
            wrapper.orderByDesc(Post::getLikeCount, Post::getCommentCount);
        } else {
            wrapper.orderByDesc(Post::getCreateTime);
        }

        Page<Post> postPage = postMapper.selectPage(page, wrapper);
        return buildPostListResult(postPage);
    }

    @Override
    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }

        // 增加浏览量
        postMapper.incrementViewCount(postId);

        PostDetailResponse response = new PostDetailResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setType(post.getType());
        response.setUserId(post.getUserId());
        response.setViewCount(post.getViewCount() + 1);
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setIsTop(post.getIsTop() == 1);
        response.setIsEssence(post.getIsEssence() == 1);
        response.setCreateTime(post.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // 解析图片
        if (StringUtils.hasText(post.getImages())) {
            try {
                response.setImages(objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                response.setImages(Collections.emptyList());
            }
        }

        // 获取作者信息
        try {
            Result<Map<String, Object>> userResult = userFeignClient.getUserInfo(post.getUserId());
            if (userResult.isSuccess() && userResult.getData() != null) {
                response.setNickname((String) userResult.getData().get("nickname"));
                response.setAvatar((String) userResult.getData().get("avatar"));
            }
        } catch (Exception e) {
            log.warn("Failed to get user info", e);
        }

        // 检查是否点赞
        if (userId != null) {
            response.setIsLiked(isLiked(userId, postId));
        } else {
            response.setIsLiked(false);
        }

        return response;
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此帖子");
        }

        post.setStatus(2);
        postMapper.updateById(post);
    }

    @Override
    @Transactional
    public void likePost(Long userId, Long postId) {
        // 检查是否已点赞
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);
        if (postLikeMapper.selectCount(wrapper) > 0) {
            return;
        }

        PostLike like = new PostLike();
        like.setPostId(postId);
        like.setUserId(userId);
        postLikeMapper.insert(like);

        postMapper.updateLikeCount(postId, 1);
    }

    @Override
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);
        int deleted = postLikeMapper.delete(wrapper);

        if (deleted > 0) {
            postMapper.updateLikeCount(postId, -1);
        }
    }

    @Override
    public Boolean isLiked(Long userId, Long postId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);
        return postLikeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public PageResult<PostListResponse> getMyPosts(Long userId, Integer pageNum, Integer pageSize) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId)
                .ne(Post::getStatus, 2)
                .orderByDesc(Post::getCreateTime);

        Page<Post> postPage = postMapper.selectPage(page, wrapper);
        return buildPostListResult(postPage);
    }

    private PageResult<PostListResponse> buildPostListResult(Page<Post> postPage) {
        if (postPage.getRecords().isEmpty()) {
            return PageResult.of(postPage.getCurrent(), postPage.getSize(), 0L, Collections.emptyList());
        }

        // 获取用户信息
        List<Long> userIds = postPage.getRecords().stream()
                .map(Post::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Map<String, Object>> userMap = new HashMap<>();
        try {
            Result<List<Map<String, Object>>> userResult = userFeignClient.getBatchUserInfo(userIds);
            if (userResult.isSuccess() && userResult.getData() != null) {
                for (Map<String, Object> user : userResult.getData()) {
                    Long id = ((Number) user.get("id")).longValue();
                    userMap.put(id, user);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get batch user info", e);
        }

        List<PostListResponse> list = postPage.getRecords().stream()
                .map(post -> {
                    PostListResponse response = new PostListResponse();
                    response.setId(post.getId());
                    response.setTitle(post.getTitle());
                    response.setSummary(post.getContent().length() > 100 ?
                            post.getContent().substring(0, 100) + "..." : post.getContent());
                    response.setType(post.getType());
                    response.setUserId(post.getUserId());
                    response.setViewCount(post.getViewCount());
                    response.setLikeCount(post.getLikeCount());
                    response.setCommentCount(post.getCommentCount());
                    response.setIsTop(post.getIsTop() == 1);
                    response.setIsEssence(post.getIsEssence() == 1);
                    response.setCreateTime(post.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                    // 封面图
                    if (StringUtils.hasText(post.getImages())) {
                        try {
                            List<String> images = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
                            if (!images.isEmpty()) {
                                response.setCoverImage(images.get(0));
                            }
                        } catch (JsonProcessingException e) {
                            // ignore
                        }
                    }

                    // 用户信息
                    Map<String, Object> user = userMap.get(post.getUserId());
                    if (user != null) {
                        response.setNickname((String) user.get("nickname"));
                        response.setAvatar((String) user.get("avatar"));
                    }

                    return response;
                })
                .collect(Collectors.toList());

        return PageResult.of(postPage.getCurrent(), postPage.getSize(), postPage.getTotal(), list);
    }
}
