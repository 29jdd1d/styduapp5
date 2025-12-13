package com.studyapp.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.result.Result;
import com.studyapp.community.entity.Post;
import com.studyapp.community.mapper.PostMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 社区统计内部接口
 */
@Tag(name = "社区统计内部接口")
@RestController
@RequestMapping("/community/inner/stats")
@RequiredArgsConstructor
public class CommunityStatsController {

    private final PostMapper postMapper;

    @Operation(summary = "获取帖子总数")
    @GetMapping("/total")
    public Result<Long> getTotalCount() {
        Long count = postMapper.selectCount(new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, CommonConstants.STATUS_NORMAL));
        return Result.success(count);
    }
}
