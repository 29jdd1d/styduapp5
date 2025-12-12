package com.studyapp.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别 0未知 1男 2女
     */
    private Integer gender;

    /**
     * 选择的专业ID
     */
    private Long majorId;

    /**
     * 考研年份
     */
    private Integer examYear;

    /**
     * 累计学习天数
     */
    private Integer studyDays;

    /**
     * 累计做题数
     */
    private Integer totalQuestions;

    /**
     * 状态 0禁用 1正常
     */
    private Integer status;
}
