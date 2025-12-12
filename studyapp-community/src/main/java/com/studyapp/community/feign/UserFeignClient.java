package com.studyapp.community.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "studyapp-user", path = "/user")
public interface UserFeignClient {

    /**
     * 获取用户基本信息
     */
    @GetMapping("/inner/info")
    Result<Map<String, Object>> getUserInfo(@RequestParam("userId") Long userId);

    /**
     * 批量获取用户信息
     */
    @GetMapping("/inner/batch")
    Result<List<Map<String, Object>>> getBatchUserInfo(@RequestParam("userIds") List<Long> userIds);
}
