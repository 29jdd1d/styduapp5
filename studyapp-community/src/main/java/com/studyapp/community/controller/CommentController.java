package com.studyapp.community.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.community.dto.*;
import com.studyapp.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@Tag(name = "评论接口")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    public Result<Long> createComment(@Valid @RequestBody CreateCommentRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(commentService.createComment(userId, request));
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/list")
    public Result<PageResult<CommentResponse>> getCommentList(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(commentService.getCommentList(userId, postId, pageNum, pageSize));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        commentService.deleteComment(userId, commentId);
        return Result.success();
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/{commentId}/like")
    public Result<Void> likeComment(@PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        commentService.likeComment(userId, commentId);
        return Result.success();
    }

    @Operation(summary = "取消点赞评论")
    @DeleteMapping("/{commentId}/like")
    public Result<Void> unlikeComment(@PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        commentService.unlikeComment(userId, commentId);
        return Result.success();
    }
}
