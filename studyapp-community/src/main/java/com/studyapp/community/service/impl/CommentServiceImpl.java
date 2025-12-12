package com.studyapp.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.community.dto.*;
import com.studyapp.community.entity.CommentLike;
import com.studyapp.community.entity.Post;
import com.studyapp.community.entity.PostComment;
import com.studyapp.community.feign.UserFeignClient;
import com.studyapp.community.mapper.CommentLikeMapper;
import com.studyapp.community.mapper.PostCommentMapper;
import com.studyapp.community.mapper.PostMapper;
import com.studyapp.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostCommentMapper postCommentMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final PostMapper postMapper;
    private final UserFeignClient userFeignClient;

    @Override
    @Transactional
    public Long createComment(Long userId, CreateCommentRequest request) {
        // 检查帖子
        Post post = postMapper.selectById(request.getPostId());
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }

        PostComment comment = new PostComment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        comment.setReplyUserId(request.getReplyUserId());
        comment.setContent(request.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);
        postCommentMapper.insert(comment);

        // 更新帖子评论数
        postMapper.updateCommentCount(request.getPostId(), 1);

        return comment.getId();
    }

    @Override
    public PageResult<CommentResponse> getCommentList(Long userId, Long postId, Integer pageNum, Integer pageSize) {
        // 获取一级评论
        Page<PostComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PostComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostComment::getPostId, postId)
                .eq(PostComment::getParentId, 0)
                .eq(PostComment::getStatus, 1)
                .orderByDesc(PostComment::getCreateTime);

        Page<PostComment> commentPage = postCommentMapper.selectPage(page, wrapper);

        if (commentPage.getRecords().isEmpty()) {
            return PageResult.of(Collections.emptyList(), 0L);
        }

        // 获取所有一级评论的子评论
        List<Long> parentIds = commentPage.getRecords().stream()
                .map(PostComment::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<PostComment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(PostComment::getParentId, parentIds)
                .eq(PostComment::getStatus, 1)
                .orderByAsc(PostComment::getCreateTime);
        List<PostComment> childComments = postCommentMapper.selectList(childWrapper);

        // 按父ID分组
        Map<Long, List<PostComment>> childMap = childComments.stream()
                .collect(Collectors.groupingBy(PostComment::getParentId));

        // 收集所有用户ID
        Set<Long> userIds = new HashSet<>();
        commentPage.getRecords().forEach(c -> {
            userIds.add(c.getUserId());
            if (c.getReplyUserId() != null) userIds.add(c.getReplyUserId());
        });
        childComments.forEach(c -> {
            userIds.add(c.getUserId());
            if (c.getReplyUserId() != null) userIds.add(c.getReplyUserId());
        });

        // 获取用户信息
        Map<Long, Map<String, Object>> userMap = getUserMap(new ArrayList<>(userIds));

        // 获取当前用户点赞的评论
        Set<Long> likedCommentIds = new HashSet<>();
        if (userId != null) {
            List<Long> allCommentIds = new ArrayList<>(parentIds);
            allCommentIds.addAll(childComments.stream().map(PostComment::getId).collect(Collectors.toList()));

            LambdaQueryWrapper<CommentLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(CommentLike::getUserId, userId)
                    .in(CommentLike::getCommentId, allCommentIds);
            List<CommentLike> likes = commentLikeMapper.selectList(likeWrapper);
            likedCommentIds = likes.stream().map(CommentLike::getCommentId).collect(Collectors.toSet());
        }

        // 构建响应
        Set<Long> finalLikedCommentIds = likedCommentIds;
        List<CommentResponse> list = commentPage.getRecords().stream()
                .map(comment -> {
                    CommentResponse response = buildCommentResponse(comment, userMap, finalLikedCommentIds);

                    // 子评论
                    List<PostComment> children = childMap.get(comment.getId());
                    if (children != null && !children.isEmpty()) {
                        response.setChildren(children.stream()
                                .map(child -> buildCommentResponse(child, userMap, finalLikedCommentIds))
                                .collect(Collectors.toList()));
                    }

                    return response;
                })
                .collect(Collectors.toList());

        return PageResult.of(list, commentPage.getTotal());
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        PostComment comment = postCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }

        comment.setStatus(2);
        postCommentMapper.updateById(comment);

        // 更新帖子评论数
        postMapper.updateCommentCount(comment.getPostId(), -1);
    }

    @Override
    @Transactional
    public void likeComment(Long userId, Long commentId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);
        if (commentLikeMapper.selectCount(wrapper) > 0) {
            return;
        }

        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);
        commentLikeMapper.insert(like);

        postCommentMapper.updateLikeCount(commentId, 1);
    }

    @Override
    @Transactional
    public void unlikeComment(Long userId, Long commentId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);
        int deleted = commentLikeMapper.delete(wrapper);

        if (deleted > 0) {
            postCommentMapper.updateLikeCount(commentId, -1);
        }
    }

    private Map<Long, Map<String, Object>> getUserMap(List<Long> userIds) {
        Map<Long, Map<String, Object>> userMap = new HashMap<>();
        if (userIds.isEmpty()) {
            return userMap;
        }

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
        return userMap;
    }

    private CommentResponse buildCommentResponse(PostComment comment, Map<Long, Map<String, Object>> userMap,
                                                  Set<Long> likedCommentIds) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setUserId(comment.getUserId());
        response.setContent(comment.getContent());
        response.setLikeCount(comment.getLikeCount());
        response.setIsLiked(likedCommentIds.contains(comment.getId()));
        response.setCreateTime(comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        Map<String, Object> user = userMap.get(comment.getUserId());
        if (user != null) {
            response.setNickname((String) user.get("nickname"));
            response.setAvatar((String) user.get("avatar"));
        }

        if (comment.getReplyUserId() != null) {
            Map<String, Object> replyUser = userMap.get(comment.getReplyUserId());
            if (replyUser != null) {
                response.setReplyNickname((String) replyUser.get("nickname"));
            }
        }

        return response;
    }
}
