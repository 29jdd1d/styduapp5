package com.studyapp.practice.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "studyapp-user", path = "/user")
public interface UserFeignClient {

    /**
     * 增加做题数
     */
    @PostMapping("/inner/incrementTotalQuestions")
    Result<Void> incrementTotalQuestions(
            @RequestParam("userId") Long userId,
            @RequestParam("count") Integer count
    );
}
