// pages/question/list/list.js
const app = getApp()
const { questionApi } = require('../../../utils/api')
const { questionTypeMap, difficultyMap } = require('../../../utils/util')

Page({
  data: {
    categoryId: null,
    categoryName: '',
    questions: [],
    allQuestions: [],
    currentType: 'all',
    loading: true,
    hasMore: false,
    isRefreshing: false
  },

  onLoad(options) {
    const { categoryId, categoryName } = options
    this.setData({ 
      categoryId, 
      categoryName: decodeURIComponent(categoryName || '')
    })
    
    wx.setNavigationBarTitle({ title: this.data.categoryName || '题目列表' })
    this.loadQuestions()
  },

  // 加载题目列表
  async loadQuestions() {
    try {
      const res = await questionApi.getQuestionList(this.data.categoryId)
      const questions = (res.data || []).map(q => ({
        ...q,
        typeText: questionTypeMap[q.type] || '其他',
        difficultyText: difficultyMap[q.difficulty]?.text || '未知'
      }))

      this.setData({
        allQuestions: questions,
        questions: questions,
        loading: false,
        hasMore: false
      })
    } catch (error) {
      console.error('加载题目失败:', error)
      this.setData({ loading: false })
    }
  },

  // 切换题型筛选
  onTypeChange(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ currentType: type })
    this.filterQuestions(type)
  },

  // 筛选题目
  filterQuestions(type) {
    if (type === 'all') {
      this.setData({ questions: this.data.allQuestions })
    } else {
      const filtered = this.data.allQuestions.filter(q => q.type === parseInt(type))
      this.setData({ questions: filtered })
    }
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({ isRefreshing: true })
    await this.loadQuestions()
    this.setData({ isRefreshing: false })
  },

  // 跳转题目详情/答题
  goToDetail(e) {
    const { question, index } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/question/practice/practice?mode=category&categoryId=${this.data.categoryId}&startIndex=${index}`
    })
  },

  // 开始答题
  startPractice() {
    wx.navigateTo({
      url: `/pages/question/practice/practice?mode=category&categoryId=${this.data.categoryId}`
    })
  }
})
