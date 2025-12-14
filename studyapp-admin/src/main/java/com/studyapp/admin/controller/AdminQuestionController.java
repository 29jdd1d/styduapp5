package com.studyapp.admin.controller;

import com.studyapp.admin.feign.QuestionFeignClient;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 题库管理控制器
 */
@Tag(name = "题库管理")
@RestController
@RequestMapping("/admin/question")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionFeignClient questionFeignClient;

    /**
     * 分页获取题目列表
     */
    @Operation(summary = "获取题目列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getQuestionList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "difficulty", required = false) Integer difficulty,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "majorId", required = false) Long majorId
    ) {
        return questionFeignClient.getQuestionList(page, size, keyword, type, difficulty, categoryId, majorId);
    }

    /**
     * 获取题目详情
     */
    @Operation(summary = "获取题目详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getQuestionDetail(@PathVariable(name = "id") Long id) {
        return questionFeignClient.getQuestionDetail(id);
    }

    /**
     * 添加题目
     */
    @Operation(summary = "添加题目")
    @PostMapping
    public Result<Long> addQuestion(@RequestBody Map<String, Object> question) {
        return questionFeignClient.addQuestion(question);
    }

    /**
     * 更新题目
     */
    @Operation(summary = "更新题目")
    @PutMapping("/{id}")
    public Result<Void> updateQuestion(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> question
    ) {
        return questionFeignClient.updateQuestion(id, question);
    }

    /**
     * 删除题目
     */
    @Operation(summary = "删除题目")
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestion(@PathVariable(name = "id") Long id) {
        return questionFeignClient.deleteQuestion(id);
    }

    /**
     * 批量导入题目
     */
    @Operation(summary = "批量导入题目")
    @PostMapping("/import")
    public Result<Integer> importQuestions(@RequestBody List<Map<String, Object>> questions) {
        return questionFeignClient.importQuestions(questions);
    }

    /**
     * 获取分类列表(树形结构)
     */
    @Operation(summary = "获取分类列表")
    @GetMapping("/category/list")
    public Result<Object> getCategoryList(
            @RequestParam(name = "majorId", required = false) Long majorId
    ) {
        return questionFeignClient.getCategoryList(majorId);
    }

    /**
     * 添加分类
     */
    @Operation(summary = "添加分类")
    @PostMapping("/category")
    public Result<Long> addCategory(@RequestBody Map<String, Object> category) {
        return questionFeignClient.addCategory(category);
    }

    /**
     * 更新分类
     */
    @Operation(summary = "更新分类")
    @PutMapping("/category/{id}")
    public Result<Void> updateCategory(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> category
    ) {
        return questionFeignClient.updateCategory(id, category);
    }

    /**
     * 删除分类
     */
    @Operation(summary = "删除分类")
    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable(name = "id") Long id) {
        return questionFeignClient.deleteCategory(id);
    }
}
