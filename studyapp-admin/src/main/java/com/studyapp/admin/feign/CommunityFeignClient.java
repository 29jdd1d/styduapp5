package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 社区服务Feign客户端
 */
@FeignClient(name = "studyapp-community", path = "/community")
public interface CommunityFeignClient {

    /**
     * 获取帖子总数
     */
    @GetMapping("/inner/stats/total")
    Result<Long> getTotalCount();
}
