-- ============================================
-- 考研学习小程序 数据库初始化脚本
-- 创建时间: 2024
-- ============================================

-- ============================================
-- 1. 用户服务数据库 studyapp_user
-- ============================================
CREATE DATABASE IF NOT EXISTS `studyapp_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `studyapp_user`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `openid` VARCHAR(64) NOT NULL COMMENT '微信openid',
    `unionid` VARCHAR(64) DEFAULT NULL COMMENT '微信unionid',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    `major_id` BIGINT DEFAULT NULL COMMENT '选择的考研专业ID',
    `study_days` INT DEFAULT 0 COMMENT '学习天数',
    `total_questions` INT DEFAULT 0 COMMENT '做题总数',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 专业表
CREATE TABLE IF NOT EXISTS `major` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '专业ID',
    `name` VARCHAR(100) NOT NULL COMMENT '专业名称',
    `code` VARCHAR(20) DEFAULT NULL COMMENT '专业代码',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '学科门类',
    `description` TEXT DEFAULT NULL COMMENT '专业描述',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

-- 插入示例专业数据
INSERT INTO `major` (`name`, `code`, `category`, `description`, `sort`) VALUES
('计算机科学与技术', '0812', '工学', '计算机科学与技术专业', 1),
('软件工程', '0835', '工学', '软件工程专业', 2),
('电子信息工程', '0807', '工学', '电子信息工程专业', 3),
('通信工程', '0810', '工学', '通信工程专业', 4),
('金融学', '0202', '经济学', '金融学专业', 5),
('会计学', '1202', '管理学', '会计学专业', 6),
('法学', '0301', '法学', '法学专业', 7),
('英语', '0502', '文学', '英语语言文学专业', 8);

-- ============================================
-- 2. 题库服务数据库 studyapp_question
-- ============================================
CREATE DATABASE IF NOT EXISTS `studyapp_question` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `studyapp_question`;

-- 题目分类表
CREATE TABLE IF NOT EXISTS `question_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `major_id` BIGINT NOT NULL COMMENT '所属专业ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `level` TINYINT DEFAULT 1 COMMENT '层级 1一级 2二级 3三级',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_major_id` (`major_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目分类表';

-- 题目表
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `major_id` BIGINT NOT NULL COMMENT '专业ID',
    `type` TINYINT NOT NULL COMMENT '题型 1单选 2多选 3判断 4填空 5简答',
    `difficulty` TINYINT DEFAULT 1 COMMENT '难度 1简单 2中等 3困难',
    `content` TEXT NOT NULL COMMENT '题目内容',
    `options` JSON DEFAULT NULL COMMENT '选项（JSON格式）',
    `answer` VARCHAR(500) NOT NULL COMMENT '答案',
    `analysis` TEXT DEFAULT NULL COMMENT '解析',
    `source` VARCHAR(200) DEFAULT NULL COMMENT '来源（真题年份等）',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_major_id` (`major_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- ============================================
-- 3. 课程服务数据库 studyapp_course
-- ============================================
CREATE DATABASE IF NOT EXISTS `studyapp_course` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `studyapp_course`;

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    `major_id` BIGINT NOT NULL COMMENT '专业ID',
    `title` VARCHAR(200) NOT NULL COMMENT '课程标题',
    `description` TEXT DEFAULT NULL COMMENT '课程描述',
    `cover` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    `teacher` VARCHAR(50) DEFAULT NULL COMMENT '讲师',
    `teacher_avatar` VARCHAR(255) DEFAULT NULL COMMENT '讲师头像',
    `teacher_intro` TEXT DEFAULT NULL COMMENT '讲师介绍',
    `total_hours` DECIMAL(5,1) DEFAULT 0 COMMENT '总课时',
    `chapter_count` INT DEFAULT 0 COMMENT '章节数',
    `video_count` INT DEFAULT 0 COMMENT '视频数',
    `student_count` INT DEFAULT 0 COMMENT '学习人数',
    `price` DECIMAL(10,2) DEFAULT 0 COMMENT '价格，0表示免费',
    `original_price` DECIMAL(10,2) DEFAULT 0 COMMENT '原价',
    `is_free` TINYINT DEFAULT 1 COMMENT '是否免费 0否 1是',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0下架 1上架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 课程章节表
