package com.studyapp.common.constant;

/**
 * 通用常量
 */
public class CommonConstants {

    /**
     * 状态：禁用
     */
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 状态：正常
     */
    public static final Integer STATUS_NORMAL = 1;

    /**
     * 是
     */
    public static final Integer YES = 1;

    /**
     * 否
     */
    public static final Integer NO = 0;

    /**
     * 题型：单选
     */
    public static final Integer QUESTION_TYPE_SINGLE = 1;

    /**
     * 题型：多选
     */
    public static final Integer QUESTION_TYPE_MULTIPLE = 2;

    /**
     * 题型：判断
     */
    public static final Integer QUESTION_TYPE_JUDGE = 3;

    /**
     * 题型：填空
     */
    public static final Integer QUESTION_TYPE_FILL = 4;

    /**
     * 题型：简答
     */
    public static final Integer QUESTION_TYPE_SHORT_ANSWER = 5;

    /**
     * 难度：简单
     */
    public static final Integer DIFFICULTY_EASY = 1;

    /**
     * 难度：中等
     */
    public static final Integer DIFFICULTY_MEDIUM = 2;

    /**
     * 难度：困难
     */
    public static final Integer DIFFICULTY_HARD = 3;

    /**
     * 练习模式：顺序
     */
    public static final Integer PRACTICE_MODE_SEQUENCE = 1;

    /**
     * 练习模式：随机
     */
    public static final Integer PRACTICE_MODE_RANDOM = 2;

    /**
     * 练习模式：错题
     */
    public static final Integer PRACTICE_MODE_WRONG = 3;

    /**
     * 练习模式：收藏
     */
    public static final Integer PRACTICE_MODE_FAVORITE = 4;

    /**
     * 帖子分类：经验分享
     */
    public static final String POST_CATEGORY_EXPERIENCE = "experience";

    /**
     * 帖子分类：问题求助
     */
    public static final String POST_CATEGORY_QUESTION = "question";

    /**
     * 帖子分类：资料分享
     */
    public static final String POST_CATEGORY_RESOURCE = "resource";

    /**
     * 帖子分类：日常打卡
     */
    public static final String POST_CATEGORY_CHECKIN = "checkin";

    /**
     * 用户请求头中的用户ID
     */
    public static final String HEADER_USER_ID = "X-User-Id";

    /**
     * 用户请求头中的管理员ID
     */
    public static final String HEADER_ADMIN_ID = "X-Admin-Id";
}
