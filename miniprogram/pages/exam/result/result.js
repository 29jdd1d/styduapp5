// pages/exam/result/result.js
const { examApi } = require('../../../utils/api')
const { questionTypeMap } = require('../../../utils/util')

Page({
  data: {
    examId: null,
    result: {
      score: 0,
      totalScore: 100,
      passScore: 60,
      passed: false,
      usedTimeStr: '-',
      correctCount: 0,
      wrongCount: 0,
      unansweredCount: 0,
      accuracy: 0
    },
    questions: [],
    filteredQuestions: [],
    filter: 'all'
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ examId: options.id })
      this.loadResult()
    }
  },

  // 加载考试结果
  async loadResult() {
    wx.showLoading({ title: '加载中...' })

    try {
      const res = await examApi.getExamResult(this.data.examId)

      if (res.code === 200) {
        const data = res.data
        
        // 处理时间
        const usedSeconds = data.usedTime || 0
        const minutes = Math.floor(usedSeconds / 60)
        const seconds = usedSeconds % 60
        const usedTimeStr = `${minutes}分${seconds}秒`

        // 处理题目
        const questions = (data.questions || []).map((q, index) => ({
          ...q,
          index: index + 1,
          typeText: questionTypeMap[q.type] || '未知',
          options: q.options ? (typeof q.options === 'string' ? JSON.parse(q.options) : q.options) : [],
          isCorrect: q.userAnswer === q.answer,
          expanded: false
        }))

        // 统计
        const correctCount = questions.filter(q => q.isCorrect).length
        const unansweredCount = questions.filter(q => !q.userAnswer).length
        const wrongCount = questions.length - correctCount - unansweredCount
        const accuracy = questions.length > 0 ? Math.round((correctCount / questions.length) * 100) : 0

        this.setData({
          result: {
            score: data.score || 0,
            totalScore: data.totalScore || 100,
            passScore: data.passScore || 60,
            passed: (data.score || 0) >= (data.passScore || 60),
            usedTimeStr,
            correctCount,
            wrongCount,
            unansweredCount,
            accuracy
          },
          questions,
          filteredQuestions: questions
        })
      }
    } catch (error) {
      console.error('加载结果失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 设置筛选
  setFilter(e) {
    const filter = e.currentTarget.dataset.filter
    let filteredQuestions = this.data.questions

    if (filter === 'wrong') {
      filteredQuestions = this.data.questions.filter(q => !q.isCorrect)
    }

    this.setData({
      filter,
      filteredQuestions
    })
  },

  // 切换题目展开
  toggleQuestion(e) {
    const index = e.currentTarget.dataset.index
    const expanded = this.data.filteredQuestions[index].expanded
    this.setData({
      [`filteredQuestions[${index}].expanded`]: !expanded
    })
  },

  // 返回列表
  goBack() {
    wx.navigateBack()
  },

  // 复习错题
  reviewWrong() {
    const wrongIds = this.data.questions
      .filter(q => !q.isCorrect)
      .map(q => q.id)
      .join(',')
    
    wx.navigateTo({
      url: `/pages/question/practice/practice?ids=${wrongIds}&mode=review`
    })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: `我在模拟考试中获得了${this.data.result.score}分`,
      path: `/pages/exam/list/list`
    }
  }
})
