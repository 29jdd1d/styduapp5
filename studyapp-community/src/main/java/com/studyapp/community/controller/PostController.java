package com.studyapp.community.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.community.dto.*;
import com.studyapp.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子控制器
 */
@Tag(name = "帖子接口")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "创建帖子")
    @PostMapping
    public Result<Long> createPost(@Valid @RequestBody CreatePostRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(postService.createPost(userId, request));
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/list")
    public Result<PageResult<PostListResponse>> getPostList(
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "new") String orderBy,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(postService.getPostList(majorId, type, keyword, orderBy, pageNum, pageSize));
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/{postId}")
    public Result<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        return Result.success(postService.getPostDetail(userId, postId));
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        postService.deletePost(userId, postId);
        return Result.success();
    }

    @Operation(summary = "点赞帖子")
    @PostMapping("/{postId}/like")
    public Result<Void> likePost(@PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        postService.likePost(userId, postId);
        return Result.success();
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/{postId}/like")
    public Result<Void> unlikePost(@PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        postService.unlikePost(userId, postId);
        return Result.success();
    }

    @Operation(summary = "获取我的帖子")
    @GetMapping("/my")
    public Result<PageResult<PostListResponse>> getMyPosts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(postService.getMyPosts(userId, pageNum, pageSize));
    }
}
