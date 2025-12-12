package com.studyapp.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.checkin.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

    /**
     * 获取指定月份的打卡日期列表
     */
    @Select("SELECT checkin_date FROM checkin_record WHERE user_id = #{userId} " +
            "AND checkin_date >= #{startDate} AND checkin_date <= #{endDate}")
    List<LocalDate> selectCheckinDates(@Param("userId") Long userId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}
