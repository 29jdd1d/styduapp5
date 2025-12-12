package com.studyapp.practice.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.practice.dto.*;
import com.studyapp.practice.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 考试控制器
 */
@Tag(name = "考试接口")
@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @Operation(summary = "获取试卷列表")
    @GetMapping("/list")
    public Result<PageResult<ExamListResponse>> getExamList(
            @RequestParam Long majorId,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.getExamList(userId, majorId, type, pageNum, pageSize));
    }

    @Operation(summary = "开始考试")
    @PostMapping("/start/{examId}")
    public Result<PracticeQuestionResponse> startExam(@PathVariable Long examId) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.startExam(userId, examId));
    }

    @Operation(summary = "提交考试")
    @PostMapping("/submit")
    public Result<ExamResultResponse> submitExam(@Valid @RequestBody SubmitExamRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.submitExam(userId, request));
    }

    @Operation(summary = "获取考试记录")
    @GetMapping("/records")
    public Result<PageResult<ExamResultResponse>> getExamRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.getExamRecords(userId, pageNum, pageSize));
    }

    @Operation(summary = "获取考试结果详情")
    @GetMapping("/result/{recordId}")
    public Result<ExamResultResponse> getExamResult(@PathVariable Long recordId) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.getExamResult(userId, recordId));
    }
}
