// pages/question/result/result.js
const { formatDuration } = require('../../../utils/util')

Page({
  data: {
    total: 0,
    correct: 0,
    wrong: 0,
    correctRate: 0,
    time: 0,
    timeStr: '00:00',
    isPass: false,
    encourageText: ''
  },

  onLoad(options) {
    const total = parseInt(options.total) || 0
    const correct = parseInt(options.correct) || 0
    const time = parseInt(options.time) || 0
    const wrong = total - correct
    const correctRate = total > 0 ? Math.round((correct / total) * 100) : 0
    const isPass = correctRate >= 60
    const timeStr = formatDuration(time)

    // 根据正确率生成鼓励语
    let encourageText = ''
    if (correctRate >= 90) {
      encourageText = '太优秀了！你已经完全掌握了这些知识点，继续保持！'
    } else if (correctRate >= 70) {
      encourageText = '表现不错！再接再厉，向更高的目标进发！'
    } else if (correctRate >= 60) {
      encourageText = '及格啦！还有进步的空间，多多练习吧！'
    } else {
      encourageText = '不要灰心！学习是一个积累的过程，坚持就是胜利！'
    }

    this.setData({
      total,
      correct,
      wrong,
      correctRate,
      time,
      timeStr,
      isPass,
      encourageText
    })
  },

  // 查看错题
  viewWrong() {
    wx.navigateTo({
      url: '/pages/question/wrong/wrong'
    })
  },

  // 继续刷题
  continuePractice() {
    wx.redirectTo({
      url: '/pages/question/practice/practice?mode=random'
    })
  },

  // 返回题库
  goBack() {
    wx.switchTab({
      url: '/pages/question/category/category'
    })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: `我在考研学习刷题，正确率${this.data.correctRate}%，一起来学习吧！`,
      path: '/pages/index/index'
    }
  }
})
