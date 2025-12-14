// pages/course/list/list.js
const app = getApp()
const { courseApi } = require('../../../utils/api')

Page({
  data: {
    courses: [],
    allCourses: [],
    currentFilter: 'all',
    keyword: '',
    loading: false,
    hasMore: false,
    isRefreshing: false
  },

  onLoad() {
    this.loadCourses()
  },

  onShow() {
    // 更新TabBar选中状态
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 })
    }
  },

  // 加载课程列表
  async loadCourses() {
    if (this.data.loading) return
    
    this.setData({ loading: true })

    try {
      const majorId = app.globalData.majorId
      const res = await courseApi.getCourseList(majorId)
      const courses = res.data || []
      
      this.setData({
        allCourses: courses,
        courses: this.filterCourses(courses, this.data.currentFilter, this.data.keyword),
        loading: false,
        hasMore: false
      })
    } catch (error) {
      console.error('加载课程失败:', error)
      this.setData({ loading: false })
    }
  },

  // 筛选课程
  filterCourses(courses, filter, keyword) {
    let result = courses

    // 按筛选条件过滤
    if (filter === 'free') {
      result = result.filter(c => c.isFree === 1)
    } else if (filter === 'hot') {
      result = [...result].sort((a, b) => b.viewCount - a.viewCount)
    }

    // 按关键字搜索
    if (keyword) {
      result = result.filter(c => 
        c.title.includes(keyword) || 
        c.teacherName.includes(keyword)
      )
    }

    return result
  },

  // 搜索
  onSearch(e) {
    const keyword = e.detail.value.trim()
    this.setData({
      keyword,
      courses: this.filterCourses(this.data.allCourses, this.data.currentFilter, keyword)
    })
  },

  // 切换筛选
  onFilterChange(e) {
    const filter = e.currentTarget.dataset.filter
    this.setData({
      currentFilter: filter,
      courses: this.filterCourses(this.data.allCourses, filter, this.data.keyword)
    })
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({ isRefreshing: true })
    await this.loadCourses()
    this.setData({ isRefreshing: false })
  },

  // 加载更多
  loadMore() {
    // 本地数据无需分页
  },

  // 跳转详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/course/detail/detail?id=${id}`
    })
  }
})
