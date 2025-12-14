// pages/question/category/category.js
const app = getApp()
const { questionApi, practiceApi } = require('../../../utils/api')

Page({
  data: {
    categories: [],
    totalQuestions: 0,
    doneQuestions: 0,
    correctRate: 0,
    loading: true
  },

  onLoad() {
    this.loadCategories()
  },

  onShow() {
    // 更新TabBar选中状态
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 })
    }
  },

  // 加载分类列表
  async loadCategories() {
    try {
      const majorId = app.globalData.majorId
      const res = await questionApi.getCategoryTree(majorId)
      const categories = res.data || []
      
      // 计算总题数
      let totalQuestions = 0
      categories.forEach(cat => {
        totalQuestions += cat.questionCount || 0
        if (cat.children) {
          cat.children.forEach(sub => {
            totalQuestions += sub.questionCount || 0
          })
        }
        cat.expanded = false
      })

      this.setData({
        categories,
        totalQuestions,
        loading: false
      })

      // 加载做题统计
      this.loadStats()
    } catch (error) {
      console.error('加载分类失败:', error)
      this.setData({ loading: false })
    }
  },

  // 加载做题统计
  async loadStats() {
    try {
      const res = await practiceApi.getPracticeRecords(1, 100)
      const records = res.data?.list || []
      
      let doneQuestions = 0
      let correctCount = 0
      
      records.forEach(record => {
        doneQuestions += record.totalCount || 0
        correctCount += record.correctCount || 0
      })

      const correctRate = doneQuestions > 0 ? Math.round((correctCount / doneQuestions) * 100) : 0

      this.setData({
        doneQuestions,
        correctRate
      })
    } catch (error) {
      console.error('加载统计失败:', error)
    }
  },

  // 跳转到分类
  goToCategory(e) {
    const category = e.currentTarget.dataset.category
    
    // 如果有子分类，展开/收起
    if (category.children && category.children.length > 0) {
      const categories = this.data.categories.map(c => {
        if (c.id === category.id) {
          c.expanded = !c.expanded
        }
        return c
      })
      this.setData({ categories })
      return
    }

    // 跳转到题目列表
    wx.navigateTo({
      url: `/pages/question/list/list?categoryId=${category.id}&categoryName=${category.name}`
    })
  },

  // 随机刷题
  startRandomPractice() {
    wx.navigateTo({
      url: '/pages/question/practice/practice?mode=random'
    })
  },

  // 错题本
  goToWrong() {
    wx.navigateTo({
      url: '/pages/question/wrong/wrong'
    })
  },

  // 收藏夹
  goToFavorite() {
    wx.navigateTo({
      url: '/pages/question/favorite/favorite'
    })
  },

  // 模拟考试
  goToExam() {
    wx.navigateTo({
      url: '/pages/exam/list/list'
    })
  }
})
