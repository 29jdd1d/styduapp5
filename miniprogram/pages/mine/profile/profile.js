// pages/mine/profile/profile.js
const { userApi, uploadApi } = require('../../../utils/api')

Page({
  data: {
    avatar: '',
    nickname: '',
    gender: 0,
    genderText: '未知',
    phone: '',
    email: '',
    targetSchool: '',
    majorName: '',
    examYear: '',
    introduction: ''
  },

  onLoad() {
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo') || {}
    const genderMap = { 0: '未知', 1: '男', 2: '女' }
    
    this.setData({
      avatar: userInfo.avatar || '',
      nickname: userInfo.nickname || '',
      gender: userInfo.gender || 0,
      genderText: genderMap[userInfo.gender] || '未知',
      phone: userInfo.phone || '',
      email: userInfo.email || '',
      targetSchool: userInfo.targetSchool || '',
      majorName: userInfo.majorName || '',
      examYear: userInfo.examYear || '',
      introduction: userInfo.introduction || ''
    })
  },

  // 选择头像
  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath
        
        wx.showLoading({ title: '上传中...' })
        
        try {
          const uploadRes = await new Promise((resolve, reject) => {
            wx.uploadFile({
              url: uploadApi.getUploadUrl(),
              filePath: tempFilePath,
              name: 'file',
              header: {
                'Authorization': 'Bearer ' + wx.getStorageSync('token')
              },
              success: (res) => {
                const data = JSON.parse(res.data)
                if (data.code === 200) {
                  resolve(data.data)
                } else {
                  reject(new Error(data.message || '上传失败'))
                }
              },
              fail: reject
            })
          })
          
          this.setData({ avatar: uploadRes })
        } catch (error) {
          console.error('上传头像失败:', error)
          wx.showToast({
            title: '上传失败',
            icon: 'none'
          })
        } finally {
          wx.hideLoading()
        }
      }
    })
  },

  // 输入昵称
  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  // 选择性别
  chooseGender() {
    wx.showActionSheet({
      itemList: ['男', '女', '保密'],
      success: (res) => {
        const genderMap = { 0: 1, 1: 2, 2: 0 }
        const textMap = { 0: '男', 1: '女', 2: '保密' }
        
        this.setData({
          gender: genderMap[res.tapIndex],
          genderText: textMap[res.tapIndex]
        })
      }
    })
  },

  // 输入手机号
  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  // 输入邮箱
  onEmailInput(e) {
    this.setData({ email: e.detail.value })
  },

  // 输入目标院校
  onTargetSchoolInput(e) {
    this.setData({ targetSchool: e.detail.value })
  },

  // 选择年份
  chooseYear() {
    const currentYear = new Date().getFullYear()
    const years = []
    for (let i = currentYear; i <= currentYear + 3; i++) {
      years.push(i + '年')
    }
    
    wx.showActionSheet({
      itemList: years,
      success: (res) => {
        this.setData({
          examYear: years[res.tapIndex]
        })
      }
    })
  },

  // 输入简介
  onIntroductionInput(e) {
    this.setData({ introduction: e.detail.value })
  },

  // 保存资料
  async saveProfile() {
    const { nickname, phone, email } = this.data

    // 验证
    if (!nickname.trim()) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      })
      return
    }

    if (phone && !/^1\d{10}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return
    }

    if (email && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(email)) {
      wx.showToast({
        title: '邮箱格式不正确',
        icon: 'none'
      })
      return
    }

    wx.showLoading({ title: '保存中...' })

    try {
      const res = await userApi.updateUserInfo({
        avatar: this.data.avatar,
        nickname: nickname.trim(),
        gender: this.data.gender,
        phone: phone,
        email: email,
        targetSchool: this.data.targetSchool,
        examYear: this.data.examYear,
        introduction: this.data.introduction
      })

      if (res.code === 200) {
        // 更新本地存储
        const userInfo = wx.getStorageSync('userInfo') || {}
        const newUserInfo = {
          ...userInfo,
          avatar: this.data.avatar,
          nickname: nickname.trim(),
          gender: this.data.gender,
          phone: phone,
          email: email,
          targetSchool: this.data.targetSchool,
          examYear: this.data.examYear,
          introduction: this.data.introduction
        }
        wx.setStorageSync('userInfo', newUserInfo)
        getApp().globalData.userInfo = newUserInfo

        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })

        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    } catch (error) {
      console.error('保存失败:', error)
      wx.showToast({
        title: '保存失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  }
})
