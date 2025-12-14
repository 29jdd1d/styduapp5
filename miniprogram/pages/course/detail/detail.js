// pages/course/detail/detail.js
const app = getApp()
const { courseApi } = require('../../../utils/api')
const { formatDuration } = require('../../../utils/util')

Page({
  data: {
    courseId: null,
    course: {},
    chapters: [],
    progress: null,
    activeTab: 'chapters',
    currentVideoId: null
  },

  onLoad(options) {
    const courseId = options.id
    this.setData({ courseId })
    this.loadCourseDetail(courseId)
    this.loadProgress(courseId)
  },

  // 加载课程详情
  async loadCourseDetail(courseId) {
    try {
      const res = await courseApi.getCourseDetail(courseId)
      const course = res.data || {}
      
      // 处理章节数据
      const chapters = (course.chapters || []).map(chapter => ({
        ...chapter,
        expanded: false,
        videos: (chapter.videos || []).map(video => ({
          ...video,
          durationStr: formatDuration(video.duration)
        }))
      }))

      // 默认展开第一章
      if (chapters.length > 0) {
        chapters[0].expanded = true
      }

      this.setData({ course, chapters })
    } catch (error) {
      console.error('加载课程详情失败:', error)
    }
  },

  // 加载学习进度
  async loadProgress(courseId) {
    try {
      const res = await courseApi.getProgress(courseId)
      const progress = res.data
      
      if (progress) {
        this.setData({
          progress,
          currentVideoId: progress.videoId
        })
      }
    } catch (error) {
      console.error('加载学习进度失败:', error)
    }
  },

  // 切换Tab
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
  },

  // 展开/收起章节
  toggleChapter(e) {
    const index = e.currentTarget.dataset.index
    const chapters = this.data.chapters
    chapters[index].expanded = !chapters[index].expanded
    this.setData({ chapters })
  },

  // 播放视频
  playVideo(e) {
    const video = e.currentTarget.dataset.video
    
    // 检查是否可以播放
    if (!this.data.course.isFree && !video.isTry) {
      wx.showToast({
        title: '请先购买课程',
        icon: 'none'
      })
      return
    }

    wx.navigateTo({
      url: `/pages/course/video/video?videoId=${video.id}&courseId=${this.data.courseId}`
    })
  },

  // 开始学习
  startLearning() {
    const { chapters, progress, courseId } = this.data

    if (chapters.length === 0) {
      wx.showToast({
        title: '课程暂无内容',
        icon: 'none'
      })
      return
    }

    // 如果有进度，继续学习
    if (progress && progress.videoId) {
      wx.navigateTo({
        url: `/pages/course/video/video?videoId=${progress.videoId}&courseId=${courseId}`
      })
      return
    }

    // 从第一个视频开始
    const firstVideo = chapters[0]?.videos?.[0]
    if (firstVideo) {
      wx.navigateTo({
        url: `/pages/course/video/video?videoId=${firstVideo.id}&courseId=${courseId}`
      })
    }
  },

  // 分享
  onShareAppMessage() {
    return {
      title: this.data.course.title,
      path: `/pages/course/detail/detail?id=${this.data.courseId}`,
      imageUrl: this.data.course.cover
    }
  }
})
