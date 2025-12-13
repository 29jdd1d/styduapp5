package com.studyapp.practice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import com.studyapp.practice.dto.*;
import com.studyapp.practice.entity.*;
import com.studyapp.practice.feign.QuestionFeignClient;
import com.studyapp.practice.feign.UserFeignClient;
import com.studyapp.practice.mapper.*;
import com.studyapp.practice.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考试服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final UserExamRecordMapper userExamRecordMapper;
    private final UserExamAnswerMapper userExamAnswerMapper;
    private final QuestionFeignClient questionFeignClient;
    private final UserFeignClient userFeignClient;

    @Override
    public PageResult<ExamListResponse> getExamList(Long userId, Long majorId, Integer type, Integer pageNum, Integer pageSize) {
        Page<Exam> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getMajorId, majorId)
                .eq(Exam::getStatus, 1);

        if (type != null) {
            wrapper.eq(Exam::getType, type);
        }

        wrapper.orderByDesc(Exam::getYear, Exam::getCreateTime);

        Page<Exam> examPage = examMapper.selectPage(page, wrapper);

        // 获取用户已做过的试卷
        LambdaQueryWrapper<UserExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(UserExamRecord::getUserId, userId)
                .eq(UserExamRecord::getStatus, 1);
        List<UserExamRecord> doneRecords = userExamRecordMapper.selectList(recordWrapper);
        Set<Long> doneExamIds = doneRecords.stream()
                .map(UserExamRecord::getExamId)
                .collect(Collectors.toSet());

        List<ExamListResponse> list = examPage.getRecords().stream()
                .map(exam -> {
                    ExamListResponse resp = new ExamListResponse();
                    resp.setId(exam.getId());
                    resp.setTitle(exam.getTitle());
                    resp.setType(exam.getType());
                    resp.setYear(exam.getYear());
                    resp.setQuestionCount(exam.getQuestionCount());
                    resp.setTotalScore(exam.getTotalScore());
                    resp.setDuration(exam.getDuration());
                    resp.setHasDone(doneExamIds.contains(exam.getId()));
                    return resp;
                })
                .collect(Collectors.toList());

        return PageResult.of(examPage.getCurrent(), examPage.getSize(), examPage.getTotal(), list);
    }

    @Override
    @Transactional
    public PracticeQuestionResponse startExam(Long userId, Long examId) {
        // 检查试卷是否存在
        Exam exam = examMapper.selectById(examId);
        if (exam == null || exam.getStatus() != 1) {
            throw new BusinessException("试卷不存在或已下架");
        }

        // 检查是否有未完成的考试
        LambdaQueryWrapper<UserExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserExamRecord::getUserId, userId)
                .eq(UserExamRecord::getExamId, examId)
                .eq(UserExamRecord::getStatus, 0);
        UserExamRecord existingRecord = userExamRecordMapper.selectOne(wrapper);

        if (existingRecord != null) {
            // 返回继续考试
            return getExamQuestions(existingRecord.getId(), examId);
        }

        // 创建新的考试记录
        UserExamRecord record = new UserExamRecord();
        record.setUserId(userId);
        record.setExamId(examId);
        record.setScore(0);
        record.setCorrectCount(0);
        record.setWrongCount(0);
        record.setTimeSpent(0);
        record.setStatus(0);
        record.setStartTime(LocalDateTime.now());
        userExamRecordMapper.insert(record);

        return getExamQuestions(record.getId(), examId);
    }

    @Override
    @Transactional
    public ExamResultResponse submitExam(Long userId, SubmitExamRequest request) {
        // 获取考试记录
        UserExamRecord record = userExamRecordMapper.selectById(request.getRecordId());
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("考试记录不存在");
        }

        if (record.getStatus() == 1) {
            throw new BusinessException("考试已提交");
        }

        // 获取试卷信息
        Exam exam = examMapper.selectById(record.getExamId());

        // 获取试卷题目
        LambdaQueryWrapper<ExamQuestion> eqWrapper = new LambdaQueryWrapper<>();
        eqWrapper.eq(ExamQuestion::getExamId, record.getExamId())
                .orderByAsc(ExamQuestion::getSort);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(eqWrapper);

        Map<Long, ExamQuestion> examQuestionMap = examQuestions.stream()
                .collect(Collectors.toMap(ExamQuestion::getQuestionId, eq -> eq));

        // 获取题目答案
        List<Long> questionIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .collect(Collectors.toList());

        Map<Long, String> correctAnswerMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            Result<List<Map<String, Object>>> questionResult = questionFeignClient.getByIds(questionIds);
            if (questionResult.isSuccess() && questionResult.getData() != null) {
                for (Map<String, Object> q : questionResult.getData()) {
                    Long qId = ((Number) q.get("id")).longValue();
                    String answer = (String) q.get("answer");
                    correctAnswerMap.put(qId, answer);
                }
            }
        }

        // 批改试卷
        int totalScore = 0;
        int correctCount = 0;
        int wrongCount = 0;
        List<ExamResultResponse.AnswerDetail> details = new ArrayList<>();

        Map<Long, String> userAnswerMap = new HashMap<>();
        if (request.getAnswers() != null) {
            for (SubmitExamRequest.AnswerItem answer : request.getAnswers()) {
                userAnswerMap.put(answer.getQuestionId(), answer.getAnswer());
            }
        }

        for (ExamQuestion eq : examQuestions) {
            Long questionId = eq.getQuestionId();
            String userAnswer = userAnswerMap.getOrDefault(questionId, "");
            String correctAnswer = correctAnswerMap.getOrDefault(questionId, "");
            boolean isCorrect = correctAnswer.equalsIgnoreCase(userAnswer.trim());

            int score = isCorrect ? eq.getScore() : 0;
            totalScore += score;

            if (isCorrect) {
                correctCount++;
            } else {
                wrongCount++;
            }

            // 保存答题详情
            UserExamAnswer answer = new UserExamAnswer();
            answer.setRecordId(record.getId());
            answer.setQuestionId(questionId);
            answer.setUserAnswer(userAnswer);
            answer.setIsCorrect(isCorrect ? 1 : 0);
            answer.setScore(score);
            userExamAnswerMapper.insert(answer);

            // 构建详情
            ExamResultResponse.AnswerDetail detail = new ExamResultResponse.AnswerDetail();
            detail.setQuestionId(questionId);
            detail.setUserAnswer(userAnswer);
            detail.setCorrectAnswer(correctAnswer);
            detail.setIsCorrect(isCorrect);
            detail.setScore(score);
            details.add(detail);
        }

        // 更新考试记录
        record.setScore(totalScore);
        record.setCorrectCount(correctCount);
        record.setWrongCount(wrongCount);
        record.setStatus(1);
        record.setSubmitTime(LocalDateTime.now());
        record.setTimeSpent((int) Duration.between(record.getStartTime(), record.getSubmitTime()).getSeconds());
        userExamRecordMapper.updateById(record);

        // 更新用户做题统计
        userFeignClient.incrementTotalQuestions(userId, examQuestions.size());

        // 构建响应
        ExamResultResponse response = new ExamResultResponse();
        response.setRecordId(record.getId());
        response.setScore(totalScore);
        response.setTotalScore(exam.getTotalScore());
        response.setPassScore(exam.getPassScore());
        response.setIsPassed(totalScore >= exam.getPassScore());
        response.setCorrectCount(correctCount);
        response.setWrongCount(wrongCount);
        response.setTimeSpent(record.getTimeSpent());
        response.setDetails(details);

        return response;
    }

    @Override
    public PageResult<ExamResultResponse> getExamRecords(Long userId, Integer pageNum, Integer pageSize) {
        Page<UserExamRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserExamRecord::getUserId, userId)
                .eq(UserExamRecord::getStatus, 1)
                .orderByDesc(UserExamRecord::getSubmitTime);

        Page<UserExamRecord> recordPage = userExamRecordMapper.selectPage(page, wrapper);

        // 获取试卷信息
        List<Long> examIds = recordPage.getRecords().stream()
                .map(UserExamRecord::getExamId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Exam> examMap = new HashMap<>();
        if (!examIds.isEmpty()) {
            LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
            examWrapper.in(Exam::getId, examIds);
            List<Exam> exams = examMapper.selectList(examWrapper);
            examMap = exams.stream().collect(Collectors.toMap(Exam::getId, e -> e));
        }

        Map<Long, Exam> finalExamMap = examMap;
        List<ExamResultResponse> list = recordPage.getRecords().stream()
                .map(record -> {
                    ExamResultResponse resp = new ExamResultResponse();
                    resp.setRecordId(record.getId());
                    resp.setScore(record.getScore());
                    resp.setCorrectCount(record.getCorrectCount());
                    resp.setWrongCount(record.getWrongCount());
                    resp.setTimeSpent(record.getTimeSpent());

                    Exam exam = finalExamMap.get(record.getExamId());
                    if (exam != null) {
                        resp.setTotalScore(exam.getTotalScore());
                        resp.setPassScore(exam.getPassScore());
                        resp.setIsPassed(record.getScore() >= exam.getPassScore());
                    }

                    return resp;
                })
                .collect(Collectors.toList());

        return PageResult.of(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal(), list);
    }

    @Override
    public ExamResultResponse getExamResult(Long userId, Long recordId) {
        UserExamRecord record = userExamRecordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("考试记录不存在");
        }

        Exam exam = examMapper.selectById(record.getExamId());

        // 获取答题详情
        LambdaQueryWrapper<UserExamAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(UserExamAnswer::getRecordId, recordId);
        List<UserExamAnswer> answers = userExamAnswerMapper.selectList(answerWrapper);

        // 获取正确答案
        List<Long> questionIds = answers.stream()
                .map(UserExamAnswer::getQuestionId)
                .collect(Collectors.toList());

        Map<Long, String> correctAnswerMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            Result<List<Map<String, Object>>> questionResult = questionFeignClient.getByIds(questionIds);
            if (questionResult.isSuccess() && questionResult.getData() != null) {
                for (Map<String, Object> q : questionResult.getData()) {
                    Long qId = ((Number) q.get("id")).longValue();
                    String answer = (String) q.get("answer");
                    correctAnswerMap.put(qId, answer);
                }
            }
        }

        List<ExamResultResponse.AnswerDetail> details = answers.stream()
                .map(answer -> {
                    ExamResultResponse.AnswerDetail detail = new ExamResultResponse.AnswerDetail();
                    detail.setQuestionId(answer.getQuestionId());
                    detail.setUserAnswer(answer.getUserAnswer());
                    detail.setCorrectAnswer(correctAnswerMap.get(answer.getQuestionId()));
                    detail.setIsCorrect(answer.getIsCorrect() == 1);
                    detail.setScore(answer.getScore());
                    return detail;
                })
                .collect(Collectors.toList());

        ExamResultResponse response = new ExamResultResponse();
        response.setRecordId(record.getId());
        response.setScore(record.getScore());
        response.setTotalScore(exam.getTotalScore());
        response.setPassScore(exam.getPassScore());
        response.setIsPassed(record.getScore() >= exam.getPassScore());
        response.setCorrectCount(record.getCorrectCount());
        response.setWrongCount(record.getWrongCount());
        response.setTimeSpent(record.getTimeSpent());
        response.setDetails(details);

        return response;
    }

    private PracticeQuestionResponse getExamQuestions(Long recordId, Long examId) {
        // 获取试卷题目
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getSort);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(wrapper);

        List<Long> questionIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .collect(Collectors.toList());

        PracticeQuestionResponse response = new PracticeQuestionResponse();
        response.setRecordId(recordId);

        if (!questionIds.isEmpty()) {
            Result<List<Map<String, Object>>> questionResult = questionFeignClient.getByIds(questionIds);
            if (questionResult.isSuccess() && questionResult.getData() != null) {
                response.setQuestions(convertToQuestionItems(questionResult.getData()));
            }
        }

        return response;
    }

    private List<PracticeQuestionResponse.QuestionItem> convertToQuestionItems(List<Map<String, Object>> questions) {
        return questions.stream().map(q -> {
            PracticeQuestionResponse.QuestionItem item = new PracticeQuestionResponse.QuestionItem();
            item.setId(((Number) q.get("id")).longValue());
            item.setType(((Number) q.get("type")).intValue());
            item.setDifficulty(((Number) q.get("difficulty")).intValue());
            item.setContent((String) q.get("content"));
            item.setOptions(convertToOptions(q.get("options")));
            return item;
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<PracticeQuestionResponse.OptionItem> convertToOptions(Object optionsObj) {
        if (optionsObj == null) return Collections.emptyList();

        List<Map<String, String>> options = (List<Map<String, String>>) optionsObj;
        return options.stream().map(opt -> {
            PracticeQuestionResponse.OptionItem item = new PracticeQuestionResponse.OptionItem();
            item.setKey(opt.get("key"));
            item.setValue(opt.get("value"));
            return item;
        }).collect(Collectors.toList());
    }
}
