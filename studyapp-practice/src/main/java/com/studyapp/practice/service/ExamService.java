package com.studyapp.practice.service;

import com.studyapp.common.result.PageResult;
import com.studyapp.practice.dto.*;

/**
 * 考试服务接口
 */
public interface ExamService {

    /**
     * 获取试卷列表
     */
    PageResult<ExamListResponse> getExamList(Long userId, Long majorId, Integer type, Integer pageNum, Integer pageSize);

    /**
     * 开始考试
     */
    PracticeQuestionResponse startExam(Long userId, Long examId);

    /**
     * 提交考试
     */
    ExamResultResponse submitExam(Long userId, SubmitExamRequest request);

    /**
     * 获取考试记录
     */
    PageResult<ExamResultResponse> getExamRecords(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取考试详情
     */
    ExamResultResponse getExamResult(Long userId, Long recordId);
}
