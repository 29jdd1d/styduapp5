package com.studyapp.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.ResultCode;
import com.studyapp.common.utils.CosUtils;
import com.studyapp.course.dto.*;
import com.studyapp.course.entity.Course;
import com.studyapp.course.entity.CourseChapter;
import com.studyapp.course.entity.CourseVideo;
import com.studyapp.course.entity.UserCourseProgress;
import com.studyapp.course.mapper.CourseChapterMapper;
import com.studyapp.course.mapper.CourseMapper;
import com.studyapp.course.mapper.CourseVideoMapper;
import com.studyapp.course.mapper.UserCourseProgressMapper;
import com.studyapp.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseChapterMapper chapterMapper;
    private final CourseVideoMapper videoMapper;
    private final UserCourseProgressMapper progressMapper;
    private final CosUtils cosUtils;

    @Override
    public List<CourseListResponse> getCourseList(Long majorId) {
        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getMajorId, majorId)
                        .eq(Course::getStatus, CommonConstants.STATUS_NORMAL)
                        .orderByAsc(Course::getSort)
        );

        return courses.stream().map(course -> {
            CourseListResponse response = new CourseListResponse();
            BeanUtil.copyProperties(course, response);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getStatus() != CommonConstants.STATUS_NORMAL) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }

        // 增加观看次数
        courseMapper.incrementViewCount(courseId);

        CourseDetailResponse response = new CourseDetailResponse();
        BeanUtil.copyProperties(course, response);

        // 获取章节列表
        List<CourseChapter> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<CourseChapter>()
                        .eq(CourseChapter::getCourseId, courseId)
                        .eq(CourseChapter::getStatus, CommonConstants.STATUS_NORMAL)
                        .orderByAsc(CourseChapter::getSort)
        );

        List<ChapterResponse> chapterResponses = chapters.stream().map(chapter -> {
            ChapterResponse chapterResponse = new ChapterResponse();
            chapterResponse.setId(chapter.getId());
            chapterResponse.setTitle(chapter.getTitle());

            // 获取视频列表
            List<CourseVideo> videos = videoMapper.selectList(
                    new LambdaQueryWrapper<CourseVideo>()
                            .eq(CourseVideo::getChapterId, chapter.getId())
                            .eq(CourseVideo::getStatus, CommonConstants.STATUS_NORMAL)
                            .orderByAsc(CourseVideo::getSort)
            );

            List<VideoResponse> videoResponses = videos.stream().map(video -> {
                VideoResponse videoResponse = new VideoResponse();
                videoResponse.setId(video.getId());
                videoResponse.setTitle(video.getTitle());
                videoResponse.setDuration(video.getDuration());
                videoResponse.setIsFree(video.getIsFree());
                videoResponse.setDurationText(formatDuration(video.getDuration()));
                return videoResponse;
            }).collect(Collectors.toList());

            chapterResponse.setVideos(videoResponses);
            return chapterResponse;
        }).collect(Collectors.toList());

        response.setChapters(chapterResponses);
        return response;
    }

    @Override
    public VideoPlayResponse getVideoPlayUrl(Long userId, Long videoId) {
        CourseVideo video = videoMapper.selectById(videoId);
        if (video == null || video.getStatus() != CommonConstants.STATUS_NORMAL) {
            throw new BusinessException("视频不存在");
        }

        VideoPlayResponse response = new VideoPlayResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDuration(video.getDuration());

        // 生成带签名的临时播放URL（有效期30分钟）
        String playUrl;
        if (video.getVideoKey() != null && !video.getVideoKey().isEmpty()) {
            playUrl = cosUtils.generatePresignedUrl(video.getVideoKey(), 30);
        } else {
            playUrl = video.getVideoUrl();
        }
        response.setPlayUrl(playUrl);

        // 获取上次播放进度
        if (userId != null) {
            CourseChapter chapter = chapterMapper.selectById(video.getChapterId());
            if (chapter != null) {
                UserCourseProgress progress = progressMapper.selectOne(
                        new LambdaQueryWrapper<UserCourseProgress>()
                                .eq(UserCourseProgress::getUserId, userId)
                                .eq(UserCourseProgress::getCourseId, chapter.getCourseId())
                                .eq(UserCourseProgress::getVideoId, videoId)
                );
                if (progress != null) {
                    response.setLastProgress(progress.getProgress());
                }
            }
        }

        return response;
    }

    @Override
    @Transactional
    public void saveProgress(Long userId, SaveProgressRequest request) {
        UserCourseProgress progress = progressMapper.selectOne(
                new LambdaQueryWrapper<UserCourseProgress>()
                        .eq(UserCourseProgress::getUserId, userId)
                        .eq(UserCourseProgress::getCourseId, request.getCourseId())
        );

        if (progress == null) {
            progress = new UserCourseProgress();
            progress.setUserId(userId);
            progress.setCourseId(request.getCourseId());
            progress.setVideoId(request.getVideoId());
            progress.setProgress(request.getProgress());
            progress.setIsCompleted(Boolean.TRUE.equals(request.getIsCompleted()) ? 1 : 0);
            progressMapper.insert(progress);
        } else {
            progress.setVideoId(request.getVideoId());
            progress.setProgress(request.getProgress());
            if (Boolean.TRUE.equals(request.getIsCompleted())) {
                progress.setIsCompleted(1);
            }
            progressMapper.updateById(progress);
        }
    }

    @Override
    public ProgressResponse getProgress(Long userId, Long courseId) {
        UserCourseProgress progress = progressMapper.selectOne(
                new LambdaQueryWrapper<UserCourseProgress>()
                        .eq(UserCourseProgress::getUserId, userId)
                        .eq(UserCourseProgress::getCourseId, courseId)
        );

        if (progress == null) {
            return null;
        }

        ProgressResponse response = new ProgressResponse();
        response.setCourseId(progress.getCourseId());
        response.setVideoId(progress.getVideoId());
        response.setProgress(progress.getProgress());
        response.setIsCompleted(progress.getIsCompleted());
        return response;
    }

    @Override
    public List<CourseListResponse> getMyCourses(Long userId) {
        // 获取有学习记录的课程ID
        List<UserCourseProgress> progresses = progressMapper.selectList(
                new LambdaQueryWrapper<UserCourseProgress>()
                        .eq(UserCourseProgress::getUserId, userId)
                        .orderByDesc(UserCourseProgress::getUpdateTime)
        );

        if (progresses.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> courseIds = progresses.stream()
                .map(UserCourseProgress::getCourseId)
                .distinct()
                .collect(Collectors.toList());

        List<Course> courses = courseMapper.selectBatchIds(courseIds);

        return courses.stream().map(course -> {
            CourseListResponse response = new CourseListResponse();
            BeanUtil.copyProperties(course, response);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 格式化时长
     */
    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return "00:00";
        }
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
}
