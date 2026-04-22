import request from '@/utils/request'

// 获取测评列表
export const getAssessments = (params) => {
  return request({
    url: '/assessments/list',
    method: 'get',
    params
  })
}

// 获取测评详情
export const getAssessmentDetail = (id) => {
  return request({
    url: `/assessments/${id}`,
    method: 'get'
  })
}

// 开始测评
export const startAssessment = (id) => {
  return request({
    url: `/assessments/${id}/questions`,
    method: 'get'
  })
}

// 提交测评答案
export const submitAssessment = (data) => {
  return request({
    url: `/assessments/${data.assessmentId}/submit`,
    method: 'post',
    data
  })
}

// 获取测评结果
export const getAssessmentResult = (id, userId) => {
  return request({
    url: `/assessments/result/${id}?userId=${userId}`,
    method: 'get'
  })
}

// 获取测评历史
export const getAssessmentHistory = (userId) => {
  return request({
    url: `/assessments/user/${userId}/history`,
    method: 'get'
  })
}