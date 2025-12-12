# 考研学习小程序后端 - Spring Cloud 微服务架构

## 项目简介

基于 Spring Cloud + Nacos 2.4.3 的考研学习微信小程序后端，采用微服务架构，提供完整的考研学习功能。

## 技术栈

- **基础框架**: Spring Boot 3.2.0 + Spring Cloud 2023.0.0
- **服务发现/配置**: Spring Cloud Alibaba 2023.0.0.0-RC1 + Nacos 2.4.3
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.5
- **缓存**: Redis
- **认证**: JWT (jjwt 0.12.3)
- **网关**: Spring Cloud Gateway
- **服务调用**: OpenFeign
- **对象存储**: 腾讯云COS
- **API文档**: Knife4j 4.3.0

## 项目结构

```
studyapp-parent/
├── studyapp-common        # 公共模块 (工具类、配置、实体基类)
├── studyapp-gateway       # 网关服务 (端口: 8080)
├── studyapp-auth          # 认证服务 (端口: 8081)
├── studyapp-user          # 用户服务 (端口: 8082)
├── studyapp-course        # 课程服务 (端口: 8083)
├── studyapp-question      # 题库服务 (端口: 8084)
├── studyapp-practice      # 刷题服务 (端口: 8085)
├── studyapp-checkin       # 打卡服务 (端口: 8087)
├── studyapp-community     # 社区服务 (端口: 8088)
├── studyapp-admin         # 管理后台 (端口: 8089)
└── sql/                   # 数据库脚本
    └── init.sql
```

## 核心功能

### 1. 用户服务 (studyapp-user)
- 用户信息管理
- 专业管理
- 学习统计

### 2. 认证服务 (studyapp-auth)
- 微信小程序登录
- JWT令牌生成与刷新

### 3. 题库服务 (studyapp-question)
- 题目分类管理（树形结构）
- 题目管理（支持单选、多选、判断、填空、简答）

### 4. 课程服务 (studyapp-course)
- 课程管理
- 章节管理
- 视频播放（腾讯云COS预签名URL）
- 学习进度记录

### 5. 刷题服务 (studyapp-practice)
- 多模式练习（顺序、随机、错题、收藏）
- 错题本管理
- 题目收藏
- 试卷考试与批改

### 6. 打卡服务 (studyapp-checkin)
- 每日打卡
- 连续打卡统计
- 打卡排行榜
- 打卡日历

### 7. 社区服务 (studyapp-community)
- 帖子发布与管理
- 评论功能
- 点赞功能

### 8. 管理后台 (studyapp-admin)
- 管理员登录
- 仪表盘统计

## 快速开始

### 1. 环境准备

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.4.3

### 2. 数据库初始化

```bash
mysql -u root -p < sql/init.sql
```

### 3. 启动 Nacos

```bash
# 单机模式启动
sh startup.sh -m standalone
```

### 4. 修改配置

修改各服务的 `application.yml` 中的数据库、Redis、Nacos等配置。

### 5. 构建项目

```bash
mvn clean package -DskipTests
```

### 6. 启动服务

按以下顺序启动各服务：

1. studyapp-gateway
2. studyapp-auth
3. studyapp-user
4. studyapp-question
5. studyapp-course
6. studyapp-practice
7. studyapp-checkin
8. studyapp-community
9. studyapp-admin

## API 文档

启动服务后访问:
- 网关聚合文档: http://localhost:8080/doc.html
- 各服务单独文档: http://localhost:{port}/doc.html

## 配置说明

### Nacos 配置

在 Nacos 控制台创建各服务的配置文件（可选），配置优先级：Nacos > 本地配置。

### 腾讯云 COS 配置

在各服务配置中设置：

```yaml
cos:
  secret-id: your-secret-id
  secret-key: your-secret-key
  region: ap-guangzhou
  bucket: your-bucket-name
```

### 微信小程序配置

在 `studyapp-auth` 服务配置：

```yaml
wx:
  miniapp:
    app-id: your-app-id
    app-secret: your-app-secret
```

## 数据库设计

项目采用分库设计，每个服务独立数据库：

- `studyapp_user` - 用户相关
- `studyapp_question` - 题库相关
- `studyapp_course` - 课程相关
- `studyapp_practice` - 刷题相关
- `studyapp_checkin` - 打卡相关
- `studyapp_community` - 社区相关
- `studyapp_admin` - 管理后台

## 默认账号

管理后台默认账号：
- 用户名: admin
- 密码: admin123

## License

MIT License