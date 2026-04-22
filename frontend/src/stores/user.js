import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import userApi from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref('')

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role || 'user')

  // 登录
  const login = async (loginData) => {
    try {
      let response
      // 根据角色调用不同的登录接口
      if (loginData.role === 'admin') {
        response = await userApi.adminLogin(loginData)
        if (response.success) {
          userInfo.value = { ...response.admin, role: 'admin' }
        }
      } else if (loginData.role === 'psychologist') {
        response = await userApi.psychologistLogin(loginData)
        if (response.success) {
          userInfo.value = { ...response.psychologist, role: 'psychologist' }
        }
      } else {
        response = await userApi.login(loginData)
        if (response.success) {
          userInfo.value = { ...response.user, role: 'user' }
        }
      }
      
      if (response && response.success) {
        token.value = `${loginData.role}_token_` + Date.now() // 模拟token
        // 保存到本地存储
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        localStorage.setItem('token', token.value)
        localStorage.setItem('userRole', loginData.role)
        return { success: true, message: '登录成功' }
      } else {
        return { success: false, message: response?.message || '登录失败' }
      }
    } catch (error) {
      return { success: false, message: '登录失败，请稍后重试' }
    }
  }

  // 注册
  const register = async (userData) => {
    try {
      const response = await userApi.register(userData)
      if (response.success) {
        return { success: true, message: '注册成功' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: '注册失败，请稍后重试' }
    }
  }

  // 退出登录
  const logout = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  // 检查登录状态
  const checkLoginStatus = () => {
    const savedUserInfo = localStorage.getItem('userInfo')
    const savedToken = localStorage.getItem('token')
    const savedRole = localStorage.getItem('userRole')
    
    if (savedUserInfo && savedToken && savedRole) {
      userInfo.value = JSON.parse(savedUserInfo)
      token.value = savedToken
      // 确保角色信息正确
      if (!userInfo.value.role) {
        userInfo.value.role = savedRole
      }
    }
  }

  // 更新用户信息
  const updateUserInfo = async (userData) => {
    try {
      const response = await userApi.updateUser(userData)
      if (response.success) {
        userInfo.value = { ...userInfo.value, ...userData }
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        return { success: true, message: '更新成功' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: '更新失败，请稍后重试' }
    }
  }

  // 忘记密码 - 重置密码
  const forgotPassword = async (passwordData) => {
    try {
      const response = await userApi.forgotPassword(passwordData)
      if (response.success) {
        return { success: true, message: '密码重置成功' }
      } else {
        return { success: false, message: response.message || '密码重置失败' }
      }
    } catch (error) {
      return { success: false, message: '密码重置失败，请稍后重试' }
    }
  }

  // 心理专家注册
  const registerPsychologist = async (psychologistData) => {
    try {
      const response = await userApi.registerPsychologist(psychologistData)
      if (response.success) {
        return { success: true, message: '心理专家注册成功，请等待管理员审核' }
      } else {
        return { success: false, message: response.message || '专家注册失败' }
      }
    } catch (error) {
      return { success: false, message: '注册失败，请稍后重试' }
    }
  }

  // 心理专家忘记密码
  const forgotPasswordPsychologist = async (passwordData) => {
    try {
      const response = await userApi.forgotPasswordPsychologist(passwordData)
      if (response.success) {
        return { success: true, message: '密码重置成功' }
      } else {
        return { success: false, message: response.message || '密码重置失败' }
      }
    } catch (error) {
      return { success: false, message: '密码重置失败，请稍后重试' }
    }
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    userRole,
    login,
    register,
    logout,
    checkLoginStatus,
    updateUserInfo,
    forgotPassword,
    registerPsychologist,
    forgotPasswordPsychologist
  }
})