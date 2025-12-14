// pages/mine/mine.js
const { userApi, practiceApi } = require('../../utils/api')

Page({
  data: {
    userInfo: null,
    stats: {
      studyDays: 0,
      practiceCount: 0,
      checkinDays: 0,
      videoHours: 0
    },
    wrongCount: 0,
    favoriteCount: 0,
    cacheSize: '0KB'
  },

  onLoad() {
    this.loadUserInfo()
    this.calculateCacheSize()
  },

  onShow() {
    this.loadUserInfo()
    this.loadStats()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo')
    this.setData({ userInfo })
  },

  // 加载统计数据
  async loadStats() {
    if (!this.data.userInfo) return

    try {
      // 加载学习统计
      const statsRes = await userApi.getUserStats()
      if (statsRes.code === 200) {
        this.setData({
          stats: statsRes.data || this.data.stats
        })
      }

      // 加载错题数量
      const wrongRes = await practiceApi.getWrongQuestions(1, 1)
      if (wrongRes.code === 200) {
        this.setData({
          wrongCount: wrongRes.data.total || 0
        })
      }

      // 加载收藏数量
      const favoriteRes = await practiceApi.getFavoriteQuestions(1, 1)
      if (favoriteRes.code === 200) {
        this.setData({
          favoriteCount: favoriteRes.data.total || 0
        })
      }
    } catch (error) {
      console.error('加载统计失败:', error)
    }
  },

  // 计算缓存大小
  calculateCacheSize() {
    try {
      const info = wx.getStorageInfoSync()
      const size = info.currentSize
      let sizeStr = '0KB'
      
      if (size < 1024) {
        sizeStr = size + 'KB'
      } else {
        sizeStr = (size / 1024).toFixed(2) + 'MB'
      }
      
      this.setData({ cacheSize: sizeStr })
    } catch (error) {
      console.error('获取缓存大小失败:', error)
    }
  },

  // 跳转登录
  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  // 跳转编辑资料
  goToProfile() {
    wx.navigateTo({
      url: '/pages/mine/profile/profile'
    })
  },

  // 跳转页面
  goToPage(e) {
    const url = e.currentTarget.dataset.url
    wx.navigateTo({ url })
  },

  // 跳转记录页
  goToRecords(e) {
    const type = e.currentTarget.dataset.type
    wx.navigateTo({
      url: `/pages/mine/records/records?type=${type}`
    })
  },

  // 切换专业
  changeMajor() {
    wx.navigateTo({
      url: '/pages/major/major?from=mine'
    })
  },

  // 清除缓存
  clearCache() {
    wx.showModal({
      title: '提示',
      content: '确定要清除缓存吗？',
      success: (res) => {
        if (res.confirm) {
          try {
            // 保留登录信息
            const token = wx.getStorageSync('token')
            const userInfo = wx.getStorageSync('userInfo')
            const majorId = wx.getStorageSync('majorId')
            
            wx.clearStorageSync()
            
            // 恢复登录信息
            if (token) wx.setStorageSync('token', token)
            if (userInfo) wx.setStorageSync('userInfo', userInfo)
            if (majorId) wx.setStorageSync('majorId', majorId)
            
            this.calculateCacheSize()
            
            wx.showToast({
              title: '清除成功',
              icon: 'success'
            })
          } catch (error) {
            console.error('清除缓存失败:', error)
          }
        }
      }
    })
  },

  // 关于我们
  showAbout() {
    wx.showModal({
      title: '关于我们',
      content: '考研学习助手 v1.0.0\n\n专注于帮助考研学子高效备考的学习平台。\n\n功能包括：视频课程、题库练习、模拟考试、学习社区、每日打卡等。',
      showCancel: false
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorageSync()
          
          // 更新全局状态
          const app = getApp()
          app.globalData.userInfo = null
          app.globalData.token = null
          app.globalData.majorId = null

          this.setData({
            userInfo: null,
            stats: {
              studyDays: 0,
              practiceCount: 0,
              checkinDays: 0,
              videoHours: 0
            },
            wrongCount: 0,
            favoriteCount: 0
          })

          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '考研学习助手 - 一起备考',
      path: '/pages/index/index'
    }
  }
})
