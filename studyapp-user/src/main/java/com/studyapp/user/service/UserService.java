package com.studyapp.user.service;

import com.studyapp.user.dto.MajorResponse;
import com.studyapp.user.dto.UserInfoResponse;
import com.studyapp.user.dto.UserUpdateRequest;
import com.studyapp.user.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 获取用户信息
     */
    UserInfoResponse getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUser(Long userId, UserUpdateRequest request);

    /**
     * 根据openid获取用户
     */
    User getByOpenid(String openid);

    /**
     * 根据ID获取用户
     */
    User getById(Long userId);

    /**
     * 创建用户
     */
    User createUser(String openid, String nickname, String avatar);

    /**
     * 获取专业列表
     */
    List<MajorResponse> getMajorList();

    /**
     * 选择专业
     */
    void selectMajor(Long userId, Long majorId);

    /**
     * 增加学习天数
     */
    void incrementStudyDays(Long userId);

    /**
     * 增加做题数
     */
    void incrementTotalQuestions(Long userId, Integer count);

    /**
     * 获取用户信息（Map格式，供Feign调用）
     */
    Map<String, Object> getUserInfoMap(User user);

    /**
     * 批量获取用户信息（Map格式，供Feign调用）
     */
    List<Map<String, Object>> getBatchUserInfoMap(List<Long> userIds);
}
