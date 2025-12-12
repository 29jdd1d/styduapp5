package com.studyapp.practice.service;

import com.studyapp.common.result.PageResult;
import com.studyapp.practice.dto.*;

/**
 * 练习服务接口
 */
public interface PracticeService {

    /**
     * 开始练习
     */
    PracticeQuestionResponse startPractice(Long userId, StartPracticeRequest request);

    /**
     * 提交答案
     */
    SubmitAnswerResponse submitAnswer(Long userId, SubmitAnswerRequest request);

    /**
     * 结束练习
     */
    PracticeResultResponse finishPractice(Long userId, Long recordId);

    /**
     * 获取练习记录
     */
    PageResult<PracticeResultResponse> getPracticeRecords(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取错题本
     */
    PageResult<WrongQuestionResponse> getWrongQuestions(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 移除错题
     */
    void removeWrongQuestion(Long userId, Long questionId);

    /**
     * 收藏题目
     */
    void favoriteQuestion(Long userId, Long questionId);

    /**
     * 取消收藏
     */
    void unfavoriteQuestion(Long userId, Long questionId);

    /**
     * 获取收藏列表
     */
    PageResult<WrongQuestionResponse> getFavoriteQuestions(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 是否已收藏
     */
    Boolean isFavorite(Long userId, Long questionId);
}
