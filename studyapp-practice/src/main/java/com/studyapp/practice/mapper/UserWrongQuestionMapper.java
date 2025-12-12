package com.studyapp.practice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.practice.entity.UserWrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserWrongQuestionMapper extends BaseMapper<UserWrongQuestion> {
    
    /**
     * 获取用户错题ID列表
     */
    List<Long> selectWrongQuestionIds(@Param("userId") Long userId, @Param("limit") Integer limit);
}
