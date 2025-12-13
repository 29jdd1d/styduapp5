package com.studyapp.admin.feign;

import com.studyapp.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 打卡服务Feign客户端
 */
@FeignClient(name = "studyapp-checkin", path = "/checkin")
public interface CheckinFeignClient {

    /**
     * 获取今日打卡人数
     */
    @GetMapping("/inner/stats/today")
    Result<Long> getTodayCount();
}
