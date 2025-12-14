import request from '@/utils/request'

// 获取题目列表
export function getQuestionList(params) {
  return request({
    url: '/admin/question/list',
    method: 'get',
    params
  })
}

// 获取题目详情
export function getQuestionDetail(id) {
  return request({
    url: `/admin/question/${id}`,
    method: 'get'
  })
}

// 添加题目
export function addQuestion(data) {
  return request({
    url: '/admin/question',
    method: 'post',
    data
  })
}

// 更新题目
export function updateQuestion(id, data) {
  return request({
    url: `/admin/question/${id}`,
    method: 'put',
    data
  })
}

// 删除题目
export function deleteQuestion(id) {
  return request({
    url: `/admin/question/${id}`,
    method: 'delete'
  })
}

// 批量导入题目
export function importQuestions(data) {
  return request({
    url: '/admin/question/import',
    method: 'post',
    data
  })
}

// 获取分类列表
export function getCategoryList(majorId) {
  return request({
    url: '/admin/question/category/list',
    method: 'get',
    params: { majorId }
  })
}

// 添加分类
export function addCategory(data) {
  return request({
    url: '/admin/question/category',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(id, data) {
  return request({
    url: `/admin/question/category/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/admin/question/category/${id}`,
    method: 'delete'
  })
}
