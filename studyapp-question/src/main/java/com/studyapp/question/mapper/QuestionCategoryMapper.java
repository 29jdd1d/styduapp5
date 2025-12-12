package com.studyapp.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.question.entity.QuestionCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 题目分类Mapper
 */
@Mapper
public interface QuestionCategoryMapper extends BaseMapper<QuestionCategory> {

    /**
     * 更新题目数量
     */
    void updateQuestionCount(@Param("categoryId") Long categoryId, @Param("count") Integer count);
}
