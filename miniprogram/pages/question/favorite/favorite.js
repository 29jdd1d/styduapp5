// pages/question/favorite/favorite.js
const { practiceApi } = require('../../../utils/api')
const { questionTypeMap } = require('../../../utils/util')

Page({
  data: {
    favoriteList: [],
    loading: true,
    hasMore: false,
    isRefreshing: false,
    page: 1,
    size: 20
  },

  onLoad() {
    this.loadFavoriteList()
  },

  onShow() {
    // 重新加载，防止在做题页面取消收藏后数据不同步
    if (!this.data.loading) {
      this.loadFavoriteList()
    }
  },

  // 加载收藏列表
  async loadFavoriteList(loadMore = false) {
    if (this.data.loading && loadMore) return
    
    try {
      const page = loadMore ? this.data.page + 1 : 1
      const res = await practiceApi.getFavoriteQuestions(page, this.data.size)
      
      let favoriteList = (res.data?.list || []).map(item => ({
        ...item,
        question: {
          ...item.question,
          typeText: questionTypeMap[item.question?.type] || '其他'
        }
      }))

      if (loadMore) {
        favoriteList = [...this.data.favoriteList, ...favoriteList]
      }

      this.setData({
        favoriteList,
        loading: false,
        hasMore: favoriteList.length < (res.data?.total || 0),
        page
      })
    } catch (error) {
      console.error('加载收藏失败:', error)
      this.setData({ loading: false })
    }
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({ isRefreshing: true })
    await this.loadFavoriteList()
    this.setData({ isRefreshing: false })
  },

  // 跳转详情
  goToDetail(e) {
    wx.navigateTo({
      url: `/pages/question/practice/practice?mode=favorite`
    })
  },

  // 取消收藏
  async removeFavorite(e) {
    const questionId = e.currentTarget.dataset.id

    try {
      await practiceApi.unfavoriteQuestion(questionId)
      
      const favoriteList = this.data.favoriteList.filter(item => item.questionId !== questionId)
      this.setData({ favoriteList })
      
      wx.showToast({ title: '已取消收藏', icon: 'none' })
    } catch (error) {
      console.error('取消收藏失败:', error)
    }
  },

  // 开始练习
  startPractice() {
    wx.navigateTo({
      url: '/pages/question/practice/practice?mode=favorite'
    })
  }
})
