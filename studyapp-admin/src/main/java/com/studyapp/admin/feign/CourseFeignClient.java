package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 课程服务Feign客户端
 */
@FeignClient(name = "studyapp-course", path = "/course")
public interface CourseFeignClient {

    /**
     * 获取课程总数
     */
    @GetMapping("/inner/stats/total")
    Result<Long> getTotalCount();

    /**
     * 分页获取课程列表
     */
    @GetMapping("/inner/admin/list")
    Result<Map<String, Object>> getCourseList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "majorId", required = false) Long majorId
    );

    /**
     * 获取课程详情(含章节和视频)
     */
    @GetMapping("/inner/admin/detail/{id}")
    Result<Map<String, Object>> getCourseDetail(@PathVariable(name = "id") Long id);

    /**
     * 添加课程
     */
    @PostMapping("/inner/admin/course")
    Result<Long> addCourse(@RequestBody Map<String, Object> course);

    /**
     * 更新课程
     */
    @PutMapping("/inner/admin/course/{id}")
    Result<Void> updateCourse(@PathVariable(name = "id") Long id, @RequestBody Map<String, Object> course);

    /**
     * 删除课程
     */
    @DeleteMapping("/inner/admin/course/{id}")
    Result<Void> deleteCourse(@PathVariable(name = "id") Long id);

    /**
     * 更新课程状态(上下架)
     */
    @PutMapping("/inner/admin/course/{id}/status")
    Result<Void> updateCourseStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    );

    /**
     * 添加章节
     */
    @PostMapping("/inner/admin/chapter")
    Result<Long> addChapter(@RequestBody Map<String, Object> chapter);

    /**
     * 更新章节
     */
    @PutMapping("/inner/admin/chapter/{id}")
    Result<Void> updateChapter(@PathVariable(name = "id") Long id, @RequestBody Map<String, Object> chapter);

    /**
     * 删除章节
     */
    @DeleteMapping("/inner/admin/chapter/{id}")
    Result<Void> deleteChapter(@PathVariable(name = "id") Long id);

    /**
     * 添加视频
     */
    @PostMapping("/inner/admin/video")
    Result<Long> addVideo(@RequestBody Map<String, Object> video);

    /**
     * 更新视频
     */
    @PutMapping("/inner/admin/video/{id}")
    Result<Void> updateVideo(@PathVariable(name = "id") Long id, @RequestBody Map<String, Object> video);

    /**
     * 删除视频
     */
    @DeleteMapping("/inner/admin/video/{id}")
    Result<Void> deleteVideo(@PathVariable(name = "id") Long id);
}
