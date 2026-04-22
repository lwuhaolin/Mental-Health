import request from '@/utils/request'

// 用户登录
export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 心理专家登录
export const psychologistLogin = (data) => {
  return request({
    url: '/psychologist/login',
    method: 'post',
    data
  })
}

// 管理员登录
export const adminLogin = (data) => {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 更新用户信息
export const updateUser = (data) => {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 获取用户信息
export const getUserInfo = (id) => {
  return request({
    url: `/user/info/${id}`,
    method: 'get'
  })
}

// 忘记密码 - 重置密码
export const forgotPassword = (data) => {
  return request({
    url: '/user/forgot-password',
    method: 'post',
    data
  })
}

// 心理专家注册
export const registerPsychologist = (data) => {
  return request({
    url: '/psychologist/register',
    method: 'post',
    data
  })
}

// 心理专家忘记密码
export const forgotPasswordPsychologist = (data) => {
  return request({
    url: '/psychologist/forgot-password',
    method: 'post',
    data
  })
}

export default {
  login,
  psychologistLogin,
  adminLogin,
  register,
  updateUser,
  getUserInfo,
  forgotPassword,
  registerPsychologist,
  forgotPasswordPsychologist
}