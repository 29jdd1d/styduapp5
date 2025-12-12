package com.studyapp.course.controller;

import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.course.dto.*;
import com.studyapp.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 */
@Tag(name = "课程接口")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "获取课程列表")
    @GetMapping("/list")
    public Result<List<CourseListResponse>> getCourseList(@RequestParam Long majorId) {
        return Result.success(courseService.getCourseList(majorId));
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/detail/{id}")
    public Result<CourseDetailResponse> getCourseDetail(@PathVariable Long id) {
        return Result.success(courseService.getCourseDetail(id));
    }

    @Operation(summary = "获取视频播放地址")
    @GetMapping("/video/{id}")
    public Result<VideoPlayResponse> getVideoPlayUrl(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        return Result.success(courseService.getVideoPlayUrl(userId, id));
    }

    @Operation(summary = "保存学习进度")
    @PostMapping("/progress/save")
    public Result<Void> saveProgress(@Valid @RequestBody SaveProgressRequest request) {
        Long userId = UserContext.getUserId();
        courseService.saveProgress(userId, request);
        return Result.success();
    }

    @Operation(summary = "获取学习进度")
    @GetMapping("/progress/{courseId}")
    public Result<ProgressResponse> getProgress(@PathVariable Long courseId) {
        Long userId = UserContext.getUserId();
        return Result.success(courseService.getProgress(userId, courseId));
    }

    @Operation(summary = "获取我的课程")
    @GetMapping("/my/list")
    public Result<List<CourseListResponse>> getMyCourses() {
        Long userId = UserContext.getUserId();
        return Result.success(courseService.getMyCourses(userId));
    }
}
