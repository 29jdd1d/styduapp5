package com.studyapp.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.checkin.entity.CheckinStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CheckinStatsMapper extends BaseMapper<CheckinStats> {

    /**
     * 获取连续打卡排行
     */
    List<CheckinStats> selectStreakRanking(@Param("limit") Integer limit);

    /**
     * 获取总天数排行
     */
    List<CheckinStats> selectTotalDaysRanking(@Param("limit") Integer limit);
}
