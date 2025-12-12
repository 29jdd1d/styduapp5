package com.studyapp.practice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyapp.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 练习记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("practice_record")
public class PracticeRecord extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 专业ID
     */
    private Long majorId;

    /**
     * 分类ID（可空=随机刷题）
     */
    private Long categoryId;

    /**
     * 模式 1顺序 2随机 3错题 4收藏
     */
    private Integer mode;

    /**
     * 总题数
     */
    private Integer totalCount;

    /**
     * 正确数
     */
    private Integer correctCount;

    /**
     * 错误数
     */
    private Integer wrongCount;

    /**
     * 状态 0进行中 1已完成
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
