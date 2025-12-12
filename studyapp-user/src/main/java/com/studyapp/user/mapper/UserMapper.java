package com.studyapp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 增加学习天数
     */
    void incrementStudyDays(@Param("userId") Long userId);

    /**
     * 增加做题数
     */
    void incrementTotalQuestions(@Param("userId") Long userId, @Param("count") Integer count);
}
