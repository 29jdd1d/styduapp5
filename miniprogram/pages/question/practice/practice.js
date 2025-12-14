// pages/question/practice/practice.js
const app = getApp()
const { questionApi, practiceApi } = require('../../../utils/api')
const { questionTypeMap, difficultyMap } = require('../../../utils/util')

Page({
  data: {
    mode: 'random', // random, category, wrong, favorite
    categoryId: null,
    questions: [],
    currentIndex: 0,
    showSheet: false,
    showTimer: false,
    timerText: '00:00',
    answeredCount: 0,
    correctCount: 0,
    showAnswerStats: false,
    recordId: null
  },

  timer: null,
  startTime: null,
  seconds: 0,

  onLoad(options) {
    const { mode, categoryId, startIndex } = options
    this.setData({ 
      mode: mode || 'random',
      categoryId,
      currentIndex: parseInt(startIndex) || 0
    })
    
    this.loadQuestions()
  },

  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },

  // 加载题目
  async loadQuestions() {
    wx.showLoading({ title: '加载中...' })
    
    try {
      let questions = []
      const majorId = app.globalData.majorId

      switch (this.data.mode) {
        case 'random':
          const randomRes = await questionApi.getRandomQuestions(majorId, null, 20)
          questions = randomRes.data || []
          break
        case 'category':
          const categoryRes = await questionApi.getQuestionList(this.data.categoryId)
          questions = categoryRes.data || []
          break
        case 'wrong':
          const wrongRes = await practiceApi.getWrongQuestions(1, 50)
          questions = (wrongRes.data?.list || []).map(w => w.question)
          break
        case 'favorite':
          const favoriteRes = await practiceApi.getFavoriteQuestions(1, 50)
          questions = (favoriteRes.data?.list || []).map(f => f.question)
          break
      }

      // 处理题目数据
      questions = questions.map(q => this.processQuestion(q))

      this.setData({ questions })
      
      // 开始练习记录
      this.startPractice()
      
      // 启动计时器
      this.startTimer()
    } catch (error) {
      console.error('加载题目失败:', error)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      wx.hideLoading()
    }
  },

  // 处理题目数据
  processQuestion(q) {
    let options = []
    try {
      if (q.options) {
        const parsed = typeof q.options === 'string' ? JSON.parse(q.options) : q.options
        if (Array.isArray(parsed)) {
          options = parsed
        } else if (typeof parsed === 'object') {
          options = Object.keys(parsed).map(key => ({ key, value: parsed[key] }))
        }
      }
    } catch (e) {
      console.error('解析选项失败:', e)
    }

    // 生成答案文本
    let answerText = q.answer
    if (q.type === 1 || q.type === 2) {
      answerText = q.answer
    } else if (q.type === 3) {
      answerText = q.answer === '1' ? '正确' : '错误'
    }

    return {
      ...q,
      options,
      typeText: questionTypeMap[q.type] || '其他',
      difficultyText: difficultyMap[q.difficulty]?.text || '未知',
      answerText,
      userAnswer: null,
      userAnswerText: '',
      showAnswer: false,
      isCorrect: false,
      isFavorite: false
    }
  },

  // 开始练习记录
  async startPractice() {
    try {
      const res = await practiceApi.startPractice({
        mode: this.getModeValue(),
        categoryId: this.data.categoryId,
        count: this.data.questions.length
      })
      this.setData({ recordId: res.data?.id })
    } catch (error) {
      console.error('开始练习记录失败:', error)
    }
  },

  getModeValue() {
    const modeMap = { random: 2, category: 1, wrong: 3, favorite: 4 }
    return modeMap[this.data.mode] || 1
  },

  // 启动计时器
  startTimer() {
    this.startTime = Date.now()
    this.timer = setInterval(() => {
      this.seconds = Math.floor((Date.now() - this.startTime) / 1000)
      const minutes = Math.floor(this.seconds / 60)
      const secs = this.seconds % 60
      this.setData({
        timerText: `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`,
        showTimer: true
      })
    }, 1000)
  },

  // 滑动切换
  onSwiperChange(e) {
    this.setData({ currentIndex: e.detail.current })
  },

  // 选择选项（单选）
  selectOption(e) {
    const { qindex, option } = e.currentTarget.dataset
    const questions = this.data.questions
    const question = questions[qindex]
    
    if (question.showAnswer) return

    if (question.type === 1) {
      // 单选题：直接选中并显示答案
      question.userAnswer = option
      question.userAnswerText = option
      this.checkAnswer(question)
    } else if (question.type === 2) {
      // 多选题：切换选中状态
      let userAnswer = question.userAnswer || ''
      if (userAnswer.includes(option)) {
        userAnswer = userAnswer.replace(option, '')
      } else {
        userAnswer += option
      }
      // 按字母排序
      userAnswer = userAnswer.split('').sort().join('')
      question.userAnswer = userAnswer
      question.userAnswerText = userAnswer
    }

    this.setData({ questions })
    this.updateStats()
  },

  // 确认多选答案
  confirmMultiAnswer(e) {
    const index = e.currentTarget.dataset.index
    const questions = this.data.questions
    const question = questions[index]
    
    if (!question.userAnswer) {
      wx.showToast({ title: '请选择答案', icon: 'none' })
      return
    }

    this.checkAnswer(question)
    this.setData({ questions })
    this.updateStats()
  },

  // 判断题选择
  selectJudge(e) {
    const { qindex, answer } = e.currentTarget.dataset
    const questions = this.data.questions
    const question = questions[qindex]
    
    if (question.showAnswer) return

    question.userAnswer = answer
    question.userAnswerText = answer === '1' ? '正确' : '错误'
    this.checkAnswer(question)
    
    this.setData({ questions })
    this.updateStats()
  },

  // 检查答案
  checkAnswer(question) {
    question.showAnswer = true
    question.isCorrect = question.userAnswer === question.answer
    
    // 提交答案到服务器
    this.submitAnswer(question)
  },

  // 提交答案
  async submitAnswer(question) {
    if (!this.data.recordId) return
    
    try {
      await practiceApi.submitAnswer({
        recordId: this.data.recordId,
        questionId: question.id,
        answer: question.userAnswer
      })
    } catch (error) {
      console.error('提交答案失败:', error)
    }
  },

  // 更新统计
  updateStats() {
    const questions = this.data.questions
    const answeredCount = questions.filter(q => q.userAnswer).length
    const correctCount = questions.filter(q => q.isCorrect).length
    
    this.setData({ answeredCount, correctCount })
  },

  // 收藏/取消收藏
  async toggleFavorite(e) {
    const index = e.currentTarget.dataset.index
    const questions = this.data.questions
    const question = questions[index]

    try {
      if (question.isFavorite) {
        await practiceApi.unfavoriteQuestion(question.id)
        question.isFavorite = false
        wx.showToast({ title: '已取消收藏', icon: 'none' })
      } else {
        await practiceApi.favoriteQuestion(question.id)
        question.isFavorite = true
        wx.showToast({ title: '已收藏', icon: 'none' })
      }
      this.setData({ questions })
    } catch (error) {
      console.error('操作失败:', error)
    }
  },

  // 上一题
  prevQuestion() {
    if (this.data.currentIndex > 0) {
      this.setData({ currentIndex: this.data.currentIndex - 1 })
    }
  },

  // 下一题
  nextQuestion() {
    if (this.data.currentIndex < this.data.questions.length - 1) {
      this.setData({ currentIndex: this.data.currentIndex + 1 })
    }
  },

  // 显示答题卡
  showAnswerSheet() {
    this.setData({ 
      showSheet: true,
      showAnswerStats: this.data.questions.some(q => q.showAnswer)
    })
  },

  // 隐藏答题卡
  hideAnswerSheet() {
    this.setData({ showSheet: false })
  },

  // 阻止冒泡
  stopPropagation() {},

  // 跳转到指定题目
  jumpToQuestion(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ 
      currentIndex: index,
      showSheet: false
    })
  },

  // 完成练习
  async finishPractice() {
    if (this.timer) {
      clearInterval(this.timer)
    }

    // 检查是否有未答题目
    const unanswered = this.data.questions.filter(q => !q.userAnswer).length
    if (unanswered > 0) {
      const res = await wx.showModal({
        title: '提示',
        content: `还有${unanswered}道题未作答，确定要交卷吗？`
      })
      if (!res.confirm) return
    }

    // 结束练习记录
    if (this.data.recordId) {
      try {
        await practiceApi.finishPractice(this.data.recordId)
      } catch (error) {
        console.error('结束练习失败:', error)
      }
    }

    // 跳转到结果页
    const correctCount = this.data.questions.filter(q => q.isCorrect).length
    wx.redirectTo({
      url: `/pages/question/result/result?total=${this.data.questions.length}&correct=${correctCount}&time=${this.seconds}`
    })
  }
})
