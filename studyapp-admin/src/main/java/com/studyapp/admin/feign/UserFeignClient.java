package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "studyapp-user", path = "/user")
public interface UserFeignClient {

    /**
     * 获取用户总数
     */
    @GetMapping("/inner/stats/total")
    Result<Long> getTotalCount();

    /**
     * 获取今日新增用户数
     */
    @GetMapping("/inner/stats/today")
    Result<Long> getTodayNewCount();
}
