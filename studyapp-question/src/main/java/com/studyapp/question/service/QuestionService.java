package com.studyapp.question.service;

import com.studyapp.question.dto.CategoryTreeResponse;
import com.studyapp.question.dto.QuestionDetailResponse;
import com.studyapp.question.dto.QuestionResponse;
import com.studyapp.question.entity.Question;

import java.util.List;

/**
 * 题库服务接口
 */
public interface QuestionService {

    /**
     * 获取分类树
     *
     * @param majorId 专业ID
     * @return 分类树
     */
    List<CategoryTreeResponse> getCategoryTree(Long majorId);

    /**
     * 获取分类下的题目列表
     *
     * @param categoryId  分类ID
     * @param includeChildren 是否包含子分类
     * @return 题目列表
     */
    List<QuestionResponse> getQuestionList(Long categoryId, Boolean includeChildren);

    /**
     * 获取题目详情
     *
     * @param questionId 题目ID
     * @return 题目详情
     */
    QuestionDetailResponse getQuestionDetail(Long questionId);

    /**
     * 随机获取题目
     *
     * @param majorId    专业ID
     * @param categoryId 分类ID（可选）
     * @param count      数量
     * @return 题目列表
     */
    List<QuestionResponse> getRandomQuestions(Long majorId, Long categoryId, Integer count);

    /**
     * 根据ID列表获取题目
     *
     * @param ids 题目ID列表
     * @return 题目列表
     */
    List<Question> getQuestionsByIds(List<Long> ids);

    /**
     * 获取所有子分类ID（包括自己）
     *
     * @param categoryId 分类ID
     * @return 分类ID列表
     */
    List<Long> getAllChildCategoryIds(Long categoryId);
}
