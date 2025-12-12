package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏题目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_favorite_question")
public class UserFavoriteQuestion extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;
}
