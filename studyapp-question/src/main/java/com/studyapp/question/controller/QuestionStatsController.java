package com.studyapp.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.result.Result;
import com.studyapp.question.entity.Question;
import com.studyapp.question.mapper.QuestionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题库统计内部接口
 */
@Tag(name = "题库统计内部接口")
@RestController
@RequestMapping("/question/inner/stats")
@RequiredArgsConstructor
public class QuestionStatsController {

    private final QuestionMapper questionMapper;

    @Operation(summary = "获取题目总数")
    @GetMapping("/total")
    public Result<Long> getTotalCount() {
        Long count = questionMapper.selectCount(new LambdaQueryWrapper<Question>()
                .eq(Question::getStatus, CommonConstants.STATUS_NORMAL));
        return Result.success(count);
    }
}
