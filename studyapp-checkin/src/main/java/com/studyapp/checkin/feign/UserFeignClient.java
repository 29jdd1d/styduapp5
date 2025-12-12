package com.studyapp.checkin.feign;

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
     * 获取用户基本信息
     */
    @GetMapping("/inner/info")
    Result<Map<String, Object>> getUserInfo(@RequestParam("userId") Long userId);

    /**
     * 增加学习天数
     */
    @PostMapping("/inner/incrementStudyDays")
    Result<Void> incrementStudyDays(@RequestParam("userId") Long userId);
}
