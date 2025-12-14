// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    majorId: null,
    examYear: null,
    baseUrl: 'http://localhost:8080/api'  // 开发环境，生产环境需替换
  },

  onLaunch() {
    // 检查登录状态
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo
      this.globalData.majorId = userInfo?.majorId
      this.globalData.examYear = userInfo?.examYear
    }
  },

  // 检查是否登录
  checkLogin() {
    return !!this.globalData.token
  },

  // 设置登录信息
  setLoginInfo(token, userInfo) {
    this.globalData.token = token
    this.globalData.userInfo = userInfo
    this.globalData.majorId = userInfo?.majorId
    this.globalData.examYear = userInfo?.examYear
    
    wx.setStorageSync('token', token)
    wx.setStorageSync('userInfo', userInfo)
  },

  // 清除登录信息
  clearLoginInfo() {
    this.globalData.token = null
    this.globalData.userInfo = null
    this.globalData.majorId = null
    this.globalData.examYear = null
    
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  },

  // 更新用户信息
  updateUserInfo(userInfo) {
    this.globalData.userInfo = userInfo
    this.globalData.majorId = userInfo?.majorId
    this.globalData.examYear = userInfo?.examYear
    wx.setStorageSync('userInfo', userInfo)
  }
})
