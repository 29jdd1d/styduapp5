import request from '@/utils/request'

// 管理员登录
export function login(data) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data
  })
}

// 获取管理员信息
export function getAdminInfo() {
  return request({
    url: '/admin/auth/info',
    method: 'get'
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/admin/auth/password',
    method: 'post',
    params: data
  })
}

// 登出
export function logout() {
  return request({
    url: '/admin/auth/logout',
    method: 'post'
  })
}
