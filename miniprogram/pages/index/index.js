// pages/index/index.js
const app = getApp()
const { userApi, courseApi, checkinApi, communityApi } = require('../../utils/api')
const { getExamCountdown, formatRelativeTime, postTypeMap } = require('../../utils/util')

Page({
  data: {
    userInfo: {},
    examYear: null,
    countdown: null,
    checkinStats: {},
    todayCheckin: false,
    recommendCourses: [],
    hotPosts: []
  },

  onLoad() {
    // 检查登录状态
    if (!app.checkLogin()) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
  },

  onShow() {
    if (app.checkLogin()) {
      console.log('首页加载，当前Token:', app.globalData.token)
      this.loadData()
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadData().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 加载数据
  async loadData() {
    try {
      // 获取用户信息
      const userRes = await userApi.getUserInfo()
      const userInfo = userRes.data
      app.updateUserInfo(userInfo)

      // 计算考研倒计时
      const countdown = getExamCountdown(userInfo.examYear)
      
      this.setData({ 
        userInfo,
        examYear: userInfo.examYear,
        countdown
      })

      // 并行加载其他数据
      await Promise.all([
        this.loadCheckinStats(),
        this.loadRecommendCourses(),
        this.loadHotPosts()
      ])
    } catch (error) {
      console.error('加载数据失败:', error)
    }
  },

  // 加载打卡统计
  async loadCheckinStats() {
    try {
      const [statsRes, todayRes] = await Promise.all([
        checkinApi.getStats(),
        checkinApi.getTodayCheckin()
      ])
      
      this.setData({
        checkinStats: statsRes.data || {},
        todayCheckin: !!todayRes.data
      })
    } catch (error) {
      console.error('加载打卡统计失败:', error)
    }
  },

  // 加载推荐课程
  async loadRecommendCourses() {
    try {
      const majorId = app.globalData.majorId
      if (!majorId) return
      
      const res = await courseApi.getCourseList(majorId)
      const courses = (res.data || []).slice(0, 5)  // 取前5个
      
      this.setData({ recommendCourses: courses })
    } catch (error) {
      console.error('加载推荐课程失败:', error)
    }
  },

  // 加载热门帖子
  async loadHotPosts() {
    try {
      const res = await communityApi.getPostList({
        sort: 'hot',
        page: 1,
        size: 3
      })
      
      const posts = (res.data?.list || []).map(post => ({
        ...post,
        createTimeStr: formatRelativeTime(post.createTime),
        typeText: postTypeMap[post.type] || '其他'
      }))
      
      this.setData({ hotPosts: posts })
    } catch (error) {
      console.error('加载热门帖子失败:', error)
    }
  },

  // 跳转页面
  goToPage(e) {
    const url = e.currentTarget.dataset.url
    if (url.includes('/pages/course/') || url.includes('/pages/question/') || url.includes('/pages/community/')) {
      wx.switchTab({ url })
    } else {
      wx.navigateTo({ url })
    }
  },

  // 跳转个人资料
  goToProfile() {
    wx.navigateTo({ url: '/pages/mine/profile/profile' })
  },

  // 跳转打卡
  goToCheckin() {
    wx.navigateTo({ url: '/pages/checkin/checkin' })
  },

  // 跳转课程详情
  goToCourseDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/course/detail/detail?id=${id}` })
  },

  // 跳转帖子详情
  goToPostDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/community/detail/detail?id=${id}` })
  }
})
