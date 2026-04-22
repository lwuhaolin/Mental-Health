import request from '@/utils/request'

// AI对话 - 疗愈室
export const therapyChat = (data) => {
  return request({
    url: '/ai/therapy',
    method: 'post',
    data
  })
}

// AI对话 - 心理评估
export const assessmentChat = (data) => {
  return request({
    url: '/ai/assessment',
    method: 'post',
    data
  })
}

// 获取AI对话历史
export const getAIChatHistory = (params) => {
  return request({
    url: '/ai/history',
    method: 'get',
    params
  })
}

// 健康检查
export const aiHealthCheck = () => {
  return request({
    url: '/ai/health',
    method: 'get'
  })
}