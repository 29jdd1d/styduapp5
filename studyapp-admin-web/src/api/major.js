import request from '@/utils/request'

// 获取专业列表
export function getMajorList() {
  return request({
    url: '/admin/major/list',
    method: 'get'
  })
}

// 添加专业
export function addMajor(data) {
  return request({
    url: '/admin/major',
    method: 'post',
    data
  })
}

// 更新专业
export function updateMajor(id, data) {
  return request({
    url: `/admin/major/${id}`,
    method: 'put',
    data
  })
}

// 删除专业
export function deleteMajor(id) {
  return request({
    url: `/admin/major/${id}`,
    method: 'delete'
  })
}

// 更新专业状态
export function updateMajorStatus(id, status) {
  return request({
    url: `/admin/major/${id}/status`,
    method: 'put',
    params: { status }
  })
}
