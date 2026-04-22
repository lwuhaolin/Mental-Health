// AI相关API
const request = require('../utils/request')

// AI疗愈对话
const therapyChat = (data) => {
  return request.post('/ai/therapy', data)
}

// AI评估对话
const assessmentChat = (data) => {
  return request.post('/ai/assessment', data)
}

// 获取对话历史
const getChatHistory = (params) => {
  return request.get('/ai/history', params)
}

// 健康检查
const healthCheck = () => {
  return request.get('/ai/health')
}

// 删除单条历史记录
const deleteHistory = (interactionId, userId) => {
  return request.delete(`/ai/history/${interactionId}?userId=${userId}`)
}

// 清空所有历史记录
const clearHistory = (userId) => {
  return request.delete(`/ai/history/clear?userId=${userId}`)
}

module.exports = {
  therapyChat,
  assessmentChat,
  getChatHistory,
  healthCheck,
  deleteHistory,
  clearHistory
}

