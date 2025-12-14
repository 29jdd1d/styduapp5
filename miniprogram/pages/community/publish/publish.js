// pages/community/publish/publish.js
const { communityApi, uploadApi } = require('../../../utils/api')

Page({
  data: {
    postType: '1',
    title: '',
    content: '',
    images: [],
    canPublish: false,
    uploading: false
  },

  onLoad(options) {
    // 如果有预设类型
    if (options.type) {
      this.setData({ postType: options.type })
    }
  },

  // 选择类型
  selectType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ postType: type })
  },

  // 输入标题
  onTitleInput(e) {
    this.setData({
      title: e.detail.value
    })
    this.checkCanPublish()
  },

  // 输入内容
  onContentInput(e) {
    this.setData({
      content: e.detail.value
    })
    this.checkCanPublish()
  },

  // 检查是否可以发布
  checkCanPublish() {
    const { title, content, postType } = this.data
    const canPublish = postType && title.trim().length >= 5 && content.trim().length >= 10
    this.setData({ canPublish })
  },

  // 选择图片
  chooseImage() {
    const remainCount = 9 - this.data.images.length
    
    wx.chooseMedia({
      count: remainCount,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newImages = res.tempFiles.map(file => file.tempFilePath)
        this.setData({
          images: [...this.data.images, ...newImages]
        })
      }
    })
  },

  // 删除图片
  deleteImage(e) {
    const index = e.currentTarget.dataset.index
    const images = [...this.data.images]
    images.splice(index, 1)
    this.setData({ images })
  },

  // 上传图片
  async uploadImages() {
    const uploadedUrls = []
    
    for (const tempPath of this.data.images) {
      // 如果已经是网络图片，跳过
      if (tempPath.startsWith('http')) {
        uploadedUrls.push(tempPath)
        continue
      }

      try {
        const res = await new Promise((resolve, reject) => {
          wx.uploadFile({
            url: uploadApi.getUploadUrl(),
            filePath: tempPath,
            name: 'file',
            header: {
              'Authorization': 'Bearer ' + wx.getStorageSync('token')
            },
            success: (res) => {
              const data = JSON.parse(res.data)
              if (data.code === 200) {
                resolve(data.data)
              } else {
                reject(new Error(data.message || '上传失败'))
              }
            },
            fail: reject
          })
        })
        uploadedUrls.push(res)
      } catch (error) {
        console.error('上传图片失败:', error)
        throw error
      }
    }

    return uploadedUrls
  },

  // 提交帖子
  async submitPost() {
    if (!this.data.canPublish) {
      wx.showToast({
        title: '请完善帖子内容',
        icon: 'none'
      })
      return
    }

    const { title, content, postType, images } = this.data

    // 校验
    if (title.trim().length < 5) {
      wx.showToast({
        title: '标题至少5个字',
        icon: 'none'
      })
      return
    }

    if (content.trim().length < 10) {
      wx.showToast({
        title: '内容至少10个字',
        icon: 'none'
      })
      return
    }

    wx.showLoading({ title: '发布中...' })

    try {
      // 上传图片
      let imageUrls = []
      if (images.length > 0) {
        this.setData({ uploading: true })
        imageUrls = await this.uploadImages()
      }

      // 发布帖子
      const res = await communityApi.createPost({
        type: postType,
        title: title.trim(),
        content: content.trim(),
        images: imageUrls.join(',')
      })

      if (res.code === 200) {
        wx.showToast({
          title: '发布成功',
          icon: 'success'
        })

        // 通知列表刷新
        getApp().globalData.needRefreshCommunity = true

        // 返回上一页
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        throw new Error(res.message || '发布失败')
      }
    } catch (error) {
      console.error('发布失败:', error)
      wx.showToast({
        title: error.message || '发布失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
      this.setData({ uploading: false })
    }
  }
})
