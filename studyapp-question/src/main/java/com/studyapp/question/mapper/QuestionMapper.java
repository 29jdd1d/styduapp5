package com.studyapp.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.question.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目Mapper
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 随机获取题目ID
     */
    List<Long> selectRandomIds(
            @Param("majorId") Long majorId,
            @Param("categoryId") Long categoryId,
            @Param("count") Integer count
    );

    /**
     * 根据分类ID列表获取题目
     */
    List<Question> selectByCategoryIds(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("limit") Integer limit
    );
}
