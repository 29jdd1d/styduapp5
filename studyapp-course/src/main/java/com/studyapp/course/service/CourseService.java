package com.studyapp.course.service;

import com.studyapp.course.dto.*;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 获取课程列表
     *
     * @param majorId 专业ID
     * @return 课程列表
     */
    List<CourseListResponse> getCourseList(Long majorId);

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    CourseDetailResponse getCourseDetail(Long courseId);

    /**
     * 获取视频播放地址
     *
     * @param userId  用户ID
     * @param videoId 视频ID
     * @return 视频播放信息
     */
    VideoPlayResponse getVideoPlayUrl(Long userId, Long videoId);

    /**
     * 保存学习进度
     *
     * @param userId  用户ID
     * @param request 请求
     */
    void saveProgress(Long userId, SaveProgressRequest request);

    /**
     * 获取学习进度
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 学习进度
     */
    ProgressResponse getProgress(Long userId, Long courseId);

    /**
     * 获取我的课程（有学习记录的）
     *
     * @param userId 用户ID
     * @return 课程列表
     */
    List<CourseListResponse> getMyCourses(Long userId);
}
