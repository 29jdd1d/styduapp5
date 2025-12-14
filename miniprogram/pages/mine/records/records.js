// pages/mine/records/records.js
const { userApi, practiceApi, examApi, courseApi } = require('../../../utils/api')
const { formatRelativeTime, formatDuration } = require('../../../utils/util')

Page({
  data: {
    currentType: 'course',
    records: [],
    page: 1,
    pageSize: 10,
    hasMore: true,
    loading: false,
    isRefreshing: false
  },

  onLoad(options) {
    if (options.type) {
      this.setData({ currentType: options.type })
    }
    this.loadRecords()
  },

  // 切换类型
  switchType(e) {
    const type = e.currentTarget.dataset.type
    if (type === this.data.currentType) return

    this.setData({
      currentType: type,
      page: 1,
      hasMore: true,
      records: []
    })

    this.loadRecords()
  },

  // 加载记录
  async loadRecords() {
    if (this.data.loading || !this.data.hasMore) return

    this.setData({ loading: true })

    try {
      let res
      const pageNum = this.data.page
      const pageSize = this.data.pageSize

      switch (this.data.currentType) {
        case 'course':
          res = await courseApi.getStudyRecords({ pageNum, pageSize })
          break
        case 'practice':
          res = await practiceApi.getPracticeRecords(pageNum, pageSize)
          break
        case 'exam':
          res = await examApi.getExamRecords(pageNum, pageSize)
          break
      }

      if (res && res.code === 200) {
        const newRecords = this.formatRecords(res.data.records || res.data.list || [])

        this.setData({
          records: this.data.page === 1 ? newRecords : [...this.data.records, ...newRecords],
          hasMore: newRecords.length >= this.data.pageSize,
          page: this.data.page + 1
        })
      }
    } catch (error) {
      console.error('加载记录失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 格式化记录
  formatRecords(records) {
    const { currentType } = this.data

    return records.map(record => {
      if (currentType === 'course') {
        return {
          ...record,
          updateTimeStr: formatRelativeTime(record.updateTime)
        }
      } else if (currentType === 'practice') {
        return {
          ...record,
          createTimeStr: formatRelativeTime(record.createTime),
          durationStr: formatDuration(record.duration || 0),
          accuracy: record.totalCount > 0 
            ? Math.round((record.correctCount / record.totalCount) * 100) 
            : 0
        }
      } else if (currentType === 'exam') {
        return {
          ...record,
          submitTimeStr: formatRelativeTime(record.submitTime),
          durationStr: formatDuration(record.duration || 0),
          passed: record.score >= record.passScore
        }
      }
      return record
    })
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({
      isRefreshing: true,
      page: 1,
      hasMore: true,
      records: []
    })

    await this.loadRecords()

    this.setData({ isRefreshing: false })
  },

  // 加载更多
  loadMore() {
    this.loadRecords()
  },

  // 跳转课程
  goToCourse(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/course/detail/detail?id=${id}`
    })
  },

  // 跳转考试结果
  goToExamResult(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/exam/result/result?id=${id}`
    })
  }
})
