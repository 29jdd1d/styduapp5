package com.studyapp.question.controller;

import com.studyapp.common.result.Result;
import com.studyapp.question.dto.CategoryTreeResponse;
import com.studyapp.question.dto.QuestionDetailResponse;
import com.studyapp.question.dto.QuestionResponse;
import com.studyapp.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题库控制器
 */
@Tag(name = "题库接口")
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "获取分类树")
    @GetMapping("/category/tree")
    public Result<List<CategoryTreeResponse>> getCategoryTree(@RequestParam Long majorId) {
        return Result.success(questionService.getCategoryTree(majorId));
    }

    @Operation(summary = "获取分类下的题目列表")
    @GetMapping("/list")
    public Result<List<QuestionResponse>> getQuestionList(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "false") Boolean includeChildren
    ) {
        return Result.success(questionService.getQuestionList(categoryId, includeChildren));
    }

    @Operation(summary = "获取题目详情")
    @GetMapping("/detail/{id}")
    public Result<QuestionDetailResponse> getQuestionDetail(@PathVariable Long id) {
        return Result.success(questionService.getQuestionDetail(id));
    }

    @Operation(summary = "随机获取题目")
    @GetMapping("/random")
    public Result<List<QuestionResponse>> getRandomQuestions(
            @RequestParam Long majorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        return Result.success(questionService.getRandomQuestions(majorId, categoryId, count));
    }
}
