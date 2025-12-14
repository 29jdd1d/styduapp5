import request from '@/utils/request'

// 获取课程列表
export function getCourseList(params) {
  return request({
    url: '/admin/course/list',
    method: 'get',
    params
  })
}

// 获取课程详情
export function getCourseDetail(id) {
  return request({
    url: `/admin/course/${id}`,
    method: 'get'
  })
}

// 添加课程
export function addCourse(data) {
  return request({
    url: '/admin/course',
    method: 'post',
    data
  })
}

// 更新课程
export function updateCourse(id, data) {
  return request({
    url: `/admin/course/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export function deleteCourse(id) {
  return request({
    url: `/admin/course/${id}`,
    method: 'delete'
  })
}

// 更新课程状态
export function updateCourseStatus(id, status) {
  return request({
    url: `/admin/course/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 添加章节
export function addChapter(data) {
  return request({
    url: '/admin/course/chapter',
    method: 'post',
    data
  })
}

// 更新章节
export function updateChapter(id, data) {
  return request({
    url: `/admin/course/chapter/${id}`,
    method: 'put',
    data
  })
}

// 删除章节
export function deleteChapter(id) {
  return request({
    url: `/admin/course/chapter/${id}`,
    method: 'delete'
  })
}

// 添加视频
export function addVideo(data) {
  return request({
    url: '/admin/course/video',
    method: 'post',
    data
  })
}

// 更新视频
export function updateVideo(id, data) {
  return request({
    url: `/admin/course/video/${id}`,
    method: 'put',
    data
  })
}

// 删除视频
export function deleteVideo(id) {
  return request({
    url: `/admin/course/video/${id}`,
    method: 'delete'
  })
}
