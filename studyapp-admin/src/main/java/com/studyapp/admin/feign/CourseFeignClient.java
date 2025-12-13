package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 课程服务Feign客户端
 */
@FeignClient(name = "studyapp-course", path = "/course")
public interface CourseFeignClient {

    /**
     * 获取课程总数
     */
    @GetMapping("/inner/stats/total")
    Result<Long> getTotalCount();
}
