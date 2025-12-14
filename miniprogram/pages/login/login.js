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

      console.log('登录接口返回:', res)

      // 检查返回数据
      if (!res || !res.data) {
        console.error('登录接口返回数据为空')
        throw new Error('登录接口返回数据异常')
      }

      // 后端返回的 LoginResponse 直接包含字段，不是嵌套在 userInfo 中
      const loginData = res.data
      const token = loginData.token
      
      // 构造用户信息对象
      const userInfo = {
        userId: loginData.userId,
        nickname: loginData.nickname,
        avatar: loginData.avatar,
        majorId: loginData.majorId,
        majorName: loginData.majorName,
        isNewUser: loginData.isNewUser,
        hasMajor: loginData.hasMajor
      }
      
      console.log('获取到的token:', token)
      console.log('构造的userInfo:', userInfo)

      if (!token) {
        throw new Error('未获取到登录token')
      }

      app.setLoginInfo(token, userInfo)

      // 判断是否选择了专业
      if (!userInfo.majorId || !loginData.hasMajor) {
        console.log('用户未选择专业，跳转到专业选择页')
        wx.redirectTo({ url: '/pages/major/major' })
      } else {
        console.log('用户已选择专业，跳转首页')
        this.navigateToIndex()
      }
    } catch (error) {
      console.error('登录失败:', error)
      wx.showToast({
        title: error.message || '登录失败，请重试',
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
