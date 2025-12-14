/**
 * 通用工具函数
 */

// 格式化日期
const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

// 格式化相对时间
const formatRelativeTime = (timestamp) => {
  const now = Date.now()
  const diff = now - new Date(timestamp).getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  
  return formatDate(timestamp)
}

// 格式化时长（秒 -> 时:分:秒）
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  
  if (h > 0) {
    return `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  }
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

// 格式化学习时长（分钟）
const formatStudyTime = (minutes) => {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return m > 0 ? `${h}小时${m}分钟` : `${h}小时`
}

// 格式化数字（超过1万显示 x.x万）
const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return String(num)
}

// 计算考研倒计时
const getExamCountdown = (examYear) => {
  if (!examYear) return null
  
  // 考研一般在12月底
  const examDate = new Date(examYear, 11, 25) // 12月25日左右
  const now = new Date()
  const diff = examDate.getTime() - now.getTime()
  
  if (diff <= 0) return { days: 0, passed: true }
  
  const days = Math.ceil(diff / (1000 * 60 * 60 * 24))
  return { days, passed: false }
}

// 防抖函数
const debounce = (fn, delay = 300) => {
  let timer = null
  return function(...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 节流函数
const throttle = (fn, delay = 300) => {
  let last = 0
  return function(...args) {
    const now = Date.now()
    if (now - last >= delay) {
      last = now
      fn.apply(this, args)
    }
  }
}

// 题型映射
const questionTypeMap = {
  1: '单选题',
  2: '多选题',
  3: '判断题',
  4: '填空题',
  5: '简答题'
}

// 难度映射
const difficultyMap = {
  1: { text: '简单', color: '#07c160' },
  2: { text: '中等', color: '#ff976a' },
  3: { text: '困难', color: '#ee0a24' }
}

// 帖子类型映射
const postTypeMap = {
  1: '经验分享',
  2: '问题求助',
  3: '资料分享',
  4: '闲聊灌水'
}

// 默认头像
const defaultAvatar = '/images/default-avatar.png'

// 默认封面
const defaultCover = '/images/default-cover.png'

module.exports = {
  formatDate,
  formatRelativeTime,
  formatDuration,
  formatStudyTime,
  formatNumber,
  getExamCountdown,
  debounce,
  throttle,
  questionTypeMap,
  difficultyMap,
  postTypeMap,
  defaultAvatar,
  defaultCover
}
