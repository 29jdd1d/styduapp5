/**
 * HTTP 请求工具类
 */
const app = getApp()

const request = (url, method, data = {}, showLoading = true) => {
  return new Promise((resolve, reject) => {
    if (showLoading) {
      wx.showLoading({ title: '加载中...', mask: true })
    }

    wx.request({
      url: `${app.globalData.baseUrl}${url}`,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : ''
      },
      success: (res) => {
        if (showLoading) wx.hideLoading()
        
        // HTTP 401 状态码处理（Gateway 返回的未授权）
        if (res.statusCode === 401) {
          console.warn('认证失败，跳转登录页', res.data)
          app.clearLoginInfo()
          wx.redirectTo({ url: '/pages/login/login' })
          reject(new Error(res.data?.message || '登录已过期'))
          return
        }
        
        if (res.statusCode === 200) {
          if (res.data.code === 200 || res.data.code === 0) {
            resolve(res.data)
          } else if (res.data.code === 401 || res.data.code === 1001) {
            // 业务层返回的未授权
            app.clearLoginInfo()
            wx.redirectTo({ url: '/pages/login/login' })
            reject(new Error('登录已过期'))
          } else {
            wx.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(res.data)
          }
        } else {
          console.error('HTTP请求失败', res.statusCode, res.data)
          wx.showToast({
            title: res.data?.message || '网络请求失败',
            icon: 'none'
          })
          reject(new Error('网络请求失败'))
        }
      },
      fail: (err) => {
        if (showLoading) wx.hideLoading()
        wx.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// GET 请求
const get = (url, data = {}, showLoading = true) => {
  return request(url, 'GET', data, showLoading)
}

// POST 请求
const post = (url, data = {}, showLoading = true) => {
  return request(url, 'POST', data, showLoading)
}

// PUT 请求
const put = (url, data = {}, showLoading = true) => {
  return request(url, 'PUT', data, showLoading)
}

// DELETE 请求
const del = (url, data = {}, showLoading = true) => {
  return request(url, 'DELETE', data, showLoading)
}

// 上传文件
const upload = (url, filePath, name = 'file', formData = {}) => {
  return new Promise((resolve, reject) => {
    wx.showLoading({ title: '上传中...', mask: true })
    
    wx.uploadFile({
      url: `${app.globalData.baseUrl}${url}`,
      filePath: filePath,
      name: name,
      formData: formData,
      header: {
        'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : ''
      },
      success: (res) => {
        wx.hideLoading()
        const data = JSON.parse(res.data)
        if (data.code === 200 || data.code === 0) {
          resolve(data)
        } else {
          wx.showToast({ title: data.message || '上传失败', icon: 'none' })
          reject(data)
        }
      },
      fail: (err) => {
        wx.hideLoading()
        wx.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  request,
  get,
  post,
  put,
  del,
  upload
}
