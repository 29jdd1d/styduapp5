// pages/login/login.js
const app = getApp()
const { authApi } = require('../../utils/api')

Page({
  data: {
    avatarUrl: '',
    nickname: '',
    hasUserInfo: false,
    agreed: false,
    loading: false
  },

  onLoad() {
    // 检查是否已登录
    if (app.checkLogin()) {
      this.navigateToIndex()
    }
  },

  // 选择头像
  onChooseAvatar(e) {
    const avatarUrl = e.detail.avatarUrl
    this.setData({ avatarUrl })
  },

  // 输入昵称
  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  // 昵称输入完成
  onNicknameBlur(e) {
    const nickname = e.detail.value
    if (nickname) {
      this.setData({ 
        nickname,
        hasUserInfo: !!this.data.avatarUrl
      })
    }
  },

  // 同意协议
  onAgreeChange(e) {
    this.setData({ agreed: e.detail.value.includes('agree') })
  },

  // 处理登录
  async handleLogin() {
    if (!this.data.agreed) {
      wx.showToast({
        title: '请先同意用户协议',
        icon: 'none'
      })
      return
    }

    if (!this.data.nickname) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      })
      return
    }

    this.setData({ loading: true })

    try {
      // 获取微信登录code
      const loginRes = await wx.login()
      
      // 调用后端登录接口
      const res = await authApi.login({
        code: loginRes.code,
        nickname: this.data.nickname,
        avatar: this.data.avatarUrl || '/images/default-avatar.png'
      })

      // 保存登录信息
      app.setLoginInfo(res.data.token, res.data.userInfo)

      // 判断是否选择了专业
      if (!res.data.userInfo.majorId) {
        wx.redirectTo({ url: '/pages/major/major' })
      } else {
        this.navigateToIndex()
      }
    } catch (error) {
      console.error('登录失败:', error)
      wx.showToast({
        title: '登录失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 跳转首页
  navigateToIndex() {
    wx.switchTab({ url: '/pages/index/index' })
  },

  // 查看用户协议
  viewUserAgreement() {
    wx.navigateTo({ url: '/pages/webview/webview?url=user-agreement' })
  },

  // 查看隐私政策
  viewPrivacyPolicy() {
    wx.navigateTo({ url: '/pages/webview/webview?url=privacy-policy' })
  }
})
