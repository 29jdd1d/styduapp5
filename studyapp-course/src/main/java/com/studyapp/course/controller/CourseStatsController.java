package com.studyapp.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.result.Result;
import com.studyapp.course.entity.Course;
import com.studyapp.course.mapper.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程统计内部接口
 */
@Tag(name = "课程统计内部接口")
@RestController
@RequestMapping("/course/inner/stats")
@RequiredArgsConstructor
public class CourseStatsController {

    private final CourseMapper courseMapper;

    @Operation(summary = "获取课程总数")
    @GetMapping("/total")
    public Result<Long> getTotalCount() {
        Long count = courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, CommonConstants.STATUS_NORMAL));
        return Result.success(count);
    }
}
