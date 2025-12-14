// pages/checkin/ranking/ranking.js
const { checkinApi } = require('../../../utils/api')

Page({
  data: {
    activeTab: 'streak',
    rankingList: [],
    myRanking: null,
    loading: true
  },

  onLoad() {
    this.loadRanking()
  },

  // 切换Tab
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    if (tab === this.data.activeTab) return
    
    this.setData({ activeTab: tab })
    this.loadRanking()
  },

  // 加载排行榜
  async loadRanking() {
    this.setData({ loading: true })

    try {
      const { activeTab } = this.data
      
      // 加载排行榜和我的排名
      const [rankingRes, myRes] = await Promise.all([
        activeTab === 'streak' 
          ? checkinApi.getStreakRanking(50)
          : checkinApi.getTotalRanking(50),
        checkinApi.getMyRanking(activeTab)
      ])

      this.setData({
        rankingList: rankingRes.data || [],
        myRanking: myRes.data,
        loading: false
      })
    } catch (error) {
      console.error('加载排行榜失败:', error)
      this.setData({ loading: false })
    }
  }
})
