package com.studyapp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.ResultCode;
import com.studyapp.question.dto.*;
import com.studyapp.question.entity.Question;
import com.studyapp.question.entity.QuestionCategory;
import com.studyapp.question.mapper.QuestionCategoryMapper;
import com.studyapp.question.mapper.QuestionMapper;
import com.studyapp.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题库服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionCategoryMapper categoryMapper;
    private final QuestionMapper questionMapper;

    @Override
    public List<CategoryTreeResponse> getCategoryTree(Long majorId) {
        // 获取该专业下的所有分类
        List<QuestionCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<QuestionCategory>()
                        .eq(QuestionCategory::getMajorId, majorId)
                        .eq(QuestionCategory::getStatus, CommonConstants.STATUS_NORMAL)
                        .orderByAsc(QuestionCategory::getSort)
        );

        // 构建树形结构
        return buildCategoryTree(categories, 0L);
    }

    /**
     * 构建分类树
     */
    private List<CategoryTreeResponse> buildCategoryTree(List<QuestionCategory> categories, Long parentId) {
        return categories.stream()
                .filter(c -> c.getParentId().equals(parentId))
                .map(category -> {
                    CategoryTreeResponse response = new CategoryTreeResponse();
                    response.setId(category.getId());
                    response.setName(category.getName());
                    response.setIcon(category.getIcon());
                    response.setDescription(category.getDescription());
                    response.setQuestionCount(category.getQuestionCount());
                    response.setChildren(buildCategoryTree(categories, category.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionList(Long categoryId, Boolean includeChildren) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);

        // 如果包含子分类
        if (Boolean.TRUE.equals(includeChildren)) {
            List<Long> childIds = getAllChildCategoryIds(categoryId);
            categoryIds.addAll(childIds);
        }

        // 查询题目
        List<Question> questions = questionMapper.selectByCategoryIds(categoryIds, null);

        return questions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionDetailResponse getQuestionDetail(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(ResultCode.QUESTION_NOT_FOUND);
        }

        QuestionDetailResponse response = new QuestionDetailResponse();
        BeanUtil.copyProperties(question, response);
        response.setOptions(parseOptions(question.getOptions()));
        return response;
    }

    @Override
    public List<QuestionResponse> getRandomQuestions(Long majorId, Long categoryId, Integer count) {
        List<Long> questionIds;

        if (categoryId != null) {
            // 获取分类及其子分类下的题目
            List<Long> categoryIds = getAllChildCategoryIds(categoryId);
            categoryIds.add(categoryId);

            List<Question> questions = questionMapper.selectByCategoryIds(categoryIds, count * 2);
            // 随机打乱
            java.util.Collections.shuffle(questions);
            questionIds = questions.stream()
                    .limit(count)
                    .map(Question::getId)
                    .collect(Collectors.toList());
        } else {
            // 全专业随机
            questionIds = questionMapper.selectRandomIds(majorId, null, count);
        }

        if (questionIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        return questions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> getQuestionsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return questionMapper.selectBatchIds(ids);
    }

    @Override
    public List<Long> getAllChildCategoryIds(Long categoryId) {
        List<Long> result = new ArrayList<>();

        // 获取所有分类
        QuestionCategory parent = categoryMapper.selectById(categoryId);
        if (parent == null) {
            return result;
        }

        List<QuestionCategory> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<QuestionCategory>()
                        .eq(QuestionCategory::getMajorId, parent.getMajorId())
                        .eq(QuestionCategory::getStatus, CommonConstants.STATUS_NORMAL)
        );

        // 递归查找子分类
        findChildIds(allCategories, categoryId, result);
        return result;
    }

    /**
     * 递归查找子分类ID
     */
    private void findChildIds(List<QuestionCategory> allCategories, Long parentId, List<Long> result) {
        for (QuestionCategory category : allCategories) {
            if (category.getParentId().equals(parentId)) {
                result.add(category.getId());
                findChildIds(allCategories, category.getId(), result);
            }
        }
    }

    /**
     * 转换为响应对象
     */
    private QuestionResponse convertToResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setType(question.getType());
        response.setDifficulty(question.getDifficulty());
        response.setContent(question.getContent());
        response.setOptions(parseOptions(question.getOptions()));
        response.setSource(question.getSource());
        response.setYear(question.getYear());
        return response;
    }

    /**
     * 解析选项JSON
     */
    private List<QuestionOption> parseOptions(String optionsJson) {
        if (optionsJson == null || optionsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            JSONArray jsonArray = JSONUtil.parseArray(optionsJson);
            return jsonArray.stream()
                    .map(obj -> {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        QuestionOption option = new QuestionOption();
                        option.setKey(String.valueOf(map.get("key")));
                        option.setValue(String.valueOf(map.get("value")));
                        return option;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("解析选项JSON失败: {}", optionsJson, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getQuestionIds(Long categoryId, Integer count) {
        // 获取分类及子分类ID
        List<Long> categoryIds = getAllChildCategoryIds(categoryId);
        categoryIds.add(categoryId);

        // 查询题目ID（按顺序）
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Question::getCategoryId, categoryIds)
                .eq(Question::getStatus, CommonConstants.STATUS_NORMAL)
                .orderByAsc(Question::getId)
                .select(Question::getId);

        if (count != null && count > 0) {
            wrapper.last("LIMIT " + count);
        }

        return questionMapper.selectList(wrapper).stream()
                .map(Question::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getRandomQuestionIds(Long categoryId, Integer count) {
        // 获取分类及子分类ID
        List<Long> categoryIds = getAllChildCategoryIds(categoryId);
        categoryIds.add(categoryId);

        // 查询题目ID（随机）
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Question::getCategoryId, categoryIds)
                .eq(Question::getStatus, CommonConstants.STATUS_NORMAL)
                .orderByAsc(Question::getId) // 先排序获取全部
                .select(Question::getId);

        List<Long> allIds = questionMapper.selectList(wrapper).stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // 打乱并取指定数量
        if (allIds.isEmpty()) {
            return new ArrayList<>();
        }

        Collections.shuffle(allIds);
        if (count != null && count > 0 && count < allIds.size()) {
            return allIds.subList(0, count);
        }
        return allIds;
    }
}
