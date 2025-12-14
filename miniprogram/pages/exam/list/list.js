// pages/exam/list/list.js
const { examApi } = require('../../../utils/api')
const { formatDate } = require('../../../utils/util')

Page({
  data: {
    stats: {
      totalExams: 0,
      completedExams: 0,
      avgScore: 0
    },
    currentTab: 'all',
    exams: [],
    page: 1,
    pageSize: 10,
    hasMore: true,
    loading: false,
    isRefreshing: false
  },

  onLoad(options) {
    this.loadStats()
    this.loadExams()
  },

  onShow() {
    // 从考试页返回时刷新
    if (getApp().globalData.needRefreshExam) {
      this.onRefresh()
      getApp().globalData.needRefreshExam = false
    }
  },

  // 加载统计数据
  async loadStats() {
    try {
      const res = await examApi.getExamStats()
      if (res.code === 200) {
        this.setData({
          stats: res.data || this.data.stats
        })
      }
    } catch (error) {
      console.error('加载统计失败:', error)
    }
  },

  // 加载考试列表
  async loadExams() {
    if (this.data.loading || !this.data.hasMore) return

    this.setData({ loading: true })

    try {
      // 根据标签筛选确定类型
      let type = null
      if (this.data.currentTab === 'pending') {
        type = 'pending'
      } else if (this.data.currentTab === 'completed') {
        type = 'completed'
      }

      const res = await examApi.getExamList(null, type, this.data.page, this.data.pageSize)

      if (res.code === 200) {
        const now = new Date().getTime()
        const newExams = (res.data.records || res.data.list || []).map(exam => {
          const startTime = new Date(exam.startTime).getTime()
          const endTime = new Date(exam.endTime).getTime()
          
          let statusCode = 'pending'
          let statusText = '待考试'
          
          if (exam.userScore !== undefined && exam.userScore !== null) {
            statusCode = 'completed'
            statusText = '已完成'
          } else if (now < startTime) {
            statusCode = 'notstart'
            statusText = '未开始'
          } else if (now > endTime) {
            statusCode = 'ended'
            statusText = '已结束'
          } else if (exam.startedAt) {
            statusCode = 'doing'
            statusText = '答题中'
          }

          return {
            ...exam,
            statusCode,
            statusText,
            startTimeStr: formatDate(exam.startTime, 'MM-DD HH:mm'),
            endTimeStr: formatDate(exam.endTime, 'MM-DD HH:mm'),
            myScore: exam.userScore,
            usedTimeStr: exam.usedTime ? Math.floor(exam.usedTime / 60) + '分钟' : '-'
          }
        })

        this.setData({
          exams: this.data.page === 1 ? newExams : [...this.data.exams, ...newExams],
          hasMore: newExams.length >= this.data.pageSize,
          page: this.data.page + 1
        })
      }
    } catch (error) {
      console.error('加载考试列表失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 切换标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    if (tab === this.data.currentTab) return

    this.setData({
      currentTab: tab,
      page: 1,
      hasMore: true,
      exams: []
    })

    this.loadExams()
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({
      isRefreshing: true,
      page: 1,
      hasMore: true,
      exams: []
    })

    await Promise.all([
      this.loadStats(),
      this.loadExams()
    ])

    this.setData({ isRefreshing: false })
  },

  // 加载更多
  loadMore() {
    this.loadExams()
  },

  // 跳转考试
  goToExam(e) {
    const exam = e.currentTarget.dataset.exam

    if (exam.statusCode === 'notstart') {
      wx.showToast({
        title: '考试尚未开始',
        icon: 'none'
      })
      return
    }

    if (exam.statusCode === 'ended' && !exam.myScore) {
      wx.showToast({
        title: '考试已结束',
        icon: 'none'
      })
      return
    }

    if (exam.statusCode === 'completed') {
      wx.navigateTo({
        url: `/pages/exam/result/result?id=${exam.id}`
      })
    } else {
      wx.navigateTo({
        url: `/pages/exam/doing/doing?id=${exam.id}`
      })
    }
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '考研模拟考试',
      path: '/pages/exam/list/list'
    }
  }
})
