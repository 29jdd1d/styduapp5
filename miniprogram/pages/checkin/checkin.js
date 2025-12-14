// pages/checkin/checkin.js
const { checkinApi } = require('../../utils/api')

Page({
  data: {
    stats: {},
    todayCheckin: null,
    calendarDays: [],
    currentYear: 0,
    currentMonth: 0,
    weekdays: ['日', '一', '二', '三', '四', '五', '六'],
    moods: ['😞', '😐', '🙂', '😊', '🤩'],
    checkedDates: [],
    
    // 表单数据
    duration: '',
    content: '',
    mood: 3,
    loading: false
  },

  onLoad() {
    const now = new Date()
    this.setData({
      currentYear: now.getFullYear(),
      currentMonth: now.getMonth() + 1
    })
    
    this.loadData()
  },

  onShow() {
    this.loadData()
  },

  // 加载数据
  async loadData() {
    await Promise.all([
      this.loadStats(),
      this.loadTodayCheckin(),
      this.loadCalendar()
    ])
  },

  // 加载统计数据
  async loadStats() {
    try {
      const res = await checkinApi.getStats()
      this.setData({ stats: res.data || {} })
    } catch (error) {
      console.error('加载统计失败:', error)
    }
  },

  // 加载今日打卡
  async loadTodayCheckin() {
    try {
      const res = await checkinApi.getTodayCheckin()
      this.setData({ todayCheckin: res.data })
    } catch (error) {
      console.error('加载今日打卡失败:', error)
    }
  },

  // 加载日历数据
  async loadCalendar() {
    try {
      const { currentYear, currentMonth } = this.data
      const res = await checkinApi.getCalendar(currentYear, currentMonth)
      const checkedDates = res.data || []
      
      this.setData({ checkedDates })
      this.generateCalendarDays()
    } catch (error) {
      console.error('加载日历失败:', error)
      this.generateCalendarDays()
    }
  },

  // 生成日历天数
  generateCalendarDays() {
    const { currentYear, currentMonth, checkedDates } = this.data
    const days = []
    
    // 当月第一天是星期几
    const firstDay = new Date(currentYear, currentMonth - 1, 1).getDay()
    // 当月总天数
    const daysInMonth = new Date(currentYear, currentMonth, 0).getDate()
    // 上月总天数
    const daysInPrevMonth = new Date(currentYear, currentMonth - 1, 0).getDate()
    
    // 今天的日期
    const today = new Date()
    const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    
    // 上月末尾几天
    for (let i = firstDay - 1; i >= 0; i--) {
      const day = daysInPrevMonth - i
      days.push({
        day,
        date: '',
        isCurrentMonth: false,
        isToday: false,
        checked: false
      })
    }
    
    // 当月天数
    for (let i = 1; i <= daysInMonth; i++) {
      const dateStr = `${currentYear}-${String(currentMonth).padStart(2, '0')}-${String(i).padStart(2, '0')}`
      days.push({
        day: i,
        date: dateStr,
        isCurrentMonth: true,
        isToday: dateStr === todayStr,
        checked: checkedDates.includes(dateStr)
      })
    }
    
    // 下月开头几天（补齐6行）
    const remaining = 42 - days.length
    for (let i = 1; i <= remaining; i++) {
      days.push({
        day: i,
        date: '',
        isCurrentMonth: false,
        isToday: false,
        checked: false
      })
    }
    
    this.setData({ calendarDays: days })
  },

  // 上一月
  prevMonth() {
    let { currentYear, currentMonth } = this.data
    currentMonth--
    if (currentMonth < 1) {
      currentMonth = 12
      currentYear--
    }
    this.setData({ currentYear, currentMonth })
    this.loadCalendar()
  },

  // 下一月
  nextMonth() {
    let { currentYear, currentMonth } = this.data
    const now = new Date()
    
    // 不能查看未来月份
    if (currentYear > now.getFullYear() || 
        (currentYear === now.getFullYear() && currentMonth >= now.getMonth() + 1)) {
      return
    }
    
    currentMonth++
    if (currentMonth > 12) {
      currentMonth = 1
      currentYear++
    }
    this.setData({ currentYear, currentMonth })
    this.loadCalendar()
  },

  // 输入学习时长
  onDurationInput(e) {
    this.setData({ duration: e.detail.value })
  },

  // 输入学习内容
  onContentInput(e) {
    this.setData({ content: e.detail.value })
  },

  // 选择心情
  selectMood(e) {
    const mood = e.currentTarget.dataset.mood
    this.setData({ mood })
  },

  // 执行打卡
  async doCheckin() {
    const { duration, content, mood } = this.data
    
    if (!duration || parseInt(duration) <= 0) {
      wx.showToast({ title: '请输入学习时长', icon: 'none' })
      return
    }

    this.setData({ loading: true })

    try {
      await checkinApi.checkin({
        duration: parseInt(duration),
        content,
        mood
      })

      wx.showToast({ title: '打卡成功！', icon: 'success' })
      
      // 重新加载数据
      this.loadData()
      
      // 清空表单
      this.setData({
        duration: '',
        content: '',
        mood: 3
      })
    } catch (error) {
      console.error('打卡失败:', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  // 跳转排行榜
  goToRanking() {
    wx.navigateTo({ url: '/pages/checkin/ranking/ranking' })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: `我已连续打卡${this.data.stats.streakDays || 0}天，一起来学习吧！`,
      path: '/pages/index/index'
    }
  }
})
