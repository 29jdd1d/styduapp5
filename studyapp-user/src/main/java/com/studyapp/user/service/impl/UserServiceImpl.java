package com.studyapp.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.ResultCode;
import com.studyapp.user.dto.MajorResponse;
import com.studyapp.user.dto.UserInfoResponse;
import com.studyapp.user.dto.UserUpdateRequest;
import com.studyapp.user.entity.Major;
import com.studyapp.user.entity.User;
import com.studyapp.user.mapper.MajorMapper;
import com.studyapp.user.mapper.UserMapper;
import com.studyapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MajorMapper majorMapper;

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserInfoResponse response = new UserInfoResponse();
        BeanUtil.copyProperties(user, response);

        // 获取专业名称
        if (user.getMajorId() != null) {
            Major major = majorMapper.selectById(user.getMajorId());
            if (major != null) {
                response.setMajorName(major.getName());
            }
        }

        return response;
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getExamYear() != null) {
            user.setExamYear(request.getExamYear());
        }

        userMapper.updateById(user);
    }

    @Override
    public User getByOpenid(String openid) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openid)
        );
    }

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional
    public User createUser(String openid, String nickname, String avatar) {
        User user = new User();
        user.setOpenid(openid);
        user.setNickname(nickname != null ? nickname : "考研人" + RandomUtil.randomNumbers(6));
        user.setAvatar(avatar);
        user.setGender(0);
        user.setStudyDays(0);
        user.setTotalQuestions(0);
        user.setStatus(CommonConstants.STATUS_NORMAL);

        userMapper.insert(user);
        log.info("创建新用户: {}", user.getId());
        return user;
    }

    @Override
    public List<MajorResponse> getMajorList() {
        List<Major> majors = majorMapper.selectList(
                new LambdaQueryWrapper<Major>()
                        .eq(Major::getStatus, CommonConstants.STATUS_NORMAL)
                        .orderByAsc(Major::getSort)
        );

        return majors.stream().map(major -> {
            MajorResponse response = new MajorResponse();
            BeanUtil.copyProperties(major, response);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void selectMajor(Long userId, Long majorId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证专业是否存在
        Major major = majorMapper.selectById(majorId);
        if (major == null || major.getStatus() != CommonConstants.STATUS_NORMAL) {
            throw new BusinessException("专业不存在或已禁用");
        }

        user.setMajorId(majorId);
        userMapper.updateById(user);
        log.info("用户 {} 选择专业: {}", userId, majorId);
    }

    @Override
    public void incrementStudyDays(Long userId) {
        userMapper.incrementStudyDays(userId);
    }

    @Override
    public void incrementTotalQuestions(Long userId, Integer count) {
        userMapper.incrementTotalQuestions(userId, count);
    }

    @Override
    public Map<String, Object> getUserInfoMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("openid", user.getOpenid());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getAvatar());
        map.put("phone", user.getPhone());
        map.put("gender", user.getGender());
        map.put("majorId", user.getMajorId());
        map.put("examYear", user.getExamYear());
        map.put("studyDays", user.getStudyDays());
        map.put("totalQuestions", user.getTotalQuestions());
        map.put("status", user.getStatus());
        map.put("createTime", user.getCreateTime());

        // 获取专业名称
        if (user.getMajorId() != null) {
            Major major = majorMapper.selectById(user.getMajorId());
            if (major != null) {
                map.put("majorName", major.getName());
            }
        }

        return map;
    }

    @Override
    public List<Map<String, Object>> getBatchUserInfoMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        return users.stream()
                .map(this::getUserInfoMap)
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalCount() {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1));
    }

    @Override
    public Long getTodayNewCount() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .ge(User::getCreateTime, todayStart));
    }
}
