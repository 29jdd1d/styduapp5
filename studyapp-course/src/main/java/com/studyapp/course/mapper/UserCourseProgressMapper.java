package com.studyapp.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.course.entity.UserCourseProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户学习进度Mapper
 */
@Mapper
public interface UserCourseProgressMapper extends BaseMapper<UserCourseProgress> {
}
