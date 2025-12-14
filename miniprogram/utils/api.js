/**
 * API 接口封装
 */
const { get, post, put, del, upload } = require('./request')

// ==================== 认证接口 ====================
const authApi = {
  // 微信登录
  login: (data) => post('/auth/wx/login', data),
  // 刷新Token
  refreshToken: () => post('/auth/refresh'),
  // 退出登录
  logout: () => post('/auth/logout')
}

// ==================== 用户接口 ====================
const userApi = {
  // 获取用户信息
  getUserInfo: () => get('/user/info'),
  // 更新用户信息
  updateUserInfo: (data) => put('/user/update', data),
  // 获取专业列表
  getMajorList: () => get('/user/major/list'),
  // 选择专业
  selectMajor: (majorId) => post('/user/major/select', { majorId })
}

// ==================== 课程接口 ====================
const courseApi = {
  // 获取课程列表
  getCourseList: (majorId) => get('/course/list', { majorId }),
  // 获取课程详情
  getCourseDetail: (courseId) => get(`/course/detail/${courseId}`),
  // 获取视频播放地址
  getVideoUrl: (videoId) => get(`/course/video/${videoId}`),
  // 保存学习进度
  saveProgress: (data) => post('/course/progress/save', data),
  // 获取学习进度
  getProgress: (courseId) => get(`/course/progress/${courseId}`),
  // 获取我的课程
  getMyCourses: () => get('/course/my')
}

// ==================== 题库接口 ====================
const questionApi = {
  // 获取分类树
  getCategoryTree: (majorId) => get('/question/category/tree', { majorId }),
  // 获取题目列表
  getQuestionList: (categoryId, includeChildren = false) => get('/question/list', { categoryId, includeChildren }),
  // 获取题目详情
  getQuestionDetail: (questionId) => get(`/question/detail/${questionId}`),
  // 随机获取题目
  getRandomQuestions: (majorId, categoryId, count = 10) => get('/question/random', { majorId, categoryId, count })
}

// ==================== 练习接口 ====================
const practiceApi = {
  // 开始练习
  startPractice: (data) => post('/practice/start', data),
  // 提交答案
  submitAnswer: (data) => post('/practice/submit', data),
  // 结束练习
  finishPractice: (recordId) => post(`/practice/finish/${recordId}`),
  // 获取练习记录
  getPracticeRecords: (pageNum = 1, pageSize = 10) => get('/practice/records', { pageNum, pageSize }),
  // 获取错题本
  getWrongQuestions: (pageNum = 1, pageSize = 10) => get('/practice/wrong', { pageNum, pageSize }),
  // 移除错题
  removeWrongQuestion: (questionId) => post(`/practice/wrong/remove/${questionId}`),
  // 收藏题目
  favoriteQuestion: (questionId) => post(`/practice/favorite/${questionId}`),
  // 取消收藏
  unfavoriteQuestion: (questionId) => del(`/practice/favorite/${questionId}`),
  // 获取收藏列表
  getFavoriteQuestions: (pageNum = 1, pageSize = 10) => get('/practice/favorite', { pageNum, pageSize }),
  // 检查是否收藏
  checkFavorite: (questionId) => get(`/practice/favorite/check/${questionId}`)
}

// ==================== 考试接口 ====================
const examApi = {
  // 获取试卷列表
  getExamList: (majorId, type, pageNum = 1, pageSize = 10) => get('/exam/list', { majorId, type, pageNum, pageSize }),
  // 开始考试
  startExam: (examId) => post(`/exam/start/${examId}`),
  // 提交考试
  submitExam: (data) => post('/exam/submit', data),
  // 获取考试记录
  getExamRecords: (pageNum = 1, pageSize = 10) => get('/exam/records', { pageNum, pageSize }),
  // 获取考试结果详情
  getExamResult: (recordId) => get(`/exam/result/${recordId}`)
}

// ==================== 打卡接口 ====================
const checkinApi = {
  // 签到打卡
  checkin: (data) => post('/checkin/', data),
  // 获取打卡统计
  getStats: () => get('/checkin/stats'),
  // 获取打卡日历
  getCalendar: (year, month) => get('/checkin/calendar', { year, month }),
  // 获取打卡记录
  getRecords: (pageNum = 1, pageSize = 10) => get('/checkin/records', { pageNum, pageSize }),
  // 获取今日打卡
  getTodayCheckin: () => get('/checkin/today'),
  // 连续打卡排行榜
  getStreakRanking: (limit = 50) => get('/checkin/ranking/streak', { limit }),
  // 总天数排行榜
  getTotalRanking: (limit = 50) => get('/checkin/ranking/total', { limit }),
  // 获取我的排名
  getMyRanking: (type = 'streak') => get('/checkin/ranking/my', { type })
}

// ==================== 社区接口 ====================
const communityApi = {
  // 发布帖子
  createPost: (data) => post('/post', data),
  // 获取帖子列表
  getPostList: (params) => get('/post/list', params),
  // 获取帖子详情
  getPostDetail: (postId) => get(`/post/${postId}`),
  // 删除帖子
  deletePost: (postId) => del(`/post/${postId}`),
  // 点赞帖子
  likePost: (postId) => post(`/post/${postId}/like`),
  // 取消点赞帖子
  unlikePost: (postId) => del(`/post/${postId}/like`),
  // 获取我的帖子
  getMyPosts: (pageNum = 1, pageSize = 10) => get('/post/my', { pageNum, pageSize }),
  
  // 发表评论
  createComment: (data) => post('/comment', data),
  // 获取评论列表
  getCommentList: (postId, pageNum = 1, pageSize = 20) => get('/comment/list', { postId, pageNum, pageSize }),
  // 删除评论
  deleteComment: (commentId) => del(`/comment/${commentId}`),
  // 点赞评论
  likeComment: (commentId) => post(`/comment/${commentId}/like`),
  // 取消点赞评论
  unlikeComment: (commentId) => del(`/comment/${commentId}/like`)
}

// ==================== 上传接口 ====================
const uploadApi = {
  // 上传图片
  uploadImage: (filePath) => upload('/upload/image', filePath),
  // 上传头像
  uploadAvatar: (filePath) => upload('/upload/avatar', filePath)
}

module.exports = {
  authApi,
  userApi,
  courseApi,
  questionApi,
  practiceApi,
  examApi,
  checkinApi,
  communityApi,
  uploadApi
}
