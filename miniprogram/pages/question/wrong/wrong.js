// pages/question/wrong/wrong.js
const { practiceApi } = require('../../../utils/api')
const { questionTypeMap, formatRelativeTime } = require('../../../utils/util')

Page({
  data: {
    wrongList: [],
    loading: true,
    hasMore: false,
    isRefreshing: false,
    page: 1,
    size: 20
  },

  onLoad() {
    this.loadWrongList()
  },

  // 加载错题列表
  async loadWrongList(loadMore = false) {
    if (this.data.loading && loadMore) return
    
    try {
      const page = loadMore ? this.data.page + 1 : 1
      const res = await practiceApi.getWrongQuestions(page, this.data.size)
      
      let wrongList = (res.data?.list || []).map(item => ({
        ...item,
        question: {
          ...item.question,
          typeText: questionTypeMap[item.question?.type] || '其他'
        },
        lastWrongTimeStr: formatRelativeTime(item.lastWrongTime)
      }))

      if (loadMore) {
        wrongList = [...this.data.wrongList, ...wrongList]
      }

      this.setData({
        wrongList,
        loading: false,
        hasMore: wrongList.length < (res.data?.total || 0),
        page
      })
    } catch (error) {
      console.error('加载错题失败:', error)
      this.setData({ loading: false })
    }
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({ isRefreshing: true })
    await this.loadWrongList()
    this.setData({ isRefreshing: false })
  },

  // 跳转详情
  goToDetail(e) {
    const question = e.currentTarget.dataset.question
    wx.navigateTo({
      url: `/pages/question/practice/practice?mode=wrong`
    })
  },

  // 移除错题
  async removeWrong(e) {
    const questionId = e.currentTarget.dataset.id
    
    const res = await wx.showModal({
      title: '提示',
      content: '确定要移除这道错题吗？'
    })
    
    if (!res.confirm) return

    try {
      await practiceApi.removeWrongQuestion(questionId)
      
      const wrongList = this.data.wrongList.filter(item => item.questionId !== questionId)
      this.setData({ wrongList })
      
      wx.showToast({ title: '已移除', icon: 'success' })
    } catch (error) {
      console.error('移除失败:', error)
    }
  },

  // 开始练习
  startPractice() {
    wx.navigateTo({
      url: '/pages/question/practice/practice?mode=wrong'
    })
  }
})
