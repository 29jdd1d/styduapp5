// pages/community/list/list.js
const { communityApi } = require('../../../utils/api')
const { formatRelativeTime, postTypeMap } = require('../../../utils/util')

Page({
  data: {
    posts: [],
    currentType: '',
    currentSort: 'new',
    page: 1,
    pageSize: 10,
    hasMore: true,
    loading: false,
    isRefreshing: false
  },

  onLoad(options) {
    this.loadPosts()
  },

  onShow() {
    // 从发布页返回时刷新列表
    if (getApp().globalData.needRefreshCommunity) {
      this.onRefresh()
      getApp().globalData.needRefreshCommunity = false
    }
  },

  // 加载帖子列表
  async loadPosts() {
    if (this.data.loading || !this.data.hasMore) return

    this.setData({ loading: true })

    try {
      const res = await communityApi.getPostList({
        type: this.data.currentType,
        sort: this.data.currentSort,
        pageNum: this.data.page,
        pageSize: this.data.pageSize
      })

      if (res.code === 200) {
        const newPosts = (res.data.records || res.data.list || []).map(post => ({
          ...post,
          typeText: postTypeMap[post.type] || '其他',
          createTimeStr: formatRelativeTime(post.createTime),
          images: post.images ? (typeof post.images === 'string' ? post.images.split(',') : post.images) : []
        }))

        this.setData({
          posts: this.data.page === 1 ? newPosts : [...this.data.posts, ...newPosts],
          hasMore: newPosts.length >= this.data.pageSize,
          page: this.data.page + 1
        })
      }
    } catch (error) {
      console.error('加载帖子失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({
      isRefreshing: true,
      page: 1,
      hasMore: true,
      posts: []
    })

    await this.loadPosts()

    this.setData({ isRefreshing: false })
  },

  // 加载更多
  loadMore() {
    this.loadPosts()
  },

  // 切换类型
  onTypeChange(e) {
    const type = e.currentTarget.dataset.type
    if (type === this.data.currentType) return

    this.setData({
      currentType: type,
      page: 1,
      hasMore: true,
      posts: []
    })

    this.loadPosts()
  },

  // 切换排序
  toggleSort() {
    const newSort = this.data.currentSort === 'new' ? 'hot' : 'new'
    
    this.setData({
      currentSort: newSort,
      page: 1,
      hasMore: true,
      posts: []
    })

    this.loadPosts()
  },

  // 跳转到帖子详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/community/detail/detail?id=${id}`
    })
  },

  // 点赞/取消点赞
  async toggleLike(e) {
    const { id, index } = e.currentTarget.dataset
    const post = this.data.posts[index]

    try {
      if (post.isLiked) {
        await communityApi.unlikePost(id)
        this.setData({
          [`posts[${index}].isLiked`]: false,
          [`posts[${index}].likeCount`]: Math.max(0, (post.likeCount || 0) - 1)
        })
      } else {
        await communityApi.likePost(id)
        this.setData({
          [`posts[${index}].isLiked`]: true,
          [`posts[${index}].likeCount`]: (post.likeCount || 0) + 1
        })
      }
    } catch (error) {
      console.error('操作失败:', error)
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      })
    }
  },

  // 跳转到发布页
  goToPublish() {
    wx.navigateTo({
      url: '/pages/community/publish/publish'
    })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '考研学习社区 - 一起交流学习经验',
      path: '/pages/community/list/list'
    }
  }
})
