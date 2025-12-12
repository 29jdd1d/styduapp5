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
import com.studyapp.practice.service.PracticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 练习服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PracticeServiceImpl implements PracticeService {

    private final PracticeRecordMapper practiceRecordMapper;
    private final PracticeDetailMapper practiceDetailMapper;
    private final UserWrongQuestionMapper userWrongQuestionMapper;
    private final UserFavoriteQuestionMapper userFavoriteQuestionMapper;
    private final QuestionFeignClient questionFeignClient;
    private final UserFeignClient userFeignClient;

    @Override
    @Transactional
    public PracticeQuestionResponse startPractice(Long userId, StartPracticeRequest request) {
        List<Long> questionIds;
        
        switch (request.getMode()) {
            case 3: // 错题模式
                questionIds = userWrongQuestionMapper.selectWrongQuestionIds(userId, request.getCount());
                if (questionIds.isEmpty()) {
                    throw new BusinessException("暂无错题");
                }
                break;
            case 4: // 收藏模式
                questionIds = userFavoriteQuestionMapper.selectFavoriteQuestionIds(userId, request.getCount());
                if (questionIds.isEmpty()) {
                    throw new BusinessException("暂无收藏题目");
                }
                break;
            default:
                // 顺序/随机模式 - 这里简化处理，实际应该调用题库服务获取题目ID
                // TODO: 调用题库服务获取题目列表
                questionIds = new ArrayList<>();
                break;
        }

        // 创建练习记录
        PracticeRecord record = new PracticeRecord();
        record.setUserId(userId);
        record.setMajorId(request.getMajorId());
        record.setCategoryId(request.getCategoryId());
        record.setMode(request.getMode());
        record.setTotalCount(questionIds.size());
        record.setCorrectCount(0);
        record.setWrongCount(0);
        record.setStatus(0);
        record.setStartTime(LocalDateTime.now());
        practiceRecordMapper.insert(record);

        // 获取题目详情
        PracticeQuestionResponse response = new PracticeQuestionResponse();
        response.setRecordId(record.getId());

        if (!questionIds.isEmpty()) {
            Result<List<Map<String, Object>>> questionResult = questionFeignClient.getByIds(questionIds);
            if (questionResult.isSuccess() && questionResult.getData() != null) {
                response.setQuestions(convertToQuestionItems(questionResult.getData()));
            }
        }

        return response;
    }

    @Override
    @Transactional
    public SubmitAnswerResponse submitAnswer(Long userId, SubmitAnswerRequest request) {
        // 获取练习记录
        PracticeRecord record = practiceRecordMapper.selectById(request.getRecordId());
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("练习记录不存在");
        }

        // 获取题目正确答案
        Result<Map<String, Object>> questionResult = questionFeignClient.getDetail(request.getQuestionId());
        if (!questionResult.isSuccess() || questionResult.getData() == null) {
            throw new BusinessException("题目不存在");
        }

        Map<String, Object> question = questionResult.getData();
        String correctAnswer = (String) question.get("answer");
        String analysis = (String) question.get("analysis");

        // 判断是否正确
        boolean isCorrect = correctAnswer.equalsIgnoreCase(request.getAnswer().trim());

        // 保存答题详情
        PracticeDetail detail = new PracticeDetail();
        detail.setRecordId(request.getRecordId());
        detail.setQuestionId(request.getQuestionId());
        detail.setUserAnswer(request.getAnswer());
        detail.setIsCorrect(isCorrect ? 1 : 0);
        detail.setTimeSpent(request.getTimeSpent());
        practiceDetailMapper.insert(detail);

        // 更新练习记录统计
        if (isCorrect) {
            record.setCorrectCount(record.getCorrectCount() + 1);
            // 如果是错题模式且答对，更新错题正确次数
            updateWrongQuestionCorrectCount(userId, request.getQuestionId());
        } else {
            record.setWrongCount(record.getWrongCount() + 1);
            // 加入错题本
            addToWrongQuestions(userId, request.getQuestionId());
        }
        practiceRecordMapper.updateById(record);

        // 更新用户做题统计
        userFeignClient.incrementTotalQuestions(userId, 1);

        // 返回结果
        SubmitAnswerResponse response = new SubmitAnswerResponse();
        response.setIsCorrect(isCorrect);
        response.setCorrectAnswer(correctAnswer);
        response.setAnalysis(analysis);
        return response;
    }

    @Override
    @Transactional
    public PracticeResultResponse finishPractice(Long userId, Long recordId) {
        PracticeRecord record = practiceRecordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("练习记录不存在");
        }

        record.setStatus(1);
        record.setEndTime(LocalDateTime.now());
        practiceRecordMapper.updateById(record);

        return buildPracticeResult(record);
    }

    @Override
    public PageResult<PracticeResultResponse> getPracticeRecords(Long userId, Integer pageNum, Integer pageSize) {
        Page<PracticeRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PracticeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PracticeRecord::getUserId, userId)
                .eq(PracticeRecord::getStatus, 1)
                .orderByDesc(PracticeRecord::getCreateTime);

        Page<PracticeRecord> recordPage = practiceRecordMapper.selectPage(page, wrapper);

        List<PracticeResultResponse> list = recordPage.getRecords().stream()
                .map(this::buildPracticeResult)
                .collect(Collectors.toList());

        return PageResult.of(list, recordPage.getTotal());
    }

    @Override
    public PageResult<WrongQuestionResponse> getWrongQuestions(Long userId, Integer pageNum, Integer pageSize) {
        Page<UserWrongQuestion> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserWrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWrongQuestion::getUserId, userId)
                .eq(UserWrongQuestion::getIsRemoved, 0)
                .orderByDesc(UserWrongQuestion::getLastWrongTime);

        Page<UserWrongQuestion> wrongPage = userWrongQuestionMapper.selectPage(page, wrapper);

        List<Long> questionIds = wrongPage.getRecords().stream()
                .map(UserWrongQuestion::getQuestionId)
                .collect(Collectors.toList());

        if (questionIds.isEmpty()) {
            return PageResult.of(Collections.emptyList(), 0L);
        }

        // 获取题目详情
        Result<List<Map<String, Object>>> questionResult = questionFeignClient.getByIds(questionIds);
        Map<Long, Map<String, Object>> questionMap = new HashMap<>();
        if (questionResult.isSuccess() && questionResult.getData() != null) {
            for (Map<String, Object> q : questionResult.getData()) {
                questionMap.put(((Number) q.get("id")).longValue(), q);
            }
        }

        List<WrongQuestionResponse> list = wrongPage.getRecords().stream()
                .map(wq -> {
                    WrongQuestionResponse resp = new WrongQuestionResponse();
                    resp.setQuestionId(wq.getQuestionId());
                    resp.setWrongCount(wq.getWrongCount());
                    resp.setLastWrongTime(wq.getLastWrongTime() != null ?
                            wq.getLastWrongTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null);

                    Map<String, Object> question = questionMap.get(wq.getQuestionId());
                    if (question != null) {
                        resp.setType(((Number) question.get("type")).intValue());
                        resp.setDifficulty(((Number) question.get("difficulty")).intValue());
                        resp.setContent((String) question.get("content"));
                        resp.setOptions(convertToOptions(question.get("options")));
                    }
                    return resp;
                })
                .collect(Collectors.toList());

        return PageResult.of(list, wrongPage.getTotal());
    }

    @Override
    @Transactional
    public void removeWrongQuestion(Long userId, Long questionId) {
        LambdaQueryWrapper<UserWrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWrongQuestion::getUserId, userId)
                .eq(UserWrongQuestion::getQuestionId, questionId);

        UserWrongQuestion wrongQuestion = userWrongQuestionMapper.selectOne(wrapper);
        if (wrongQuestion != null) {
            wrongQuestion.setIsRemoved(1);
            userWrongQuestionMapper.updateById(wrongQuestion);
        }
    }

    @Override
    @Transactional
    public void favoriteQuestion(Long userId, Long questionId) {
        LambdaQueryWrapper<UserFavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavoriteQuestion::getUserId, userId)
                .eq(UserFavoriteQuestion::getQuestionId, questionId);

        UserFavoriteQuestion existing = userFavoriteQuestionMapper.selectOne(wrapper);
        if (existing == null) {
            UserFavoriteQuestion favorite = new UserFavoriteQuestion();
            favorite.setUserId(userId);
            favorite.setQuestionId(questionId);
            userFavoriteQuestionMapper.insert(favorite);
        }
    }

    @Override
    @Transactional
    public void unfavoriteQuestion(Long userId, Long questionId) {
        LambdaQueryWrapper<UserFavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavoriteQuestion::getUserId, userId)
                .eq(UserFavoriteQuestion::getQuestionId, questionId);
        userFavoriteQuestionMapper.delete(wrapper);
    }

    @Override
    public PageResult<WrongQuestionResponse> getFavoriteQuestions(Long userId, Integer pageNum, Integer pageSize) {
        Page<UserFavoriteQuestion> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserFavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavoriteQuestion::getUserId, userId)
                .orderByDesc(UserFavoriteQuestion::getCreateTime);

        Page<UserFavoriteQuestion> favPage = userFavoriteQuestionMapper.selectPage(page, wrapper);

        List<Long> questionIds = favPage.getRecords().stream()
                .map(UserFavoriteQuestion::getQuestionId)
                .collect(Collectors.toList());

        if (questionIds.isEmpty()) {
            return PageResult.of(Collections.emptyList(), 0L);
        }

        // 获取题目详情
        Result<List<Map<String, Object>>> questionResult = questionFeignClient.getByIds(questionIds);
        Map<Long, Map<String, Object>> questionMap = new HashMap<>();
        if (questionResult.isSuccess() && questionResult.getData() != null) {
            for (Map<String, Object> q : questionResult.getData()) {
                questionMap.put(((Number) q.get("id")).longValue(), q);
            }
        }

        List<WrongQuestionResponse> list = favPage.getRecords().stream()
                .map(fav -> {
                    WrongQuestionResponse resp = new WrongQuestionResponse();
                    resp.setQuestionId(fav.getQuestionId());

                    Map<String, Object> question = questionMap.get(fav.getQuestionId());
                    if (question != null) {
                        resp.setType(((Number) question.get("type")).intValue());
                        resp.setDifficulty(((Number) question.get("difficulty")).intValue());
                        resp.setContent((String) question.get("content"));
                        resp.setOptions(convertToOptions(question.get("options")));
                    }
                    return resp;
                })
                .collect(Collectors.toList());

        return PageResult.of(list, favPage.getTotal());
    }

    @Override
    public Boolean isFavorite(Long userId, Long questionId) {
        LambdaQueryWrapper<UserFavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavoriteQuestion::getUserId, userId)
                .eq(UserFavoriteQuestion::getQuestionId, questionId);
        return userFavoriteQuestionMapper.selectCount(wrapper) > 0;
    }

    private void addToWrongQuestions(Long userId, Long questionId) {
        LambdaQueryWrapper<UserWrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWrongQuestion::getUserId, userId)
                .eq(UserWrongQuestion::getQuestionId, questionId);

        UserWrongQuestion existing = userWrongQuestionMapper.selectOne(wrapper);
        if (existing == null) {
            UserWrongQuestion wrongQuestion = new UserWrongQuestion();
            wrongQuestion.setUserId(userId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setWrongCount(1);
            wrongQuestion.setCorrectCount(0);
            wrongQuestion.setLastWrongTime(LocalDateTime.now());
            wrongQuestion.setIsRemoved(0);
            userWrongQuestionMapper.insert(wrongQuestion);
        } else {
            existing.setWrongCount(existing.getWrongCount() + 1);
            existing.setLastWrongTime(LocalDateTime.now());
            existing.setIsRemoved(0);  // 重新激活
            userWrongQuestionMapper.updateById(existing);
        }
    }

    private void updateWrongQuestionCorrectCount(Long userId, Long questionId) {
        LambdaQueryWrapper<UserWrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWrongQuestion::getUserId, userId)
                .eq(UserWrongQuestion::getQuestionId, questionId);

        UserWrongQuestion existing = userWrongQuestionMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setCorrectCount(existing.getCorrectCount() + 1);
            // 连续答对3次自动移出错题本
            if (existing.getCorrectCount() >= 3) {
                existing.setIsRemoved(1);
            }
            userWrongQuestionMapper.updateById(existing);
        }
    }

    private PracticeResultResponse buildPracticeResult(PracticeRecord record) {
        PracticeResultResponse response = new PracticeResultResponse();
        response.setRecordId(record.getId());
        response.setTotalCount(record.getTotalCount());
        response.setCorrectCount(record.getCorrectCount());
        response.setWrongCount(record.getWrongCount());

        if (record.getTotalCount() > 0) {
            response.setAccuracy((double) record.getCorrectCount() / record.getTotalCount() * 100);
        } else {
            response.setAccuracy(0.0);
        }

        if (record.getStartTime() != null && record.getEndTime() != null) {
            response.setTimeSpent((int) Duration.between(record.getStartTime(), record.getEndTime()).getSeconds());
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
