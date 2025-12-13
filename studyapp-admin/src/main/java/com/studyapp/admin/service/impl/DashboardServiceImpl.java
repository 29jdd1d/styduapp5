package com.studyapp.admin.service.impl;

import com.studyapp.admin.dto.DashboardStatsResponse;
import com.studyapp.admin.feign.*;
import com.studyapp.admin.service.DashboardService;
import com.studyapp.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 仪表盘服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserFeignClient userFeignClient;
    private final QuestionFeignClient questionFeignClient;
    private final CourseFeignClient courseFeignClient;
    private final CommunityFeignClient communityFeignClient;
    private final CheckinFeignClient checkinFeignClient;
    private final PracticeFeignClient practiceFeignClient;

    @Override
    public DashboardStatsResponse getStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();

        // 用户统计
        response.setTotalUsers(safeGetLong(userFeignClient.getTotalCount()));
        response.setTodayNewUsers(safeGetLong(userFeignClient.getTodayNewCount()));

        // 题目统计
        response.setTotalQuestions(safeGetLong(questionFeignClient.getTotalCount()));

        // 课程统计
        response.setTotalCourses(safeGetLong(courseFeignClient.getTotalCount()));

        // 帖子统计
        response.setTotalPosts(safeGetLong(communityFeignClient.getTotalCount()));

        // 今日打卡
        response.setTodayCheckins(safeGetLong(checkinFeignClient.getTodayCount()));

        // 今日练习
        response.setTodayPracticeCount(safeGetLong(practiceFeignClient.getTodayCount()));

        // 活跃用户（暂用今日打卡数代替）
        response.setActiveUsers(response.getTodayCheckins());

        return response;
    }

    /**
     * 安全获取Long值，Feign调用失败返回0
     */
    private Long safeGetLong(Result<Long> result) {
        try {
            if (result != null && result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.warn("Feign调用失败: {}", e.getMessage());
        }
        return 0L;
    }
}
