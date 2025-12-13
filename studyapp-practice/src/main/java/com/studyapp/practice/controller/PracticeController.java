package com.studyapp.practice.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.practice.dto.*;
import com.studyapp.practice.service.PracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 练习控制器
 * 提供考研刷题练习相关功能，包括开始练习、提交答案、错题本、收藏等
 */
@Tag(name = "刷题接口", description = "考研刷题练习相关接口，支持顺序/随机练习模式、错题本管理、题目收藏等功能")
@RestController
@RequestMapping("/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    @Operation(
            summary = "开始练习",
            description = "开始一次新的刷题练习。支持顺序模式和随机模式，可以选择题目分类、数量等参数。系统会根据请求参数生成练习题目并返回第一道题"
    )
    @PostMapping("/start")
    public Result<PracticeQuestionResponse> startPractice(
            @Parameter(description = "开始练习请求参数，包含练习模式、题目分类、题目数量等配置")
            @Valid @RequestBody StartPracticeRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.startPractice(userId, request));
    }

    @Operation(
            summary = "提交答案",
            description = "提交当前题目的答案。系统会判断答案是否正确，记录答题结果，并返回下一道题目（如果有的话）。错误的题目会自动加入错题本"
    )
    @PostMapping("/submit")
    public Result<SubmitAnswerResponse> submitAnswer(
            @Parameter(description = "提交答案请求参数，包含练习记录ID、题目ID、用户选择的答案")
            @Valid @RequestBody SubmitAnswerRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.submitAnswer(userId, request));
    }

    @Operation(
            summary = "结束练习",
            description = "结束当前练习并获取练习结果统计。返回本次练习的正确率、答题数量、用时等统计信息"
    )
    @PostMapping("/finish/{recordId}")
    public Result<PracticeResultResponse> finishPractice(
            @Parameter(description = "练习记录ID", required = true, example = "1")
            @PathVariable Long recordId) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.finishPractice(userId, recordId));
    }

    @Operation(
            summary = "获取练习记录",
            description = "分页查询当前用户的历史练习记录。返回每次练习的时间、题目数量、正确率等信息"
    )
    @GetMapping("/records")
    public Result<PageResult<PracticeResultResponse>> getPracticeRecords(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.getPracticeRecords(userId, pageNum, pageSize));
    }

    @Operation(
            summary = "获取错题本",
            description = "分页查询当前用户的错题列表。错题本记录了用户在练习中答错的题目，方便针对性复习"
    )
    @GetMapping("/wrong")
    public Result<PageResult<WrongQuestionResponse>> getWrongQuestions(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.getWrongQuestions(userId, pageNum, pageSize));
    }

    @Operation(
            summary = "移除错题",
            description = "从错题本中移除指定题目。当用户认为已经掌握该题目时，可以将其从错题本中移除"
    )
    @PostMapping("/wrong/remove/{questionId}")
    public Result<Void> removeWrongQuestion(
            @Parameter(description = "要移除的题目ID", required = true, example = "1")
            @PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        practiceService.removeWrongQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(
            summary = "收藏题目",
            description = "将指定题目添加到收藏夹。用户可以收藏重要或有价值的题目，方便后续复习查看"
    )
    @PostMapping("/favorite/{questionId}")
    public Result<Void> favoriteQuestion(
            @Parameter(description = "要收藏的题目ID", required = true, example = "1")
            @PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        practiceService.favoriteQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(
            summary = "取消收藏",
            description = "将指定题目从收藏夹中移除"
    )
    @DeleteMapping("/favorite/{questionId}")
    public Result<Void> unfavoriteQuestion(
            @Parameter(description = "要取消收藏的题目ID", required = true, example = "1")
            @PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        practiceService.unfavoriteQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(
            summary = "获取收藏列表",
            description = "分页查询当前用户收藏的题目列表。返回用户收藏的所有题目信息"
    )
    @GetMapping("/favorite")
    public Result<PageResult<WrongQuestionResponse>> getFavoriteQuestions(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.getFavoriteQuestions(userId, pageNum, pageSize));
    }

    @Operation(
            summary = "检查是否已收藏",
            description = "检查指定题目是否已被当前用户收藏。用于前端显示收藏状态"
    )
    @GetMapping("/favorite/check/{questionId}")
    public Result<Boolean> isFavorite(
            @Parameter(description = "要检查的题目ID", required = true, example = "1")
            @PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.isFavorite(userId, questionId));
    }
}
