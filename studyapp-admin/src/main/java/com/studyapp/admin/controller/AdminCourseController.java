package com.studyapp.admin.controller;

import com.studyapp.admin.feign.CourseFeignClient;
import com.studyapp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 课程管理控制器
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/admin/course")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseFeignClient courseFeignClient;

    /**
     * 分页获取课程列表
     */
    @Operation(summary = "获取课程列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getCourseList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "majorId", required = false) Long majorId
    ) {
        return courseFeignClient.getCourseList(page, size, keyword, status, majorId);
    }

    /**
     * 获取课程详情(含章节和视频)
     */
    @Operation(summary = "获取课程详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getCourseDetail(@PathVariable(name = "id") Long id) {
        return courseFeignClient.getCourseDetail(id);
    }

    /**
     * 添加课程
     */
    @Operation(summary = "添加课程")
    @PostMapping
    public Result<Long> addCourse(@RequestBody Map<String, Object> course) {
        return courseFeignClient.addCourse(course);
    }

    /**
     * 更新课程
     */
    @Operation(summary = "更新课程")
    @PutMapping("/{id}")
    public Result<Void> updateCourse(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> course
    ) {
        return courseFeignClient.updateCourse(id, course);
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable(name = "id") Long id) {
        return courseFeignClient.deleteCourse(id);
    }

    /**
     * 更新课程状态(上下架)
     */
    @Operation(summary = "更新课程状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateCourseStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    ) {
        return courseFeignClient.updateCourseStatus(id, status);
    }

    /**
     * 添加章节
     */
    @Operation(summary = "添加章节")
    @PostMapping("/chapter")
    public Result<Long> addChapter(@RequestBody Map<String, Object> chapter) {
        return courseFeignClient.addChapter(chapter);
    }

    /**
     * 更新章节
     */
    @Operation(summary = "更新章节")
    @PutMapping("/chapter/{id}")
    public Result<Void> updateChapter(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> chapter
    ) {
        return courseFeignClient.updateChapter(id, chapter);
    }

    /**
     * 删除章节
     */
    @Operation(summary = "删除章节")
    @DeleteMapping("/chapter/{id}")
    public Result<Void> deleteChapter(@PathVariable(name = "id") Long id) {
        return courseFeignClient.deleteChapter(id);
    }

    /**
     * 添加视频
     */
    @Operation(summary = "添加视频")
    @PostMapping("/video")
    public Result<Long> addVideo(@RequestBody Map<String, Object> video) {
        return courseFeignClient.addVideo(video);
    }

    /**
     * 更新视频
     */
    @Operation(summary = "更新视频")
    @PutMapping("/video/{id}")
    public Result<Void> updateVideo(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> video
    ) {
        return courseFeignClient.updateVideo(id, video);
    }

    /**
     * 删除视频
     */
    @Operation(summary = "删除视频")
    @DeleteMapping("/video/{id}")
    public Result<Void> deleteVideo(@PathVariable(name = "id") Long id) {
        return courseFeignClient.deleteVideo(id);
    }
}
