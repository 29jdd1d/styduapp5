package com.studyapp.auth.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "studyapp-user", path = "/user")
public interface UserFeignClient {

    /**
     * 根据openid获取用户
     */
    @GetMapping("/inner/getByOpenid")
    Result<Map<String, Object>> getByOpenid(@RequestParam("openid") String openid);

    /**
     * 创建用户
     */
    @PostMapping("/inner/create")
    Result<Map<String, Object>> createUser(
            @RequestParam("openid") String openid,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "avatar", required = false) String avatar
    );
}
