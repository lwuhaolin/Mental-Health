import request from '@/utils/request'

// 获取系统统计数据
export const getSystemStats = () => {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

// 获取用户管理数据
export const getUserManagementData = () => {
  return request({
    url: '/admin/user-management',
    method: 'get'
  })
}

// 获取专家管理数据
export const getPsychologistManagementData = () => {
  return request({
    url: '/admin/psychologist-management',
    method: 'get'
  })
}

// 用户CRUD
export const createUser = (data) => {
  return request({
    url: '/admin/user',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/admin/user',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

// 专家CRUD
export const createPsychologist = (data) => {
  return request({
    url: '/admin/psychologist',
    method: 'post',
    data
  })
}

export const updatePsychologist = (data) => {
  return request({
    url: '/admin/psychologist',
    method: 'put',
    data
  })
}

export const deletePsychologist = (id) => {
  return request({
    url: `/admin/psychologist/${id}`,
    method: 'delete'
  })
}

// 测评管理CRUD
export const getAssessmentManagementData = () => {
  return request({
    url: '/admin/assessment-management',
    method: 'get'
  })
}

export const createAssessment = (data) => {
  return request({
    url: '/admin/assessment',
    method: 'post',
    data
  })
}

export const updateAssessment = (data) => {
  return request({
    url: '/admin/assessment',
    method: 'put',
    data
  })
}

export const deleteAssessment = (id) => {
  return request({
    url: `/admin/assessment/${id}`,
    method: 'delete'
  })
}

// 咨询记录管理
export const getConsultationManagementData = () => {
  return request({
    url: '/admin/consultation-management',
    method: 'get'
  })
}

export const deleteConsultation = (id) => {
  return request({
    url: `/admin/consultation/${id}`,
    method: 'delete'
  })
}

// 漂流瓶管理
export const getBottleManagementData = () => {
  return request({
    url: '/admin/bottle-management',
    method: 'get'
  })
}

export const deleteBottle = (id) => {
  return request({
    url: `/admin/bottle/${id}`,
    method: 'delete'
  })
}

// AI对话记录管理
export const getAIManagementData = () => {
  return request({
    url: '/admin/ai-management',
    method: 'get'
  })
}

export const deleteAIRecord = (id) => {
  return request({
    url: `/admin/ai/${id}`,
    method: 'delete'
  })
}

export default {
  getSystemStats,
  getUserManagementData,
  getPsychologistManagementData,
  createUser,
  updateUser,
  deleteUser,
  createPsychologist,
  updatePsychologist,
  deletePsychologist,
  getAssessmentManagementData,
  createAssessment,
  updateAssessment,
  deleteAssessment,
  getConsultationManagementData,
  deleteConsultation,
  getBottleManagementData,
  deleteBottle,
  getAIManagementData,
  deleteAIRecord
}