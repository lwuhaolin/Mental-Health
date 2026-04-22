// 认证相关工具函数

// 保存token
export const setToken = (token) => {
  localStorage.setItem('xingyu_token', token)
}

// 获取token
export const getToken = () => {
  return localStorage.getItem('xingyu_token')
}

// 移除token
export const removeToken = () => {
  localStorage.removeItem('xingyu_token')
}

// 保存用户信息
export const setUserInfo = (userInfo) => {
  localStorage.setItem('xingyu_user', JSON.stringify(userInfo))
}

// 获取用户信息
export const getUserInfo = () => {
  const userStr = localStorage.getItem('xingyu_user')
  return userStr ? JSON.parse(userStr) : null
}

// 移除用户信息
export const removeUserInfo = () => {
  localStorage.removeItem('xingyu_user')
}

// 检查是否登录
export const isLoggedIn = () => {
  return !!getToken()
}

// 退出登录
export const logout = () => {
  removeToken()
  removeUserInfo()
}