package com.studyapp.course.controller;

import com.studyapp.common.result.Result;
import com.studyapp.common.utils.UserContext;
import com.studyapp.course.dto.*;
import com.studyapp.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 * 提供考研学习小程序的课程相关功能接口
 * 包括课程列表查询、课程详情、视频播放、学习进度管理等功能
 */
@Tag(name = "课程接口", description = "考研学习小程序课程模块，提供课程查询、视频播放、学习进度管理等功能")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 获取课程列表（按专业）
     * 根据专业ID查询该专业下的所有课程列表
     *
     * @param majorId 专业ID
     * @return 课程列表
     */
    @Operation(
            summary = "获取课程列表",
            description = "根据专业ID获取该专业下的所有课程列表，返回课程基本信息包括课程名称、封面、简介、讲师等"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "400", description = "参数错误，专业ID无效"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/list")
    public Result<List<CourseListResponse>> getCourseList(
            @Parameter(description = "专业ID，用于筛选该专业下的课程", required = true, example = "1")
            @RequestParam(name = "majorId") Long majorId) {
        return Result.success(courseService.getCourseList(majorId));
    }

    /**
     * 获取课程详情（包含章节和视频）
     * 根据课程ID查询课程的完整信息，包括课程介绍、章节列表、视频列表等
     *
     * @param id 课程ID
     * @return 课程详情
     */
    @Operation(
            summary = "获取课程详情",
            description = "根据课程ID获取课程的详细信息，包括课程基本信息、讲师介绍、章节目录、视频列表等完整内容"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "课程不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/detail/{id}")
    public Result<CourseDetailResponse> getCourseDetail(
            @Parameter(description = "课程ID", required = true, example = "1")
            @PathVariable(name = "id") Long id) {
        return Result.success(courseService.getCourseDetail(id));
    }

    /**
     * 获取视频播放地址
     * 根据视频ID获取视频的播放地址，同时记录用户的观看行为
     *
     * @param id 视频ID
     * @return 视频播放信息
     */
    @Operation(
            summary = "获取视频播放地址",
            description = "根据视频ID获取视频的播放地址URL，返回视频播放所需的完整信息，包括播放地址、视频时长、上次播放位置等"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "用户未登录"),
            @ApiResponse(responseCode = "404", description = "视频不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/video/{id}")
    public Result<VideoPlayResponse> getVideoPlayUrl(
            @Parameter(description = "视频ID", required = true, example = "1")
            @PathVariable(name = "id") Long id) {
        Long userId = UserContext.getUserId();
        return Result.success(courseService.getVideoPlayUrl(userId, id));
    }

    /**
     * 保存学习进度
     * 保存用户观看视频的学习进度，支持断点续播功能
     *
     * @param request 学习进度请求对象
     * @return 操作结果
     */
    @Operation(
            summary = "保存学习进度",
            description = "保存用户的视频学习进度，包括当前播放位置、观看时长等信息，用于实现断点续播和学习统计功能"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "401", description = "用户未登录"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/progress/save")
    public Result<Void> saveProgress(
            @Parameter(description = "学习进度信息，包含视频ID、播放位置、观看时长等", required = true)
            @Valid @RequestBody SaveProgressRequest request) {
        Long userId = UserContext.getUserId();
        courseService.saveProgress(userId, request);
        return Result.success();
    }

    /**
     * 获取学习进度
     * 获取用户在指定课程的学习进度信息
     *
     * @param courseId 课程ID
     * @return 学习进度信息
     */
    @Operation(
            summary = "获取学习进度",
            description = "获取当前用户在指定课程的学习进度信息，包括已学习的视频列表、各视频的播放进度、整体完成百分比等"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "用户未登录"),
            @ApiResponse(responseCode = "404", description = "课程不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/progress/{courseId}")
    public Result<ProgressResponse> getProgress(
            @Parameter(description = "课程ID，查询该课程的学习进度", required = true, example = "1")
            @PathVariable(name = "courseId") Long courseId) {
        Long userId = UserContext.getUserId();
        return Result.success(courseService.getProgress(userId, courseId));
    }

    /**
     * 获取我的学习课程
     * 获取当前用户正在学习的所有课程列表
     *
     * @return 用户学习的课程列表
     */
    @Operation(
            summary = "获取我的课程",
            description = "获取当前登录用户正在学习的所有课程列表，包括课程基本信息和学习进度，按最近学习时间排序"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "用户未登录"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/my/list")
    public Result<List<CourseListResponse>> getMyCourses() {
        Long userId = UserContext.getUserId();
        return Result.success(courseService.getMyCourses(userId));
    }
}
