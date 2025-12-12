package com.studyapp.common.constant;

/**
 * Redis常量
 */
public class RedisConstants {

    /**
     * 用户Token前缀
     */
    public static final String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 管理员Token前缀
     */
    public static final String ADMIN_TOKEN_PREFIX = "admin:token:";

    /**
     * 微信session_key前缀
     */
    public static final String WX_SESSION_KEY_PREFIX = "wx:session:";

    /**
     * 用户信息缓存前缀
     */
    public static final String USER_INFO_PREFIX = "user:info:";

    /**
     * 题目详情缓存前缀
     */
    public static final String QUESTION_DETAIL_PREFIX = "question:detail:";

    /**
     * 题目分类缓存前缀
     */
    public static final String QUESTION_CATEGORY_PREFIX = "question:category:";

    /**
     * 课程详情缓存前缀
     */
    public static final String COURSE_DETAIL_PREFIX = "course:detail:";

    /**
     * 打卡排行榜
     */
    public static final String CHECKIN_RANK_PREFIX = "checkin:rank:";

    /**
     * 今日打卡用户集合
     */
    public static final String CHECKIN_TODAY_PREFIX = "checkin:today:";

    /**
     * 帖子点赞用户集合
     */
    public static final String POST_LIKE_PREFIX = "post:like:";

    /**
     * 评论点赞用户集合
     */
    public static final String COMMENT_LIKE_PREFIX = "comment:like:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 默认过期时间（秒）
     */
    public static final long DEFAULT_EXPIRE = 3600;

    /**
     * Token过期时间（秒）- 7天
     */
    public static final long TOKEN_EXPIRE = 7 * 24 * 3600;

    /**
     * 验证码过期时间（秒）- 5分钟
     */
    public static final long CAPTCHA_EXPIRE = 300;
}
