// 心理评估相关API
const request = require('../utils/request')

// 获取评估列表
const getAssessments = (params) => {
  return request.get('/assessments/list', params)
}

// 获取评估详情
const getAssessmentDetail = (id) => {
  return request.get(`/assessments/${id}`)
}

// 开始评估（获取题目）
const startAssessment = (id) => {
  return request.get(`/assessments/${id}/questions`)
}

// 提交评估答案
const submitAssessment = (data) => {
  const assessmentId = data.assessmentId
  // 提交数据格式: {userId, answers: {questionId: answer}, timeSpent}
  const submitData = {
    userId: data.userId,
    answers: data.answers,
    timeSpent: data.timeSpent || 0
  }
  
  console.log(`提交到: /assessments/${assessmentId}/submit`, submitData)
  
  return request.post(`/assessments/${assessmentId}/submit`, submitData)
}

// 获取评估结果
const getAssessmentResult = (id, userId) => {
  return request.get(`/assessments/result/${id}?userId=${userId}`)
}

// 获取评估历史
const getAssessmentHistory = (userId) => {
  return request.get(`/assessments/user/${userId}/history`)
}

module.exports = {
  getAssessments,
  getAssessmentDetail,
  startAssessment,
  submitAssessment,
  getAssessmentResult,
  getAssessmentHistory
}

