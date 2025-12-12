package com.studyapp.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 课程Mapper
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 增加观看次数
     */
    void incrementViewCount(@Param("courseId") Long courseId);
}
