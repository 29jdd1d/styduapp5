package com.studyapp.practice.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.practice.dto.*;
import com.studyapp.practice.service.PracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 练习控制器
 */
@Tag(name = "刷题接口")
@RestController
@RequestMapping("/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    @Operation(summary = "开始练习")
    @PostMapping("/start")
    public Result<PracticeQuestionResponse> startPractice(@Valid @RequestBody StartPracticeRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.startPractice(userId, request));
    }

    @Operation(summary = "提交答案")
    @PostMapping("/submit")
    public Result<SubmitAnswerResponse> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.submitAnswer(userId, request));
    }

    @Operation(summary = "结束练习")
    @PostMapping("/finish/{recordId}")
    public Result<PracticeResultResponse> finishPractice(@PathVariable Long recordId) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.finishPractice(userId, recordId));
    }

    @Operation(summary = "获取练习记录")
    @GetMapping("/records")
    public Result<PageResult<PracticeResultResponse>> getPracticeRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.getPracticeRecords(userId, pageNum, pageSize));
    }

    @Operation(summary = "获取错题本")
    @GetMapping("/wrong")
    public Result<PageResult<WrongQuestionResponse>> getWrongQuestions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.getWrongQuestions(userId, pageNum, pageSize));
    }

    @Operation(summary = "移除错题")
    @PostMapping("/wrong/remove/{questionId}")
    public Result<Void> removeWrongQuestion(@PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        practiceService.removeWrongQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(summary = "收藏题目")
    @PostMapping("/favorite/{questionId}")
    public Result<Void> favoriteQuestion(@PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        practiceService.favoriteQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorite/{questionId}")
    public Result<Void> unfavoriteQuestion(@PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        practiceService.unfavoriteQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(summary = "获取收藏列表")
    @GetMapping("/favorite")
    public Result<PageResult<WrongQuestionResponse>> getFavoriteQuestions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.getFavoriteQuestions(userId, pageNum, pageSize));
    }

    @Operation(summary = "是否已收藏")
    @GetMapping("/favorite/check/{questionId}")
    public Result<Boolean> isFavorite(@PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        return Result.success(practiceService.isFavorite(userId, questionId));
    }
}
