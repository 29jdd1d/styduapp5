package com.studyapp.checkin.service;

import com.studyapp.checkin.dto.*;
import com.studyapp.common.result.PageResult;

import java.util.List;

/**
 * 打卡服务接口
 */
public interface CheckinService {

    /**
     * 打卡
     */
    CheckinResponse checkin(Long userId, CheckinRequest request);

    /**
     * 获取打卡统计
     */
    CheckinStatsResponse getStats(Long userId);

    /**
     * 获取打卡日历
     */
    List<String> getCalendar(Long userId, Integer year, Integer month);

    /**
     * 获取打卡记录
     */
    PageResult<CheckinRecordResponse> getRecords(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取今日打卡
     */
    CheckinRecordResponse getTodayCheckin(Long userId);

    /**
     * 获取连续打卡排行
     */
    List<CheckinRankResponse> getStreakRanking(Integer limit);

    /**
     * 获取总天数排行
     */
    List<CheckinRankResponse> getTotalDaysRanking(Integer limit);

    /**
     * 获取用户排名
     */
    Integer getUserRank(Long userId, String type);
}
