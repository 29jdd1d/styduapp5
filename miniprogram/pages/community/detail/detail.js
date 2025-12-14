// pages/community/detail/detail.js
const { communityApi } = require('../../../utils/api')
const { formatRelativeTime, postTypeMap } = require('../../../utils/util')

Page({
  data: {
    postId: null,
    post: {},
    comments: [],
    page: 1,
    pageSize: 20,
    hasMore: true,
    loading: false,
    commentContent: '',
    replyTo: null,  // { id, nickname }
    inputFocus: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ postId: options.id })
      this.loadPostDetail()
      this.loadComments()
    }
  },

  // 加载帖子详情
  async loadPostDetail() {
    try {
      const res = await communityApi.getPostDetail(this.data.postId)
      
      if (res.code === 200) {
        const post = res.data
        this.setData({
          post: {
            ...post,
            typeText: postTypeMap[post.type] || '其他',
            createTimeStr: formatRelativeTime(post.createTime),
            images: post.images ? (typeof post.images === 'string' ? post.images.split(',') : post.images) : []
          }
        })
      }
    } catch (error) {
      console.error('加载帖子详情失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 加载评论列表
  async loadComments() {
    if (this.data.loading || !this.data.hasMore) return

    this.setData({ loading: true })

    try {
      const res = await communityApi.getCommentList(this.data.postId, this.data.page, this.data.pageSize)

      if (res.code === 200) {
        const newComments = (res.data.records || res.data.list || []).map(comment => ({
          ...comment,
          createTimeStr: formatRelativeTime(comment.createTime)
        }))

        this.setData({
          comments: this.data.page === 1 ? newComments : [...this.data.comments, ...newComments],
          hasMore: newComments.length >= this.data.pageSize,
          page: this.data.page + 1
        })
      }
    } catch (error) {
      console.error('加载评论失败:', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  // 加载更多评论
  loadMoreComments() {
    this.loadComments()
  },

  // 预览图片
  previewImage(e) {
    const current = e.currentTarget.dataset.current
    wx.previewImage({
      current,
      urls: this.data.post.images
    })
  },

  // 点赞帖子
  async toggleLike() {
    const post = this.data.post

    try {
      if (post.isLiked) {
        await communityApi.unlikePost(this.data.postId)
        this.setData({
          'post.isLiked': false,
          'post.likeCount': Math.max(0, (post.likeCount || 0) - 1)
        })
      } else {
        await communityApi.likePost(this.data.postId)
        this.setData({
          'post.isLiked': true,
          'post.likeCount': (post.likeCount || 0) + 1
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

  // 点赞评论
  async likeComment(e) {
    const { id, index } = e.currentTarget.dataset
    const comment = this.data.comments[index]

    try {
      if (comment.isLiked) {
        // 取消点赞
        this.setData({
          [`comments[${index}].isLiked`]: false,
          [`comments[${index}].likeCount`]: Math.max(0, (comment.likeCount || 0) - 1)
        })
      } else {
        // 点赞
        this.setData({
          [`comments[${index}].isLiked`]: true,
          [`comments[${index}].likeCount`]: (comment.likeCount || 0) + 1
        })
      }
    } catch (error) {
      console.error('操作失败:', error)
    }
  },

  // 回复评论
  replyComment(e) {
    const { id, nickname } = e.currentTarget.dataset
    this.setData({
      replyTo: { id, nickname },
      inputFocus: true
    })
  },

  // 输入框失去焦点
  onInputBlur() {
    setTimeout(() => {
      if (!this.data.commentContent) {
        this.setData({ replyTo: null })
      }
    }, 100)
  },

  // 输入评论
  onCommentInput(e) {
    this.setData({
      commentContent: e.detail.value
    })
  },

  // 提交评论
  async submitComment() {
    const content = this.data.commentContent.trim()
    if (!content) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      })
      return
    }

    wx.showLoading({ title: '发送中...' })

    try {
      const params = {
        postId: this.data.postId,
        content
      }

      if (this.data.replyTo) {
        params.parentId = this.data.replyTo.id
      }

      const res = await communityApi.addComment(params)

      if (res.code === 200) {
        wx.showToast({
          title: '评论成功',
          icon: 'success'
        })

        // 清空输入
        this.setData({
          commentContent: '',
          replyTo: null,
          inputFocus: false
        })

        // 刷新评论列表
        this.setData({
          comments: [],
          page: 1,
          hasMore: true
        })
        this.loadComments()

        // 更新评论数
        this.setData({
          'post.commentCount': (this.data.post.commentCount || 0) + 1
        })
      }
    } catch (error) {
      console.error('评论失败:', error)
      wx.showToast({
        title: '评论失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 分享
  onShareAppMessage() {
    return {
      title: this.data.post.title,
      path: `/pages/community/detail/detail?id=${this.data.postId}`
    }
  }
})
