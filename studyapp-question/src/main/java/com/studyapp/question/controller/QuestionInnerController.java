package com.studyapp.question.controller;

import com.studyapp.common.result.Result;
import com.studyapp.question.dto.QuestionDetailResponse;
import com.studyapp.question.entity.Question;
import com.studyapp.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题库内部调用控制器
 */
@Tag(name = "题库内部接口")
@RestController
@RequestMapping("/question/inner")
@RequiredArgsConstructor
public class QuestionInnerController {

    private final QuestionService questionService;

    @Operation(summary = "根据ID列表获取题目")
    @PostMapping("/getByIds")
    public Result<List<Question>> getByIds(@RequestBody List<Long> ids) {
        return Result.success(questionService.getQuestionsByIds(ids));
    }

    @Operation(summary = "获取题目详情")
    @GetMapping("/detail")
    public Result<QuestionDetailResponse> getDetail(@RequestParam Long id) {
        return Result.success(questionService.getQuestionDetail(id));
    }

    @Operation(summary = "获取分类的所有子分类ID")
    @GetMapping("/category/childIds")
    public Result<List<Long>> getChildCategoryIds(@RequestParam Long categoryId) {
        return Result.success(questionService.getAllChildCategoryIds(categoryId));
    }

    @Operation(summary = "获取分类下的题目ID列表（顺序）")
    @GetMapping("/listIds")
    public Result<List<Long>> getQuestionIds(@RequestParam Long categoryId,
                                              @RequestParam Integer count) {
        return Result.success(questionService.getQuestionIds(categoryId, count));
    }

    @Operation(summary = "随机获取分类下的题目ID列表")
    @GetMapping("/randomIds")
    public Result<List<Long>> getRandomQuestionIds(@RequestParam Long categoryId,
                                                    @RequestParam Integer count) {
        return Result.success(questionService.getRandomQuestionIds(categoryId, count));
    }
}
