package com.studyapp.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyapp.checkin.dto.*;
import com.studyapp.checkin.entity.CheckinRecord;
import com.studyapp.checkin.entity.CheckinStats;
import com.studyapp.checkin.feign.UserFeignClient;
import com.studyapp.checkin.mapper.CheckinRecordMapper;
import com.studyapp.checkin.mapper.CheckinStatsMapper;
import com.studyapp.checkin.service.CheckinService;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.PageResult;
import com.studyapp.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 打卡服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {

    private final CheckinRecordMapper checkinRecordMapper;
    private final CheckinStatsMapper checkinStatsMapper;
    private final UserFeignClient userFeignClient;

    @Override
    @Transactional
    public CheckinResponse checkin(Long userId, CheckinRequest request) {
        LocalDate today = LocalDate.now();

        // 检查今日是否已打卡
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
                .eq(CheckinRecord::getCheckinDate, today);
        CheckinRecord existing = checkinRecordMapper.selectOne(wrapper);

        if (existing != null) {
            // 更新今日打卡记录
            existing.setStudyMinutes(request.getStudyMinutes());
            existing.setQuestionCount(request.getQuestionCount());
            existing.setContent(request.getContent());
            existing.setMood(request.getMood());
            checkinRecordMapper.updateById(existing);

            CheckinResponse response = new CheckinResponse();
            response.setSuccess(true);
            response.setMessage("今日打卡已更新");
            
            CheckinStats stats = getOrCreateStats(userId);
            response.setCurrentStreak(stats.getCurrentStreak());
            response.setTotalDays(stats.getTotalDays());
            response.setIsNewRecord(false);
            return response;
        }

        // 创建打卡记录
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setCheckinDate(today);
        record.setStudyMinutes(request.getStudyMinutes());
        record.setQuestionCount(request.getQuestionCount());
        record.setContent(request.getContent());
        record.setMood(request.getMood());
        checkinRecordMapper.insert(record);

        // 更新统计
        CheckinStats stats = getOrCreateStats(userId);
        stats.setTotalDays(stats.getTotalDays() + 1);
        stats.setTotalMinutes(stats.getTotalMinutes() + (request.getStudyMinutes() != null ? request.getStudyMinutes() : 0));

        // 计算连续天数
        LocalDate lastDate = stats.getLastCheckinDate();
        boolean isNewRecord = false;

        if (lastDate != null && lastDate.plusDays(1).equals(today)) {
            // 连续打卡
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            if (stats.getCurrentStreak() > stats.getMaxStreak()) {
                stats.setMaxStreak(stats.getCurrentStreak());
                isNewRecord = true;
            }
        } else if (lastDate == null || !lastDate.equals(today)) {
            // 断签，重新开始
            stats.setCurrentStreak(1);
        }

        stats.setLastCheckinDate(today);
        checkinStatsMapper.updateById(stats);

        // 更新用户学习天数
        userFeignClient.incrementStudyDays(userId);

        CheckinResponse response = new CheckinResponse();
        response.setSuccess(true);
        response.setMessage(isNewRecord ? "恭喜！创造新纪录！" : "打卡成功");
        response.setCurrentStreak(stats.getCurrentStreak());
        response.setTotalDays(stats.getTotalDays());
        response.setIsNewRecord(isNewRecord);

        return response;
    }

    @Override
    public CheckinStatsResponse getStats(Long userId) {
        CheckinStats stats = getOrCreateStats(userId);
        LocalDate today = LocalDate.now();

        // 检查今日是否打卡
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
                .eq(CheckinRecord::getCheckinDate, today);
        boolean checkedToday = checkinRecordMapper.selectCount(wrapper) > 0;

        // 获取本月打卡日期
        YearMonth yearMonth = YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<LocalDate> checkinDates = checkinRecordMapper.selectCheckinDates(userId, startDate, endDate);

        CheckinStatsResponse response = new CheckinStatsResponse();
        response.setTotalDays(stats.getTotalDays());
        response.setCurrentStreak(stats.getCurrentStreak());
        response.setMaxStreak(stats.getMaxStreak());
        response.setTotalMinutes(stats.getTotalMinutes());
        response.setCheckedToday(checkedToday);
        response.setMonthlyCheckins(checkinDates.stream()
                .map(date -> date.format(DateTimeFormatter.ISO_DATE))
                .collect(Collectors.toList()));

        return response;
    }

    @Override
    public List<String> getCalendar(Long userId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<LocalDate> checkinDates = checkinRecordMapper.selectCheckinDates(userId, startDate, endDate);
        return checkinDates.stream()
                .map(date -> date.format(DateTimeFormatter.ISO_DATE))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<CheckinRecordResponse> getRecords(Long userId, Integer pageNum, Integer pageSize) {
        Page<CheckinRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
                .orderByDesc(CheckinRecord::getCheckinDate);

        Page<CheckinRecord> recordPage = checkinRecordMapper.selectPage(page, wrapper);

        List<CheckinRecordResponse> list = recordPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PageResult.of(list, recordPage.getTotal());
    }

    @Override
    public CheckinRecordResponse getTodayCheckin(Long userId) {
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
                .eq(CheckinRecord::getCheckinDate, LocalDate.now());

        CheckinRecord record = checkinRecordMapper.selectOne(wrapper);
        return record != null ? convertToResponse(record) : null;
    }

    @Override
    public List<CheckinRankResponse> getStreakRanking(Integer limit) {
        List<CheckinStats> statsList = checkinStatsMapper.selectStreakRanking(limit);
        return buildRankingResponse(statsList, "streak");
    }

    @Override
    public List<CheckinRankResponse> getTotalDaysRanking(Integer limit) {
        List<CheckinStats> statsList = checkinStatsMapper.selectTotalDaysRanking(limit);
        return buildRankingResponse(statsList, "totalDays");
    }

    @Override
    public Integer getUserRank(Long userId, String type) {
        // 简化实现：实际应该用SQL查询排名
        List<CheckinStats> allStats;
        if ("streak".equals(type)) {
            allStats = checkinStatsMapper.selectStreakRanking(1000);
        } else {
            allStats = checkinStatsMapper.selectTotalDaysRanking(1000);
        }

        for (int i = 0; i < allStats.size(); i++) {
            if (allStats.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return null;
    }

    private CheckinStats getOrCreateStats(Long userId) {
        LambdaQueryWrapper<CheckinStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinStats::getUserId, userId);
        CheckinStats stats = checkinStatsMapper.selectOne(wrapper);

        if (stats == null) {
            stats = new CheckinStats();
            stats.setUserId(userId);
            stats.setTotalDays(0);
            stats.setCurrentStreak(0);
            stats.setMaxStreak(0);
            stats.setTotalMinutes(0);
            checkinStatsMapper.insert(stats);
        }

        return stats;
    }

    private CheckinRecordResponse convertToResponse(CheckinRecord record) {
        CheckinRecordResponse response = new CheckinRecordResponse();
        response.setId(record.getId());
        response.setCheckinDate(record.getCheckinDate().format(DateTimeFormatter.ISO_DATE));
        response.setStudyMinutes(record.getStudyMinutes());
        response.setQuestionCount(record.getQuestionCount());
        response.setContent(record.getContent());
        response.setMood(record.getMood());
        return response;
    }

    private List<CheckinRankResponse> buildRankingResponse(List<CheckinStats> statsList, String type) {
        List<CheckinRankResponse> result = new ArrayList<>();
        int rank = 1;

        for (CheckinStats stats : statsList) {
            CheckinRankResponse response = new CheckinRankResponse();
            response.setRank(rank++);
            response.setUserId(stats.getUserId());
            response.setStreak(stats.getCurrentStreak());
            response.setTotalDays(stats.getTotalDays());

            // 获取用户信息
            try {
                Result<Map<String, Object>> userResult = userFeignClient.getUserInfo(stats.getUserId());
                if (userResult.isSuccess() && userResult.getData() != null) {
                    response.setNickname((String) userResult.getData().get("nickname"));
                    response.setAvatar((String) userResult.getData().get("avatar"));
                }
            } catch (Exception e) {
                log.warn("Failed to get user info for userId: {}", stats.getUserId());
            }

            result.add(response);
        }

        return result;
    }
}
