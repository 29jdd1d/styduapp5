package com.studyapp.community.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.community.dto.*;
import com.studyapp.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@Tag(name = "评论接口", description = "帖子评论相关接口，包括发表评论、查看评论、点赞评论、删除评论等功能")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "发表评论", description = "对帖子发表评论，支持一级评论和回复其他评论（二级评论），评论内容支持文字")
    @PostMapping
    public Result<Long> createComment(
            @Parameter(description = "创建评论请求参数，包含帖子ID、评论内容、父评论ID（可选）等", required = true)
            @Valid @RequestBody CreateCommentRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(commentService.createComment(userId, request));
    }

    @Operation(summary = "获取评论列表", description = "分页获取指定帖子的评论列表，包含评论者信息、评论内容、点赞数、回复数等，支持嵌套显示回复")
    @GetMapping("/list")
    public Result<PageResult<CommentResponse>> getCommentList(
            @Parameter(description = "帖子ID，获取该帖子下的所有评论", required = true, example = "1")
            @RequestParam(name = "postId") Long postId,
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(commentService.getCommentList(userId, postId, pageNum, pageSize));
    }

    @Operation(summary = "删除评论", description = "删除指定的评论，只有评论作者本人才能删除自己的评论，删除后相关回复也会被删除")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(
            @Parameter(description = "要删除的评论ID", required = true, example = "1")
            @PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        commentService.deleteComment(userId, commentId);
        return Result.success();
    }

    @Operation(summary = "点赞评论", description = "对指定评论进行点赞操作，每个用户对同一评论只能点赞一次")
    @PostMapping("/{commentId}/like")
    public Result<Void> likeComment(
            @Parameter(description = "要点赞的评论ID", required = true, example = "1")
            @PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        commentService.likeComment(userId, commentId);
        return Result.success();
    }

    @Operation(summary = "取消点赞评论", description = "取消对指定评论的点赞，只有已点赞的评论才能取消点赞")
    @DeleteMapping("/{commentId}/like")
    public Result<Void> unlikeComment(
            @Parameter(description = "要取消点赞的评论ID", required = true, example = "1")
            @PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        commentService.unlikeComment(userId, commentId);
        return Result.success();
    }
}
