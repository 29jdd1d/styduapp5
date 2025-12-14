package com.studyapp.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyapp.common.result.Result;
import com.studyapp.question.entity.Question;
import com.studyapp.question.entity.QuestionCategory;
import com.studyapp.question.mapper.QuestionCategoryMapper;
import com.studyapp.question.mapper.QuestionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题库服务管理接口（供Admin服务调用）
 */
@Tag(name = "题库管理内部接口")
@RestController
@RequestMapping("/question/inner/admin")
@RequiredArgsConstructor
public class QuestionAdminController {

    private final QuestionMapper questionMapper;
    private final QuestionCategoryMapper categoryMapper;

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
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Question::getContent, keyword);
        }
        if (type != null) {
            wrapper.eq(Question::getType, type);
        }
        if (difficulty != null) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (categoryId != null) {
            wrapper.eq(Question::getCategoryId, categoryId);
        }
        if (majorId != null) {
            wrapper.eq(Question::getMajorId, majorId);
        }
        wrapper.orderByAsc(Question::getSort).orderByDesc(Question::getCreateTime);
        
        IPage<Question> pageResult = questionMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 获取分类信息
        List<Question> questions = pageResult.getRecords();
        List<Long> categoryIds = questions.stream()
                .map(Question::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> categoryNameMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            LambdaQueryWrapper<QuestionCategory> catWrapper = new LambdaQueryWrapper<>();
            catWrapper.in(QuestionCategory::getId, categoryIds);
            List<QuestionCategory> categories = categoryMapper.selectList(catWrapper);
            categoryNameMap = categories.stream()
                    .collect(Collectors.toMap(QuestionCategory::getId, QuestionCategory::getName));
        }
        
        // 组装返回数据
        Map<Long, String> finalCategoryNameMap = categoryNameMap;
        List<Map<String, Object>> list = questions.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("majorId", q.getMajorId());
            map.put("categoryId", q.getCategoryId());
            map.put("categoryName", finalCategoryNameMap.get(q.getCategoryId()));
            map.put("type", q.getType());
            map.put("difficulty", q.getDifficulty());
            map.put("content", q.getContent());
            map.put("options", q.getOptions());
            map.put("answer", q.getAnswer());
            map.put("analysis", q.getAnalysis());
            map.put("source", q.getSource());
            map.put("year", q.getYear());
            map.put("sort", q.getSort());
            map.put("status", q.getStatus());
            map.put("createTime", q.getCreateTime());
            return map;
        }).toList();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("list", list);
        
        return Result.success(result);
    }

    /**
     * 获取题目详情
     */
    @Operation(summary = "获取题目详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getQuestionDetail(@PathVariable(name = "id") Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", question.getId());
        result.put("majorId", question.getMajorId());
        result.put("categoryId", question.getCategoryId());
        result.put("type", question.getType());
        result.put("difficulty", question.getDifficulty());
        result.put("content", question.getContent());
        result.put("options", question.getOptions());
        result.put("answer", question.getAnswer());
        result.put("analysis", question.getAnalysis());
        result.put("source", question.getSource());
        result.put("year", question.getYear());
        result.put("sort", question.getSort());
        result.put("status", question.getStatus());
        result.put("createTime", question.getCreateTime());
        
        // 获取分类信息
        if (question.getCategoryId() != null) {
            QuestionCategory category = categoryMapper.selectById(question.getCategoryId());
            if (category != null) {
                result.put("categoryName", category.getName());
            }
        }
        
        return Result.success(result);
    }

    /**
     * 添加题目
     */
    @Operation(summary = "添加题目")
    @PostMapping("/question")
    public Result<Long> addQuestion(@RequestBody Map<String, Object> questionData) {
        Question question = new Question();
        question.setMajorId(questionData.get("majorId") != null ? ((Number) questionData.get("majorId")).longValue() : null);
        question.setCategoryId(questionData.get("categoryId") != null ? ((Number) questionData.get("categoryId")).longValue() : null);
        question.setType(((Number) questionData.get("type")).intValue());
        question.setDifficulty(questionData.get("difficulty") != null ? ((Number) questionData.get("difficulty")).intValue() : 1);
        question.setContent((String) questionData.get("content"));
        question.setOptions((String) questionData.get("options"));
        question.setAnswer((String) questionData.get("answer"));
        question.setAnalysis((String) questionData.get("analysis"));
        question.setSource((String) questionData.get("source"));
        question.setYear(questionData.get("year") != null ? ((Number) questionData.get("year")).intValue() : null);
        question.setSort(questionData.get("sort") != null ? ((Number) questionData.get("sort")).intValue() : 0);
        question.setStatus(questionData.get("status") != null ? ((Number) questionData.get("status")).intValue() : 1);
        
        questionMapper.insert(question);
        
        // 更新分类题目数量
        if (question.getCategoryId() != null) {
            updateCategoryQuestionCount(question.getCategoryId(), 1);
        }
        
        return Result.success(question.getId());
    }

    /**
     * 更新题目
     */
    @Operation(summary = "更新题目")
    @PutMapping("/question/{id}")
    public Result<Void> updateQuestion(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> questionData
    ) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }
        
        Long oldCategoryId = question.getCategoryId();
        
        if (questionData.containsKey("majorId")) {
            question.setMajorId(questionData.get("majorId") != null ? ((Number) questionData.get("majorId")).longValue() : null);
        }
        if (questionData.containsKey("categoryId")) {
            question.setCategoryId(questionData.get("categoryId") != null ? ((Number) questionData.get("categoryId")).longValue() : null);
        }
        if (questionData.containsKey("type")) {
            question.setType(((Number) questionData.get("type")).intValue());
        }
        if (questionData.containsKey("difficulty")) {
            question.setDifficulty(((Number) questionData.get("difficulty")).intValue());
        }
        if (questionData.containsKey("content")) {
            question.setContent((String) questionData.get("content"));
        }
        if (questionData.containsKey("options")) {
            question.setOptions((String) questionData.get("options"));
        }
        if (questionData.containsKey("answer")) {
            question.setAnswer((String) questionData.get("answer"));
        }
        if (questionData.containsKey("analysis")) {
            question.setAnalysis((String) questionData.get("analysis"));
        }
        if (questionData.containsKey("source")) {
            question.setSource((String) questionData.get("source"));
        }
        if (questionData.containsKey("year")) {
            question.setYear(questionData.get("year") != null ? ((Number) questionData.get("year")).intValue() : null);
        }
        if (questionData.containsKey("sort")) {
            question.setSort(((Number) questionData.get("sort")).intValue());
        }
        if (questionData.containsKey("status")) {
            question.setStatus(((Number) questionData.get("status")).intValue());
        }
        
        questionMapper.updateById(question);
        
        // 更新分类题目数量（如果分类变更）
        if (oldCategoryId != null && !oldCategoryId.equals(question.getCategoryId())) {
            updateCategoryQuestionCount(oldCategoryId, -1);
        }
        if (question.getCategoryId() != null && !question.getCategoryId().equals(oldCategoryId)) {
            updateCategoryQuestionCount(question.getCategoryId(), 1);
        }
        
        return Result.success();
    }

    /**
     * 删除题目
     */
    @Operation(summary = "删除题目")
    @DeleteMapping("/question/{id}")
    public Result<Void> deleteQuestion(@PathVariable(name = "id") Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }
        
        questionMapper.deleteById(id);
        
        // 更新分类题目数量
        if (question.getCategoryId() != null) {
            updateCategoryQuestionCount(question.getCategoryId(), -1);
        }
        
        return Result.success();
    }

    /**
     * 批量导入题目
     */
    @Operation(summary = "批量导入题目")
    @PostMapping("/question/import")
    public Result<Integer> importQuestions(@RequestBody List<Map<String, Object>> questionList) {
        int successCount = 0;
        Map<Long, Integer> categoryCountMap = new HashMap<>();
        
        for (Map<String, Object> questionData : questionList) {
            try {
                Question question = new Question();
                question.setMajorId(questionData.get("majorId") != null ? ((Number) questionData.get("majorId")).longValue() : null);
                question.setCategoryId(questionData.get("categoryId") != null ? ((Number) questionData.get("categoryId")).longValue() : null);
                question.setType(((Number) questionData.get("type")).intValue());
                question.setDifficulty(questionData.get("difficulty") != null ? ((Number) questionData.get("difficulty")).intValue() : 1);
                question.setContent((String) questionData.get("content"));
                question.setOptions((String) questionData.get("options"));
                question.setAnswer((String) questionData.get("answer"));
                question.setAnalysis((String) questionData.get("analysis"));
                question.setSource((String) questionData.get("source"));
                question.setYear(questionData.get("year") != null ? ((Number) questionData.get("year")).intValue() : null);
                question.setSort(questionData.get("sort") != null ? ((Number) questionData.get("sort")).intValue() : 0);
                question.setStatus(questionData.get("status") != null ? ((Number) questionData.get("status")).intValue() : 1);
                
                questionMapper.insert(question);
                successCount++;
                
                // 统计每个分类新增的题目数
                if (question.getCategoryId() != null) {
                    categoryCountMap.merge(question.getCategoryId(), 1, Integer::sum);
                }
            } catch (Exception e) {
                // 单条失败不影响其他
            }
        }
        
        // 批量更新分类题目数量
        for (Map.Entry<Long, Integer> entry : categoryCountMap.entrySet()) {
            updateCategoryQuestionCount(entry.getKey(), entry.getValue());
        }
        
        return Result.success(successCount);
    }

    /**
     * 获取分类列表(树形结构)
     */
    @Operation(summary = "获取分类列表")
    @GetMapping("/category/list")
    public Result<List<Map<String, Object>>> getCategoryList(
            @RequestParam(name = "majorId", required = false) Long majorId
    ) {
        LambdaQueryWrapper<QuestionCategory> wrapper = new LambdaQueryWrapper<>();
        if (majorId != null) {
            wrapper.eq(QuestionCategory::getMajorId, majorId);
        }
        wrapper.orderByAsc(QuestionCategory::getSort);
        List<QuestionCategory> categories = categoryMapper.selectList(wrapper);
        
        // 构建树形结构
        List<Map<String, Object>> tree = buildCategoryTree(categories, 0L);
        
        return Result.success(tree);
    }

    /**
     * 添加分类
     */
    @Operation(summary = "添加分类")
    @PostMapping("/category")
    public Result<Long> addCategory(@RequestBody Map<String, Object> categoryData) {
        QuestionCategory category = new QuestionCategory();
        category.setMajorId(categoryData.get("majorId") != null ? ((Number) categoryData.get("majorId")).longValue() : null);
        category.setParentId(categoryData.get("parentId") != null ? ((Number) categoryData.get("parentId")).longValue() : 0L);
        category.setName((String) categoryData.get("name"));
        category.setIcon((String) categoryData.get("icon"));
        category.setDescription((String) categoryData.get("description"));
        category.setQuestionCount(0);
        category.setSort(categoryData.get("sort") != null ? ((Number) categoryData.get("sort")).intValue() : 0);
        category.setStatus(categoryData.get("status") != null ? ((Number) categoryData.get("status")).intValue() : 1);
        
        categoryMapper.insert(category);
        return Result.success(category.getId());
    }

    /**
     * 更新分类
     */
    @Operation(summary = "更新分类")
    @PutMapping("/category/{id}")
    public Result<Void> updateCategory(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> categoryData
    ) {
        QuestionCategory category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        if (categoryData.containsKey("majorId")) {
            category.setMajorId(categoryData.get("majorId") != null ? ((Number) categoryData.get("majorId")).longValue() : null);
        }
        if (categoryData.containsKey("parentId")) {
            category.setParentId(categoryData.get("parentId") != null ? ((Number) categoryData.get("parentId")).longValue() : 0L);
        }
        if (categoryData.containsKey("name")) {
            category.setName((String) categoryData.get("name"));
        }
        if (categoryData.containsKey("icon")) {
            category.setIcon((String) categoryData.get("icon"));
        }
        if (categoryData.containsKey("description")) {
            category.setDescription((String) categoryData.get("description"));
        }
        if (categoryData.containsKey("sort")) {
            category.setSort(((Number) categoryData.get("sort")).intValue());
        }
        if (categoryData.containsKey("status")) {
            category.setStatus(((Number) categoryData.get("status")).intValue());
        }
        
        categoryMapper.updateById(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @Operation(summary = "删除分类")
    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable(name = "id") Long id) {
        QuestionCategory category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<QuestionCategory> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(QuestionCategory::getParentId, id);
        Long childCount = categoryMapper.selectCount(childWrapper);
        if (childCount > 0) {
            return Result.error("该分类下有子分类，无法删除");
        }
        
        // 检查是否有题目
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(Question::getCategoryId, id);
        Long questionCount = questionMapper.selectCount(questionWrapper);
        if (questionCount > 0) {
            return Result.error("该分类下有题目，无法删除");
        }
        
        categoryMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 构建分类树
     */
    private List<Map<String, Object>> buildCategoryTree(List<QuestionCategory> categories, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        for (QuestionCategory category : categories) {
            if (category.getParentId().equals(parentId)) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", category.getId());
                node.put("majorId", category.getMajorId());
                node.put("parentId", category.getParentId());
                node.put("name", category.getName());
                node.put("icon", category.getIcon());
                node.put("description", category.getDescription());
                node.put("questionCount", category.getQuestionCount());
                node.put("sort", category.getSort());
                node.put("status", category.getStatus());
                
                List<Map<String, Object>> children = buildCategoryTree(categories, category.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                
                tree.add(node);
            }
        }
        
        return tree;
    }

    /**
     * 更新分类题目数量
     */
    private void updateCategoryQuestionCount(Long categoryId, int delta) {
        QuestionCategory category = categoryMapper.selectById(categoryId);
        if (category != null) {
            category.setQuestionCount(Math.max(0, category.getQuestionCount() + delta));
            categoryMapper.updateById(category);
        }
    }
}
