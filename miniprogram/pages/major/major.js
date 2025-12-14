// pages/major/major.js
const app = getApp()
const { userApi } = require('../../utils/api')

Page({
  data: {
    majors: [],
    groupedMajors: [],
    keyword: '',
    selectedId: null,
    loading: true
  },

  onLoad() {
    this.loadMajors()
  },

  // 加载专业列表
  async loadMajors() {
    try {
      const res = await userApi.getMajorList()
      const majors = res.data || []
      
      this.setData({
        majors,
        groupedMajors: this.groupByCategory(majors),
        loading: false
      })
    } catch (error) {
      console.error('加载专业列表失败:', error)
      this.setData({ loading: false })
    }
  },

  // 按学科门类分组
  groupByCategory(majors) {
    const groups = {}
    majors.forEach(major => {
      const category = major.category || '其他'
      if (!groups[category]) {
        groups[category] = []
      }
      groups[category].push(major)
    })
    
    return Object.keys(groups).map(category => ({
      category,
      majors: groups[category]
    }))
  },

  // 搜索输入
  onSearchInput(e) {
    const keyword = e.detail.value.trim()
    this.setData({ keyword })
    this.filterMajors(keyword)
  },

  // 清除搜索
  clearSearch() {
    this.setData({ 
      keyword: '',
      groupedMajors: this.groupByCategory(this.data.majors)
    })
  },

  // 过滤专业
  filterMajors(keyword) {
    if (!keyword) {
      this.setData({
        groupedMajors: this.groupByCategory(this.data.majors)
      })
      return
    }

    const filtered = this.data.majors.filter(major => 
      major.name.includes(keyword) || 
      major.code.includes(keyword)
    )
    
    this.setData({
      groupedMajors: this.groupByCategory(filtered)
    })
  },

  // 选择专业
  selectMajor(e) {
    const id = e.currentTarget.dataset.id
    this.setData({
      selectedId: this.data.selectedId === id ? null : id
    })
  },

  // 确认选择
  async confirmSelect() {
    if (!this.data.selectedId) return

    try {
      wx.showLoading({ title: '保存中...' })
      
      await userApi.selectMajor(this.data.selectedId)
      
      // 更新本地用户信息
      const selectedMajor = this.data.majors.find(m => m.id === this.data.selectedId)
      const userInfo = app.globalData.userInfo
      userInfo.majorId = this.data.selectedId
      userInfo.majorName = selectedMajor?.name
      app.updateUserInfo(userInfo)

      wx.hideLoading()
      wx.showToast({
        title: '设置成功',
        icon: 'success'
      })

      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1500)
    } catch (error) {
      wx.hideLoading()
      console.error('选择专业失败:', error)
    }
  }
})
