package com.studyapp.practice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.practice.entity.UserFavoriteQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFavoriteQuestionMapper extends BaseMapper<UserFavoriteQuestion> {

    /**
     * 获取用户收藏题目ID列表
     */
    List<Long> selectFavoriteQuestionIds(@Param("userId") Long userId, @Param("limit") Integer limit);
}
