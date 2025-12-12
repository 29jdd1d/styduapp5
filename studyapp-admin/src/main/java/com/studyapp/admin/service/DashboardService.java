package com.studyapp.admin.service;

import com.studyapp.admin.dto.DashboardStatsResponse;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     */
    DashboardStatsResponse getStats();
}
