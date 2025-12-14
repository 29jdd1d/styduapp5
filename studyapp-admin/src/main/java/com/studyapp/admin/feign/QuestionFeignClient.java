package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 题库服务Feign客户端
 */
@FeignClient(name = "studyapp-question", path = "/question")
public interface QuestionFeignClient {

    /**
     * 获取题目总数
     */
    @GetMapping("/inner/stats/total")
    Result<Long> getTotalCount();

    /**
     * 分页获取题目列表
     */
    @GetMapping("/inner/admin/list")
    Result<Map<String, Object>> getQuestionList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "difficulty", required = false) Integer difficulty,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "majorId", required = false) Long majorId
    );

    /**
     * 获取题目详情
     */
    @GetMapping("/inner/admin/detail/{id}")
    Result<Map<String, Object>> getQuestionDetail(@PathVariable(name = "id") Long id);

    /**
     * 添加题目
     */
    @PostMapping("/inner/admin/question")
    Result<Long> addQuestion(@RequestBody Map<String, Object> question);

    /**
     * 更新题目
     */
    @PutMapping("/inner/admin/question/{id}")
    Result<Void> updateQuestion(@PathVariable(name = "id") Long id, @RequestBody Map<String, Object> question);

    /**
     * 删除题目
     */
    @DeleteMapping("/inner/admin/question/{id}")
    Result<Void> deleteQuestion(@PathVariable(name = "id") Long id);

    /**
     * 批量导入题目
     */
    @PostMapping("/inner/admin/question/import")
    Result<Integer> importQuestions(@RequestBody List<Map<String, Object>> questions);

    /**
     * 获取分类列表(树形结构)
     */
    @GetMapping("/inner/admin/category/list")
    Result<Object> getCategoryList(@RequestParam(name = "majorId", required = false) Long majorId);

    /**
     * 添加分类
     */
    @PostMapping("/inner/admin/category")
    Result<Long> addCategory(@RequestBody Map<String, Object> category);

    /**
     * 更新分类
     */
    @PutMapping("/inner/admin/category/{id}")
    Result<Void> updateCategory(@PathVariable(name = "id") Long id, @RequestBody Map<String, Object> category);

    /**
     * 删除分类
     */
    @DeleteMapping("/inner/admin/category/{id}")
    Result<Void> deleteCategory(@PathVariable(name = "id") Long id);
}
