package com.studyapp.community.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.community.dto.*;
import com.studyapp.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子控制器
 */
@Tag(name = "帖子接口", description = "社区帖子相关接口，包括发布、浏览、点赞、删除帖子等功能")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "发布帖子", description = "用户发布新帖子，支持设置标题、内容、专业分类、帖子类型等，可上传图片附件")
    @PostMapping
    public Result<Long> createPost(
            @Parameter(description = "创建帖子请求参数，包含标题、内容、专业ID、帖子类型等", required = true)
            @Valid @RequestBody CreatePostRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(postService.createPost(userId, request));
    }

    @Operation(summary = "获取帖子列表", description = "分页获取帖子列表，支持按专业、类型、关键词筛选，以及按最新或热度排序")
    @GetMapping("/list")
    public Result<PageResult<PostListResponse>> getPostList(
            @Parameter(description = "专业ID，用于筛选特定专业的帖子，不传则显示所有专业")
            @RequestParam(required = false) Long majorId,
            @Parameter(description = "帖子类型：1-经验分享，2-问题求助，3-资料分享，4-日常交流等，不传则显示所有类型")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "搜索关键词，用于模糊匹配帖子标题和内容")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "排序方式：new-最新发布，hot-最热门（按点赞和评论数）", example = "new")
            @RequestParam(defaultValue = "new") String orderBy,
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(postService.getPostList(majorId, type, keyword, orderBy, pageNum, pageSize));
    }

    @Operation(summary = "获取帖子详情", description = "根据帖子ID获取帖子的详细信息，包括标题、内容、作者信息、点赞数、评论数等，同时会增加帖子浏览量")
    @GetMapping("/{postId}")
    public Result<PostDetailResponse> getPostDetail(
            @Parameter(description = "帖子ID", required = true, example = "1")
            @PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        return Result.success(postService.getPostDetail(userId, postId));
    }

    @Operation(summary = "删除帖子", description = "删除指定的帖子，只有帖子作者本人才能删除自己的帖子")
    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(
            @Parameter(description = "要删除的帖子ID", required = true, example = "1")
            @PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        postService.deletePost(userId, postId);
        return Result.success();
    }

    @Operation(summary = "点赞帖子", description = "对指定帖子进行点赞操作，每个用户对同一帖子只能点赞一次")
    @PostMapping("/{postId}/like")
    public Result<Void> likePost(
            @Parameter(description = "要点赞的帖子ID", required = true, example = "1")
            @PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        postService.likePost(userId, postId);
        return Result.success();
    }

    @Operation(summary = "取消点赞帖子", description = "取消对指定帖子的点赞，只有已点赞的帖子才能取消点赞")
    @DeleteMapping("/{postId}/like")
    public Result<Void> unlikePost(
            @Parameter(description = "要取消点赞的帖子ID", required = true, example = "1")
            @PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        postService.unlikePost(userId, postId);
        return Result.success();
    }

    @Operation(summary = "获取我的帖子", description = "分页获取当前登录用户发布的所有帖子列表，按发布时间倒序排列")
    @GetMapping("/my")
    public Result<PageResult<PostListResponse>> getMyPosts(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(postService.getMyPosts(userId, pageNum, pageSize));
    }
}
