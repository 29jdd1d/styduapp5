package com.studyapp.auth.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.studyapp.auth.config.WxMiniappConfig;
import com.studyapp.auth.dto.LoginResponse;
import com.studyapp.auth.dto.WxLoginRequest;
import com.studyapp.auth.feign.UserFeignClient;
import com.studyapp.auth.service.AuthService;
import com.studyapp.common.constant.RedisConstants;
import com.studyapp.common.exception.BusinessException;
import com.studyapp.common.result.Result;
import com.studyapp.common.result.ResultCode;
import com.studyapp.common.utils.JwtUtils;
import com.studyapp.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WxMiniappConfig wxMiniappConfig;
    private final UserFeignClient userFeignClient;
    private final RedisUtils redisUtils;
    private final JwtUtils jwtUtils;

    /**
     * 微信登录API地址
     */
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public LoginResponse wxLogin(WxLoginRequest request) {
        // 1. 调用微信接口获取openid
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_LOGIN_URL, wxMiniappConfig.getAppId(), wxMiniappConfig.getAppSecret(), request.getCode());

        String response = HttpUtil.get(url);
        log.info("微信登录响应: {}", response);

        JSONObject jsonObject = JSONUtil.parseObj(response);

        // 检查是否有错误
        if (jsonObject.containsKey("errcode") && jsonObject.getInt("errcode") != 0) {
            log.error("微信登录失败: {}", jsonObject.getStr("errmsg"));
            throw new BusinessException(ResultCode.WX_LOGIN_ERROR, jsonObject.getStr("errmsg"));
        }

        String openid = jsonObject.getStr("openid");
        String sessionKey = jsonObject.getStr("session_key");

        if (openid == null || openid.isEmpty()) {
            throw new BusinessException(ResultCode.WX_LOGIN_ERROR);
        }

        // 2. 保存session_key到Redis
        redisUtils.set(RedisConstants.WX_SESSION_KEY_PREFIX + openid, sessionKey, 
                RedisConstants.TOKEN_EXPIRE, TimeUnit.SECONDS);

        // 3. 查询用户是否存在
        Result<Map<String, Object>> userResult = userFeignClient.getByOpenid(openid);
        Map<String, Object> userInfo;
        boolean isNewUser = false;

        if (!userResult.isSuccess() || userResult.getData() == null) {
            // 4. 新用户，创建用户
            Result<Map<String, Object>> createResult = userFeignClient.createUser(
                    openid, request.getNickname(), request.getAvatar());
            if (!createResult.isSuccess() || createResult.getData() == null) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR, "创建用户失败");
            }
            userInfo = createResult.getData();
            isNewUser = true;
        } else {
            userInfo = userResult.getData();
        }

        // 5. 生成Token
        Long userId = Long.valueOf(userInfo.get("id").toString());
        String token = jwtUtils.generateToken(userId, openid);

        // 6. 保存Token到Redis
        redisUtils.set(RedisConstants.USER_TOKEN_PREFIX + userId, token,
                RedisConstants.TOKEN_EXPIRE, TimeUnit.SECONDS);

        // 7. 构建响应
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUserId(userId);
        loginResponse.setNickname(userInfo.get("nickname") != null ? userInfo.get("nickname").toString() : null);
        loginResponse.setAvatar(userInfo.get("avatar") != null ? userInfo.get("avatar").toString() : null);
        loginResponse.setIsNewUser(isNewUser);

        // 判断是否已选择专业
        Object majorId = userInfo.get("majorId");
        loginResponse.setHasMajor(majorId != null);
        if (majorId != null) {
            loginResponse.setMajorId(Long.valueOf(majorId.toString()));
            loginResponse.setMajorName(userInfo.get("majorName") != null ? userInfo.get("majorName").toString() : null);
        }

        return loginResponse;
    }

    @Override
    public String refreshToken(String oldToken) {
        // 验证旧Token
        Long userId = jwtUtils.getUserId(oldToken);
        if (userId == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 获取openid（从旧Token解析）
        var claims = jwtUtils.parseToken(oldToken);
        if (claims == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        String openid = claims.get("openid", String.class);

        // 生成新Token
        String newToken = jwtUtils.generateToken(userId, openid);

        // 更新Redis中的Token
        redisUtils.set(RedisConstants.USER_TOKEN_PREFIX + userId, newToken,
                RedisConstants.TOKEN_EXPIRE, TimeUnit.SECONDS);

        return newToken;
    }

    @Override
    public void logout(String token) {
        Long userId = jwtUtils.getUserId(token);
        if (userId != null) {
            // 删除Redis中的Token
            redisUtils.delete(RedisConstants.USER_TOKEN_PREFIX + userId);
        }
    }
}
