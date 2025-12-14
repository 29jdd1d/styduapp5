// pages/course/video/video.js
const app = getApp()
const { courseApi } = require('../../../utils/api')
const { formatDuration, debounce } = require('../../../utils/util')

Page({
  data: {
    courseId: null,
    videoId: null,
    videoUrl: '',
    currentVideo: {},
    chapters: [],
    allVideos: [],
    currentIndex: 0,
    hasPrev: false,
    hasNext: false,
    progressPercent: 0,
    initialTime: 0
  },

  videoContext: null,
  saveProgressDebounced: null,

  onLoad(options) {
    const { videoId, courseId } = options
    this.setData({ videoId: parseInt(videoId), courseId })
    
    this.videoContext = wx.createVideoContext('videoPlayer')
    this.saveProgressDebounced = debounce(this.saveProgress.bind(this), 5000)
    
    this.loadCourseDetail(courseId, parseInt(videoId))
  },

  onUnload() {
    // 离开页面时保存进度
    this.saveProgress()
  },

  onHide() {
    // 切换到后台时保存进度
    this.saveProgress()
  },

  // 加载课程详情
  async loadCourseDetail(courseId, videoId) {
    try {
      const res = await courseApi.getCourseDetail(courseId)
      const course = res.data || {}
      
      // 处理章节数据，收集所有视频
      const allVideos = []
      const chapters = (course.chapters || []).map(chapter => {
        const videos = (chapter.videos || []).map(video => {
          const v = {
            ...video,
            chapterId: chapter.id,
            chapterTitle: chapter.title,
            durationStr: formatDuration(video.duration)
          }
          allVideos.push(v)
          return v
        })
        return {
          ...chapter,
          expanded: videos.some(v => v.id === videoId),
          videos
        }
      })

      // 找到当前视频的索引
      const currentIndex = allVideos.findIndex(v => v.id === videoId)
      const currentVideo = allVideos[currentIndex] || {}

      this.setData({
        chapters,
        allVideos,
        currentVideo,
        currentIndex,
        hasPrev: currentIndex > 0,
        hasNext: currentIndex < allVideos.length - 1
      })

      // 加载视频URL和进度
      this.loadVideoUrl(videoId)
      this.loadProgress(videoId)
    } catch (error) {
      console.error('加载课程详情失败:', error)
    }
  },

  // 加载视频播放地址
  async loadVideoUrl(videoId) {
    try {
      const res = await courseApi.getVideoUrl(videoId)
      this.setData({ videoUrl: res.data?.url || '' })
    } catch (error) {
      console.error('加载视频地址失败:', error)
      wx.showToast({
        title: '视频加载失败',
        icon: 'none'
      })
    }
  },

  // 加载学习进度
  async loadProgress(videoId) {
    try {
      const res = await courseApi.getProgress(this.data.courseId)
      const progress = res.data
      
      if (progress && progress.videoId === videoId) {
        this.setData({
          initialTime: progress.position || 0,
          progressPercent: Math.floor((progress.position / (this.data.currentVideo.duration || 1)) * 100)
        })
      }
    } catch (error) {
      console.error('加载进度失败:', error)
    }
  },

  // 视频播放进度更新
  onTimeUpdate(e) {
    const { currentTime, duration } = e.detail
    const percent = Math.floor((currentTime / duration) * 100)
    
    this.setData({ progressPercent: percent })
    
    // 防抖保存进度
    this.currentTime = currentTime
    this.duration = duration
    this.saveProgressDebounced()
  },

  // 保存学习进度
  async saveProgress() {
    if (!this.currentTime || !this.data.videoId) return

    try {
      await courseApi.saveProgress({
        videoId: this.data.videoId,
        position: Math.floor(this.currentTime),
        duration: Math.floor(this.duration)
      })
    } catch (error) {
      console.error('保存进度失败:', error)
    }
  },

  // 视频播放结束
  onVideoEnded() {
    // 自动播放下一节
    if (this.data.hasNext) {
      wx.showModal({
        title: '提示',
        content: '是否继续播放下一节？',
        success: (res) => {
          if (res.confirm) {
            this.playNext()
          }
        }
      })
    }
  },

  // 视频播放错误
  onVideoError(e) {
    console.error('视频播放错误:', e)
    wx.showToast({
      title: '视频播放失败',
      icon: 'none'
    })
  },

  // 视频暂停
  onVideoPause() {
    this.saveProgress()
  },

  // 展开/收起章节
  toggleChapter(e) {
    const index = e.currentTarget.dataset.index
    const chapters = this.data.chapters
    chapters[index].expanded = !chapters[index].expanded
    this.setData({ chapters })
  },

  // 切换视频
  switchVideo(e) {
    const video = e.currentTarget.dataset.video
    if (video.id === this.data.currentVideo.id) return

    // 保存当前进度
    this.saveProgress()

    // 更新当前视频
    const currentIndex = this.data.allVideos.findIndex(v => v.id === video.id)
    
    this.setData({
      videoId: video.id,
      currentVideo: video,
      currentIndex,
      hasPrev: currentIndex > 0,
      hasNext: currentIndex < this.data.allVideos.length - 1,
      progressPercent: 0,
      initialTime: 0
    })

    this.loadVideoUrl(video.id)
  },

  // 上一节
  playPrev() {
    if (!this.data.hasPrev) return
    const prevVideo = this.data.allVideos[this.data.currentIndex - 1]
    this.switchVideo({ currentTarget: { dataset: { video: prevVideo } } })
  },

  // 下一节
  playNext() {
    if (!this.data.hasNext) return
    const nextVideo = this.data.allVideos[this.data.currentIndex + 1]
    this.switchVideo({ currentTarget: { dataset: { video: nextVideo } } })
  }
})
