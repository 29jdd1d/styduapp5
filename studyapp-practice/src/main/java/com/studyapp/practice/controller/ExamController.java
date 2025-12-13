package com.studyapp.practice.controller;

import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.practice.dto.*;
import com.studyapp.practice.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 考试控制器
 * 提供考研模拟考试相关功能，包括试卷列表、开始考试、提交考试、查看考试记录等
 */
@Tag(name = "考试接口", description = "考研模拟考试相关接口，支持真题试卷和模拟试卷的考试功能")
@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @Operation(
            summary = "获取试卷列表",
            description = "根据专业和试卷类型分页查询可用的试卷列表。支持筛选真题试卷和模拟试卷，返回试卷的基本信息和用户作答情况"
    )
    @GetMapping("/list")
    public Result<PageResult<ExamListResponse>> getExamList(
            @Parameter(description = "专业ID，用于筛选对应专业的试卷", required = true, example = "1")
            @RequestParam(name = "majorId") Long majorId,
            @Parameter(description = "试卷类型：1-真题试卷，2-模拟试卷。不传则查询全部类型", example = "1")
            @RequestParam(name = "type", required = false) Integer type,
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.getExamList(userId, majorId, type, pageNum, pageSize));
    }

    @Operation(
            summary = "开始考试",
            description = "开始一场模拟考试。系统会创建考试记录，并返回试卷的所有题目。考试开始后会记录开始时间，用于计算考试用时"
    )
    @PostMapping("/start/{examId}")
    public Result<PracticeQuestionResponse> startExam(
            @Parameter(description = "试卷ID", required = true, example = "1")
            @PathVariable Long examId) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.startExam(userId, examId));
    }

    @Operation(
            summary = "提交考试",
            description = "提交整场考试的所有答案。系统会批改试卷，计算得分，并返回考试结果。提交后考试记录状态变为已完成"
    )
    @PostMapping("/submit")
    public Result<ExamResultResponse> submitExam(
            @Parameter(description = "提交考试请求参数，包含考试记录ID和所有题目的答案")
            @Valid @RequestBody SubmitExamRequest request) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.submitExam(userId, request));
    }

    @Operation(
            summary = "获取考试记录",
            description = "分页查询当前用户的历史考试记录。返回每次考试的时间、得分、用时等信息，按考试时间倒序排列"
    )
    @GetMapping("/records")
    public Result<PageResult<ExamResultResponse>> getExamRecords(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量，默认10条", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.getExamRecords(userId, pageNum, pageSize));
    }

    @Operation(
            summary = "获取考试结果详情",
            description = "获取指定考试记录的详细结果。包括总分、正确率、每道题的作答情况、正确答案和解析等详细信息"
    )
    @GetMapping("/result/{recordId}")
    public Result<ExamResultResponse> getExamResult(
            @Parameter(description = "考试记录ID", required = true, example = "1")
            @PathVariable Long recordId) {
        Long userId = UserContext.getUserId();
        return Result.success(examService.getExamResult(userId, recordId));
    }
}
