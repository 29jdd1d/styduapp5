// pages/exam/doing/doing.js
const { examApi } = require('../../../utils/api')
const { questionTypeMap } = require('../../../utils/util')

Page({
  data: {
    examId: null,
    exam: {},
    questions: [],
    currentIndex: 0,
    remainTime: 0,
    remainTimeStr: '00:00:00',
    showCard: false,
    answeredCount: 0,
    timer: null
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ examId: options.id })
      this.loadExam()
    }
  },

  onUnload() {
    this.clearTimer()
    this.saveProgress()
  },

  onHide() {
    this.saveProgress()
  },

  // 加载考试
  async loadExam() {
    wx.showLoading({ title: '加载中...' })

    try {
      const res = await examApi.startExam(this.data.examId)

      if (res.code === 200) {
        const exam = res.data
        const questions = (exam.questions || []).map(q => ({
          ...q,
          typeText: questionTypeMap[q.type] || '未知',
          options: q.options ? (typeof q.options === 'string' ? JSON.parse(q.options) : q.options) : [],
          userAnswer: q.userAnswer || ''
        }))

        this.setData({
          exam,
          questions,
          remainTime: exam.remainTime || exam.duration * 60
        })

        this.updateAnsweredCount()
        this.startTimer()
      }
    } catch (error) {
      console.error('加载考试失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 开始计时
  startTimer() {
    this.clearTimer()
    
    this.data.timer = setInterval(() => {
      let remainTime = this.data.remainTime - 1
      
      if (remainTime <= 0) {
        this.clearTimer()
        this.autoSubmit()
        return
      }

      const hours = Math.floor(remainTime / 3600)
      const minutes = Math.floor((remainTime % 3600) / 60)
      const seconds = remainTime % 60

      this.setData({
        remainTime,
        remainTimeStr: `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      })
    }, 1000)
  },

  // 清除计时器
  clearTimer() {
    if (this.data.timer) {
      clearInterval(this.data.timer)
      this.data.timer = null
    }
  },

  // 滑动切换
  onSwiperChange(e) {
    this.setData({
      currentIndex: e.detail.current
    })
  },

  // 上一题
  prevQuestion() {
    if (this.data.currentIndex > 0) {
      this.setData({
        currentIndex: this.data.currentIndex - 1
      })
    }
  },

  // 下一题
  nextQuestion() {
    if (this.data.currentIndex < this.data.questions.length - 1) {
      this.setData({
        currentIndex: this.data.currentIndex + 1
      })
    }
  },

  // 选择选项（单选/多选）
  selectOption(e) {
    const { qindex, key } = e.currentTarget.dataset
    const question = this.data.questions[qindex]
    let userAnswer = question.userAnswer || ''

    if (question.type === 1) {
      // 单选
      userAnswer = key
    } else if (question.type === 2) {
      // 多选
      const answers = userAnswer.split('')
      const index = answers.indexOf(key)
      if (index > -1) {
        answers.splice(index, 1)
      } else {
        answers.push(key)
        answers.sort()
      }
      userAnswer = answers.join('')
    }

    this.setData({
      [`questions[${qindex}].userAnswer`]: userAnswer
    })

    this.updateAnsweredCount()
  },

  // 选择判断
  selectJudge(e) {
    const { qindex, value } = e.currentTarget.dataset
    this.setData({
      [`questions[${qindex}].userAnswer`]: value
    })
    this.updateAnsweredCount()
  },

  // 输入答案
  onInputAnswer(e) {
    const qindex = e.currentTarget.dataset.qindex
    this.setData({
      [`questions[${qindex}].userAnswer`]: e.detail.value
    })
    this.updateAnsweredCount()
  },

  // 更新已答数量
  updateAnsweredCount() {
    const answeredCount = this.data.questions.filter(q => q.userAnswer && q.userAnswer.trim()).length
    this.setData({ answeredCount })
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url
    wx.previewImage({
      current: url,
      urls: [url]
    })
  },

  // 显示答题卡
  showAnswerCard() {
    this.setData({ showCard: true })
  },

  // 隐藏答题卡
  hideAnswerCard() {
    this.setData({ showCard: false })
  },

  // 跳转到指定题目
  jumpToQuestion(e) {
    const index = e.currentTarget.dataset.index
    this.setData({
      currentIndex: index,
      showCard: false
    })
  },

  // 保存进度
  async saveProgress() {
    try {
      const answers = this.data.questions.map(q => ({
        questionId: q.id,
        answer: q.userAnswer
      }))

      await examApi.saveExamProgress(this.data.examId, {
        answers,
        remainTime: this.data.remainTime
      })
    } catch (error) {
      console.error('保存进度失败:', error)
    }
  },

  // 确认交卷
  confirmSubmit() {
    const unanswered = this.data.questions.length - this.data.answeredCount

    let content = '确定要交卷吗？'
    if (unanswered > 0) {
      content = `还有${unanswered}道题未作答，确定要交卷吗？`
    }

    wx.showModal({
      title: '提示',
      content,
      success: (res) => {
        if (res.confirm) {
          this.submitExam()
        }
      }
    })
  },

  // 自动交卷
  autoSubmit() {
    wx.showToast({
      title: '考试时间到，自动交卷',
      icon: 'none',
      duration: 2000
    })

    setTimeout(() => {
      this.submitExam()
    }, 1000)
  },

  // 提交考试
  async submitExam() {
    wx.showLoading({ title: '提交中...' })

    try {
      const answers = this.data.questions.map(q => ({
        questionId: q.id,
        answer: q.userAnswer
      }))

      const res = await examApi.submitExam(this.data.examId, { answers })

      if (res.code === 200) {
        this.clearTimer()

        // 通知列表刷新
        getApp().globalData.needRefreshExam = true

        // 跳转到结果页
        wx.redirectTo({
          url: `/pages/exam/result/result?id=${this.data.examId}`
        })
      }
    } catch (error) {
      console.error('提交失败:', error)
      wx.showToast({
        title: '提交失败，请重试',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  }
})
