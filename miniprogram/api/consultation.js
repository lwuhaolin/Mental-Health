// 咨询相关API
const request = require('../utils/request')

// 获取专家列表
const getPsychologists = (params) => {
  return request.get('/psychologist/list', params)
}

// 获取专家详情
const getPsychologistDetail = (id) => {
  return request.get(`/psychologist/detail/${id}`)
}

// 预约咨询 - 保存到咨询记录表
const bookConsultation = (data) => {
  return request.post('/consultation/book', data)
}

// 创建咨询预约 - 保存到预约表
const createAppointment = (data) => {
  // 格式化数据以匹配后端接口
  const appointmentData = {
    userId: data.userId,
    psychologistId: data.psychologistId,
    topic: data.description || data.title || '心理咨询',  // 使用description作为topic
    contactInfo: data.contactInfo || data.phone || '无',  // 联系方式
    preferredTime: data.appointmentTime || data.scheduledTime  // 预约时间
  }
  
  console.log('提交预约数据:', appointmentData)
  
  return request.post('/consultation/appointment', appointmentData)
}

// 获取用户咨询记录
const getUserConsultations = (userId) => {
  return request.get(`/consultation/user/${userId}`)
}

// 获取咨询详情
const getConsultationDetail = (id) => {
  return request.get(`/consultation/detail/${id}`)
}

// 发送消息
const sendMessage = (data) => {
  return request.post('/consultation/message/send', data)
}

// 获取消息列表
const getMessages = (consultationId) => {
  return request.get(`/consultation/message/list/${consultationId}`)
}

// 标记消息已读
const markMessageAsRead = (messageId) => {
  return request.post(`/consultation/message/read/${messageId}`)
}

// 取消预约
const cancelAppointment = (data) => {
  return request.post('/consultation/appointment/cancel', data)
}

// 提交反馈
const submitFeedback = (consultationId, rating, feedback) => {
  return request.post(`/consultation/${consultationId}/feedback`, {
    userRating: rating,
    userFeedback: feedback
  })
}

module.exports = {
  getPsychologists,
  getPsychologistDetail,
  bookConsultation,
  createAppointment,
  getUserConsultations,
  getConsultationDetail,
  sendMessage,
  getMessages,
  markMessageAsRead,
  cancelAppointment,
  submitFeedback
}

