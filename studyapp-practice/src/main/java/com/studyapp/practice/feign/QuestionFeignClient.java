package com.studyapp.practice.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 题库服务Feign客户端
 */
@FeignClient(name = "studyapp-question", path = "/question")
public interface QuestionFeignClient {

    /**
     * 根据ID列表获取题目
     */
    @PostMapping("/inner/getByIds")
    Result<List<Map<String, Object>>> getByIds(@RequestBody List<Long> ids);

    /**
     * 获取题目详情
     */
    @GetMapping("/inner/detail")
    Result<Map<String, Object>> getDetail(@RequestParam("id") Long id);

    /**
     * 获取分类的所有子分类ID
     */
    @GetMapping("/inner/category/childIds")
    Result<List<Long>> getChildCategoryIds(@RequestParam("categoryId") Long categoryId);

    /**
     * 根据分类获取题目ID列表（顺序模式）
     */
    @GetMapping("/inner/listIds")
    Result<List<Long>> getQuestionIds(@RequestParam("categoryId") Long categoryId,
                                       @RequestParam("count") Integer count);

    /**
     * 根据分类随机获取题目ID列表（随机模式）
     */
    @GetMapping("/inner/randomIds")
    Result<List<Long>> getRandomQuestionIds(@RequestParam("categoryId") Long categoryId,
                                             @RequestParam("count") Integer count);
}
