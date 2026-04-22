import request from '@/utils/request'

// 获取专家列表
export const getPsychologists = (params) => {
  return request({
    url: '/psychologist/list',
    method: 'get',
    params
  })
}

// 预约咨询
export const bookConsultation = (data) => {
  return request({
    url: '/consultation/book',
    method: 'post',
    data
  })
}

// 创建咨询预约（保存到预约表）
export const createConsultationAppointment = (data) => {
  return request({
    url: '/consultation/appointment',
    method: 'post',
    data
  })
}

// 专家接单
export const acceptConsultation = (data) => {
  return request({
    url: '/consultation/accept',
    method: 'post',
    data
  })
}

// 获取用户咨询记录
export const getUserConsultations = (userId) => {
  return request({
    url: `/consultation/user/${userId}`,
    method: 'get'
  })
}

// 获取专家咨询记录
export const getPsychologistConsultations = (psychologistId) => {
  return request({
    url: `/consultation/psychologist/${psychologistId}`,
    method: 'get'
  })
}

// 完成咨询
export const completeConsultation = (data) => {
  return request({
    url: '/consultation/complete',
    method: 'post',
    data
  })
}

// 发送留言（需要检查是否有对应的控制器）
export const sendConsultationMessage = (data) => {
  return request({
    url: '/consultation/message/send',
    method: 'post',
    data
  })
}

// 获取留言列表（需要检查是否有对应的控制器）
export const getConsultationMessages = (consultationId) => {
  return request({
    url: `/consultation/message/list/${consultationId}`,
    method: 'get'
  })
}

// 标记留言为已读（需要检查是否有对应的控制器）
export const markMessageAsRead = (messageId) => {
  return request({
    url: `/consultation/message/read/${messageId}`,
    method: 'post'
  })
}

// 获取未读留言数量（需要检查是否有对应的控制器）
export const getUnreadMessageCount = (consultationId, userType) => {
  return request({
    url: '/consultation/message/unread/count',
    method: 'get',
    params: {
      consultationId,
      userType
    }
  })
}

// 获取专家的患者列表
export const getPsychologistPatients = (psychologistId) => {
  return request({
    url: `/consultation/psychologist/${psychologistId}/patients`,
    method: 'get'
  })
}

// 获取专家的咨询预约列表
export const getPsychologistAppointments = (psychologistId) => {
  return request({
    url: `/consultation/appointments/psychologist/${psychologistId}`,
    method: 'get'
  })
}

// 获取心理专家统计数据
export const getPsychologistStats = (psychologistId) => {
  return request({
    url: `/psychologist/stats/${psychologistId}`,
    method: 'get'
  })
}

// 获取咨询详情
export const getConsultationDetail = (consultationId) => {
  return request({
    url: `/consultation/detail/${consultationId}`,
    method: 'get'
  })
}

// 更新咨询状态
export const updateConsultationStatus = (consultationId, status) => {
  return request({
    url: '/consultation/update-status',
    method: 'post',
    params: {
      consultationId,
      status
    }
  })
}

// 专家接单（预约）
export const acceptAppointment = (data) => {
  return request({
    url: '/consultation/appointment/accept',
    method: 'post',
    data
  })
}

// 取消预约
export const cancelAppointment = (data) => {
  return request({
    url: '/consultation/appointment/cancel',
    method: 'post',
    data
  })
}

// 获取专家详情
export const getPsychologistDetail = (psychologistId) => {
  return request({
    url: `/psychologist/detail/${psychologistId}`,
    method: 'get'
  })
}

// 上传专家头像
export const uploadPsychologistAvatar = (formData) => {
  return request({
    url: '/psychologist/avatar/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 获取专家收入趋势数据
export const getPsychologistIncomeTrend = (psychologistId, range = 'month') => {
  return request({
    url: `/psychologist/stats/${psychologistId}/income-trend`,
    method: 'get',
    params: { range }
  })
}

// 获取专家咨询类型分布
export const getPsychologistConsultationTypes = (psychologistId) => {
  return request({
    url: `/psychologist/stats/${psychologistId}/consultation-types`,
    method: 'get'
  })
}

// 获取专家咨询时长统计
export const getPsychologistDurationStats = (psychologistId) => {
  return request({
    url: `/psychologist/stats/${psychologistId}/duration-stats`,
    method: 'get'
  })
}

// 获取专家评分分布
export const getPsychologistRatingDistribution = (psychologistId) => {
  return request({
    url: `/psychologist/stats/${psychologistId}/rating-distribution`,
    method: 'get'
  })
}

// 获取专家详细统计报表
export const getPsychologistDetailedStats = (psychologistId, params) => {
  return request({
    url: `/psychologist/stats/${psychologistId}/detailed`,
    method: 'get',
    params
  })
}

// 提交咨询反馈
export const submitConsultationFeedback = (consultationId, rating, feedback) => {
  return request({
    url: `/consultation/${consultationId}/feedback`,
    method: 'post',
    data: {
      userRating: rating,
      userFeedback: feedback
    }
  })
}