CREATE TABLE IF NOT EXISTS `course_chapter` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';

-- 课程视频表
CREATE TABLE IF NOT EXISTS `course_video` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '视频ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `title` VARCHAR(200) NOT NULL COMMENT '视频标题',
    `video_url` VARCHAR(500) NOT NULL COMMENT '视频地址（COS key）',
    `duration` INT DEFAULT 0 COMMENT '时长（秒）',
    `is_free` TINYINT DEFAULT 0 COMMENT '是否免费试看 0否 1是',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_chapter_id` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程视频表';

-- 用户课程学习进度表
CREATE TABLE IF NOT EXISTS `user_course_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `video_id` BIGINT NOT NULL COMMENT '视频ID',
    `progress` INT DEFAULT 0 COMMENT '播放进度（秒）',
    `duration` INT DEFAULT 0 COMMENT '视频总时长（秒）',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成 0否 1是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    KEY `idx_user_course` (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户课程学习进度表';

-- ============================================
-- 4. 刷题服务数据库 studyapp_practice
-- ============================================
CREATE DATABASE IF NOT EXISTS `studyapp_practice` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `studyapp_practice`;

-- 练习记录表
CREATE TABLE IF NOT EXISTS `practice_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `major_id` BIGINT NOT NULL COMMENT '专业ID',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `mode` TINYINT NOT NULL COMMENT '模式 1顺序 2随机 3错题 4收藏',
    `total_count` INT DEFAULT 0 COMMENT '总题数',
    `correct_count` INT DEFAULT 0 COMMENT '正确数',
    `wrong_count` INT DEFAULT 0 COMMENT '错误数',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0进行中 1已完成',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习记录表';

-- 练习详情表
CREATE TABLE IF NOT EXISTS `practice_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `record_id` BIGINT NOT NULL COMMENT '练习记录ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `user_answer` VARCHAR(500) DEFAULT NULL COMMENT '用户答案',
    `is_correct` TINYINT DEFAULT 0 COMMENT '是否正确 0错 1对',
    `time_spent` INT DEFAULT 0 COMMENT '用时（秒）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习详情表';

-- 错题本表
CREATE TABLE IF NOT EXISTS `user_wrong_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `wrong_count` INT DEFAULT 1 COMMENT '错误次数',
    `correct_count` INT DEFAULT 0 COMMENT '正确次数',
    `last_wrong_time` DATETIME DEFAULT NULL COMMENT '最近错误时间',
    `is_removed` TINYINT DEFAULT 0 COMMENT '是否移出 0否 1是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question` (`user_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本表';

-- 收藏题目表
CREATE TABLE IF NOT EXISTS `user_favorite_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question` (`user_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏题目表';

-- 试卷表
CREATE TABLE IF NOT EXISTS `exam` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
    `title` VARCHAR(200) NOT NULL COMMENT '试卷名称',
    `major_id` BIGINT NOT NULL COMMENT '专业ID',
    `total_score` INT DEFAULT 100 COMMENT '总分',
    `pass_score` INT DEFAULT 60 COMMENT '及格分',
    `duration` INT DEFAULT 120 COMMENT '考试时长（分钟）',
    `question_count` INT DEFAULT 0 COMMENT '题目数量',
    `type` TINYINT DEFAULT 1 COMMENT '类型 1模拟卷 2真题卷',
    `year` INT DEFAULT NULL COMMENT '年份',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0下架 1上架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷表';

-- 试卷题目关联表
CREATE TABLE IF NOT EXISTS `exam_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `exam_id` BIGINT NOT NULL COMMENT '试卷ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `score` INT DEFAULT 1 COMMENT '该题分值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷题目关联表';

-- 用户考试记录表
CREATE TABLE IF NOT EXISTS `user_exam_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `exam_id` BIGINT NOT NULL COMMENT '试卷ID',
    `score` INT DEFAULT 0 COMMENT '得分',
    `correct_count` INT DEFAULT 0 COMMENT '正确数',
    `wrong_count` INT DEFAULT 0 COMMENT '错误数',
    `time_spent` INT DEFAULT 0 COMMENT '用时（秒）',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0进行中 1已交卷',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `submit_time` DATETIME DEFAULT NULL COMMENT '交卷时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户考试记录表';

-- 用户考试答题详情表
CREATE TABLE IF NOT EXISTS `user_exam_answer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `record_id` BIGINT NOT NULL COMMENT '考试记录ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `user_answer` VARCHAR(500) DEFAULT NULL COMMENT '用户答案',
    `is_correct` TINYINT DEFAULT 0 COMMENT '是否正确 0错 1对',
    `score` INT DEFAULT 0 COMMENT '得分',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户考试答题详情表';

-- ============================================
-- 5. 打卡服务数据库 studyapp_checkin
-- ============================================
CREATE DATABASE IF NOT EXISTS `studyapp_checkin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `studyapp_checkin`;

-- 打卡记录表
CREATE TABLE IF NOT EXISTS `checkin_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `checkin_date` DATE NOT NULL COMMENT '打卡日期',
    `study_minutes` INT DEFAULT 0 COMMENT '学习时长（分钟）',
    `question_count` INT DEFAULT 0 COMMENT '做题数',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '学习内容描述',
    `mood` TINYINT DEFAULT NULL COMMENT '打卡心情 1-5',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';

-- 打卡统计表
CREATE TABLE IF NOT EXISTS `checkin_stats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_days` INT DEFAULT 0 COMMENT '总打卡天数',
    `current_streak` INT DEFAULT 0 COMMENT '当前连续天数',
    `max_streak` INT DEFAULT 0 COMMENT '最长连续天数',
    `total_minutes` INT DEFAULT 0 COMMENT '总学习时长（分钟）',
    `last_checkin_date` DATE DEFAULT NULL COMMENT '最近打卡日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡统计表';

-- ============================================
-- 6. 社区服务数据库 studyapp_community
-- ============================================
CREATE DATABASE IF NOT EXISTS `studyapp_community` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `studyapp_community`;

-- 帖子表
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `images` JSON DEFAULT NULL COMMENT '图片（JSON数组）',
    `major_id` BIGINT DEFAULT NULL COMMENT '专业ID（可空，表示全站）',
    `type` TINYINT DEFAULT 1 COMMENT '类型 1经验分享 2问题求助 3资料分享 4闲聊灌水',
    `view_count` INT DEFAULT 0 COMMENT '浏览数',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶 0否 1是',
    `is_essence` TINYINT DEFAULT 0 COMMENT '是否精华 0否 1是',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0待审核 1正常 2已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- 评论表
CREATE TABLE IF NOT EXISTS `post_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID（0为一级评论）',
    `reply_user_id` BIGINT DEFAULT NULL COMMENT '回复的用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0待审核 1正常 2已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 帖子点赞表
CREATE TABLE IF NOT EXISTS `post_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子点赞表';

-- 评论点赞表
CREATE TABLE IF NOT EXISTS `comment_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `comment_id` BIGINT NOT NULL COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

-- ============================================
-- 7. 管理后台数据库 studyapp_admin
-- ============================================
CREATE DATABASE IF NOT EXISTS `studyapp_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `studyapp_admin`;

-- 管理员用户表
CREATE TABLE IF NOT EXISTS `admin_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(64) NOT NULL COMMENT '密码（MD5）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` TINYINT DEFAULT 2 COMMENT '角色 1超级管理员 2普通管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- 插入默认管理员账号 (密码: admin123 的MD5值)
INSERT INTO `admin_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '超级管理员', 1, 1);

-- ============================================
-- 完成
-- ============================================
