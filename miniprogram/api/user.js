// 用户相关API
const request = require('../utils/request')

// 用户登录
const login = (data) => {
  return request.post('/user/login', data)
}

// 用户注册
const register = (data) => {
  return request.post('/user/register', data)
}

// 获取用户信息
const getUserInfo = (id) => {
  return request.get(`/user/info/${id}`)
}

// 更新用户信息
const updateUser = (data) => {
  return request.put('/user/update', data)
}

// 忘记密码
const forgotPassword = (data) => {
  return request.post('/user/forgot-password', data)
}

module.exports = {
  login,
  register,
  getUserInfo,
  updateUser,
  forgotPassword
}

