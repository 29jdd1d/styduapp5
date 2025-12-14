package com.studyapp.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyapp.common.result.Result;
import com.studyapp.course.entity.Course;
import com.studyapp.course.entity.CourseChapter;
import com.studyapp.course.entity.CourseVideo;
import com.studyapp.course.mapper.CourseChapterMapper;
import com.studyapp.course.mapper.CourseMapper;
import com.studyapp.course.mapper.CourseVideoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程服务管理接口（供Admin服务调用）
 */
@Tag(name = "课程管理内部接口")
@RestController
@RequestMapping("/course/inner/admin")
@RequiredArgsConstructor
public class CourseAdminController {

    private final CourseMapper courseMapper;
    private final CourseChapterMapper chapterMapper;
    private final CourseVideoMapper videoMapper;

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
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(Course::getStatus, status);
        }
        if (majorId != null) {
            wrapper.eq(Course::getMajorId, majorId);
        }
        wrapper.orderByAsc(Course::getSort).orderByDesc(Course::getCreateTime);
        
        IPage<Course> pageResult = courseMapper.selectPage(new Page<>(page, size), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("list", pageResult.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取课程详情(含章节和视频)
     */
    @Operation(summary = "获取课程详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getCourseDetail(@PathVariable(name = "id") Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("course", course);
        
        // 获取章节列表
        LambdaQueryWrapper<CourseChapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(CourseChapter::getCourseId, id)
                .orderByAsc(CourseChapter::getSort);
        List<CourseChapter> chapters = chapterMapper.selectList(chapterWrapper);
        
        // 获取每个章节的视频
        List<Map<String, Object>> chapterList = chapters.stream().map(chapter -> {
            Map<String, Object> chapterMap = new HashMap<>();
            chapterMap.put("id", chapter.getId());
            chapterMap.put("title", chapter.getTitle());
            chapterMap.put("sort", chapter.getSort());
            chapterMap.put("status", chapter.getStatus());
            
            LambdaQueryWrapper<CourseVideo> videoWrapper = new LambdaQueryWrapper<>();
            videoWrapper.eq(CourseVideo::getChapterId, chapter.getId())
                    .orderByAsc(CourseVideo::getSort);
            List<CourseVideo> videos = videoMapper.selectList(videoWrapper);
            chapterMap.put("videos", videos);
            
            return chapterMap;
        }).toList();
        
        result.put("chapters", chapterList);
        
        return Result.success(result);
    }

    /**
     * 添加课程
     */
    @Operation(summary = "添加课程")
    @PostMapping("/course")
    public Result<Long> addCourse(@RequestBody Map<String, Object> courseData) {
        Course course = new Course();
        course.setMajorId(courseData.get("majorId") != null ? ((Number) courseData.get("majorId")).longValue() : null);
        course.setTitle((String) courseData.get("title"));
        course.setCover((String) courseData.get("cover"));
        course.setDescription((String) courseData.get("description"));
        course.setTeacherName((String) courseData.get("teacherName"));
        course.setTeacherAvatar((String) courseData.get("teacherAvatar"));
        course.setIsFree(courseData.get("isFree") != null ? ((Number) courseData.get("isFree")).intValue() : 1);
        course.setViewCount(0);
        course.setChapterCount(0);
        course.setVideoCount(0);
        course.setSort(courseData.get("sort") != null ? ((Number) courseData.get("sort")).intValue() : 0);
        course.setStatus(courseData.get("status") != null ? ((Number) courseData.get("status")).intValue() : 1);
        
        courseMapper.insert(course);
        return Result.success(course.getId());
    }

    /**
     * 更新课程
     */
    @Operation(summary = "更新课程")
    @PutMapping("/course/{id}")
    public Result<Void> updateCourse(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> courseData
    ) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        if (courseData.containsKey("majorId")) {
            course.setMajorId(courseData.get("majorId") != null ? ((Number) courseData.get("majorId")).longValue() : null);
        }
        if (courseData.containsKey("title")) {
            course.setTitle((String) courseData.get("title"));
        }
        if (courseData.containsKey("cover")) {
            course.setCover((String) courseData.get("cover"));
        }
        if (courseData.containsKey("description")) {
            course.setDescription((String) courseData.get("description"));
        }
        if (courseData.containsKey("teacherName")) {
            course.setTeacherName((String) courseData.get("teacherName"));
        }
        if (courseData.containsKey("teacherAvatar")) {
            course.setTeacherAvatar((String) courseData.get("teacherAvatar"));
        }
        if (courseData.containsKey("isFree")) {
            course.setIsFree(((Number) courseData.get("isFree")).intValue());
        }
        if (courseData.containsKey("sort")) {
            course.setSort(((Number) courseData.get("sort")).intValue());
        }
        if (courseData.containsKey("status")) {
            course.setStatus(((Number) courseData.get("status")).intValue());
        }
        
        courseMapper.updateById(course);
        return Result.success();
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/course/{id}")
    public Result<Void> deleteCourse(@PathVariable(name = "id") Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        // 删除课程下的所有视频
        LambdaQueryWrapper<CourseChapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(CourseChapter::getCourseId, id);
        List<CourseChapter> chapters = chapterMapper.selectList(chapterWrapper);
        
        for (CourseChapter chapter : chapters) {
            LambdaQueryWrapper<CourseVideo> videoWrapper = new LambdaQueryWrapper<>();
            videoWrapper.eq(CourseVideo::getChapterId, chapter.getId());
            videoMapper.delete(videoWrapper);
        }
        
        // 删除章节
        chapterMapper.delete(chapterWrapper);
        
        // 删除课程
        courseMapper.deleteById(id);
        
        return Result.success();
    }

    /**
     * 更新课程状态(上下架)
     */
    @Operation(summary = "更新课程状态")
    @PutMapping("/course/{id}/status")
    public Result<Void> updateCourseStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    ) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        course.setStatus(status);
        courseMapper.updateById(course);
        
        return Result.success();
    }

    /**
     * 添加章节
     */
    @Operation(summary = "添加章节")
    @PostMapping("/chapter")
    public Result<Long> addChapter(@RequestBody Map<String, Object> chapterData) {
        Long courseId = ((Number) chapterData.get("courseId")).longValue();
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(courseId);
        chapter.setTitle((String) chapterData.get("title"));
        chapter.setSort(chapterData.get("sort") != null ? ((Number) chapterData.get("sort")).intValue() : 0);
        chapter.setStatus(chapterData.get("status") != null ? ((Number) chapterData.get("status")).intValue() : 1);
        
        chapterMapper.insert(chapter);
        
        // 更新课程章节数
        course.setChapterCount(course.getChapterCount() + 1);
        courseMapper.updateById(course);
        
        return Result.success(chapter.getId());
    }

    /**
     * 更新章节
     */
    @Operation(summary = "更新章节")
    @PutMapping("/chapter/{id}")
    public Result<Void> updateChapter(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> chapterData
    ) {
        CourseChapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            return Result.error("章节不存在");
        }
        
        if (chapterData.containsKey("title")) {
            chapter.setTitle((String) chapterData.get("title"));
        }
        if (chapterData.containsKey("sort")) {
            chapter.setSort(((Number) chapterData.get("sort")).intValue());
        }
        if (chapterData.containsKey("status")) {
            chapter.setStatus(((Number) chapterData.get("status")).intValue());
        }
        
        chapterMapper.updateById(chapter);
        return Result.success();
    }

    /**
     * 删除章节
     */
    @Operation(summary = "删除章节")
    @DeleteMapping("/chapter/{id}")
    public Result<Void> deleteChapter(@PathVariable(name = "id") Long id) {
        CourseChapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            return Result.error("章节不存在");
        }
        
        // 删除章节下的视频
        LambdaQueryWrapper<CourseVideo> videoWrapper = new LambdaQueryWrapper<>();
        videoWrapper.eq(CourseVideo::getChapterId, id);
        Long videoCount = videoMapper.selectCount(videoWrapper);
        videoMapper.delete(videoWrapper);
        
        // 删除章节
        chapterMapper.deleteById(id);
        
        // 更新课程统计
        Course course = courseMapper.selectById(chapter.getCourseId());
        if (course != null) {
            course.setChapterCount(Math.max(0, course.getChapterCount() - 1));
            course.setVideoCount(Math.max(0, course.getVideoCount() - videoCount.intValue()));
            courseMapper.updateById(course);
        }
        
        return Result.success();
    }

    /**
     * 添加视频
     */
    @Operation(summary = "添加视频")
    @PostMapping("/video")
    public Result<Long> addVideo(@RequestBody Map<String, Object> videoData) {
        Long chapterId = ((Number) videoData.get("chapterId")).longValue();
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            return Result.error("章节不存在");
        }
        
        CourseVideo video = new CourseVideo();
        video.setChapterId(chapterId);
        video.setTitle((String) videoData.get("title"));
        video.setVideoUrl((String) videoData.get("videoUrl"));
        video.setVideoKey((String) videoData.get("videoKey"));
        video.setDuration(videoData.get("duration") != null ? ((Number) videoData.get("duration")).intValue() : 0);
        video.setIsFree(videoData.get("isFree") != null ? ((Number) videoData.get("isFree")).intValue() : 0);
        video.setSort(videoData.get("sort") != null ? ((Number) videoData.get("sort")).intValue() : 0);
        video.setStatus(videoData.get("status") != null ? ((Number) videoData.get("status")).intValue() : 1);
        
        videoMapper.insert(video);
        
        // 更新课程视频数
        Course course = courseMapper.selectById(chapter.getCourseId());
        if (course != null) {
            course.setVideoCount(course.getVideoCount() + 1);
            courseMapper.updateById(course);
        }
        
        return Result.success(video.getId());
    }

    /**
     * 更新视频
     */
    @Operation(summary = "更新视频")
    @PutMapping("/video/{id}")
    public Result<Void> updateVideo(
            @PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> videoData
    ) {
        CourseVideo video = videoMapper.selectById(id);
        if (video == null) {
            return Result.error("视频不存在");
        }
        
        if (videoData.containsKey("title")) {
            video.setTitle((String) videoData.get("title"));
        }
        if (videoData.containsKey("videoUrl")) {
            video.setVideoUrl((String) videoData.get("videoUrl"));
        }
        if (videoData.containsKey("videoKey")) {
            video.setVideoKey((String) videoData.get("videoKey"));
        }
        if (videoData.containsKey("duration")) {
            video.setDuration(((Number) videoData.get("duration")).intValue());
        }
        if (videoData.containsKey("isFree")) {
            video.setIsFree(((Number) videoData.get("isFree")).intValue());
        }
        if (videoData.containsKey("sort")) {
            video.setSort(((Number) videoData.get("sort")).intValue());
        }
        if (videoData.containsKey("status")) {
            video.setStatus(((Number) videoData.get("status")).intValue());
        }
        
        videoMapper.updateById(video);
        return Result.success();
    }

    /**
     * 删除视频
     */
    @Operation(summary = "删除视频")
    @DeleteMapping("/video/{id}")
    public Result<Void> deleteVideo(@PathVariable(name = "id") Long id) {
        CourseVideo video = videoMapper.selectById(id);
        if (video == null) {
            return Result.error("视频不存在");
        }
        
        // 获取章节
        CourseChapter chapter = chapterMapper.selectById(video.getChapterId());
        
        // 删除视频
        videoMapper.deleteById(id);
        
        // 更新课程视频数
        if (chapter != null) {
            Course course = courseMapper.selectById(chapter.getCourseId());
            if (course != null) {
                course.setVideoCount(Math.max(0, course.getVideoCount() - 1));
                courseMapper.updateById(course);
            }
        }
        
        return Result.success();
    }
}
