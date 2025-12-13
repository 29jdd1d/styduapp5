package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 题库服务Feign客户端
 */
@FeignClient(name = "studyapp-question", path = "/question")
public interface QuestionFeignClient {

    /**
     * 获取题目总数
     */
    @GetMapping("/inner/stats/total")
    Result<Long> getTotalCount();
}
