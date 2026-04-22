import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  },
  transformRequest: [function (data) {
    // 对请求数据进行转换处理
    if (data instanceof FormData) {
      return data
    }
    return JSON.stringify(data)
  }],
  transformResponse: [function (data) {
    // 对响应数据进行转换处理
    if (typeof data === 'string') {
      try {
        data = JSON.parse(data)
      } catch (e) {
        // 忽略解析错误
      }
    }
    return data
  }]
})
// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    
    // 添加token到请求头
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果返回的code不是200，则判断为错误
    if (response.status !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res
  },
  (error) => {
    const { status } = error.response || {}
    
    switch (status) {
      case 401:
        ElMessage.error('登录已过期，请重新登录')
        // 清除用户信息并跳转到登录页
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
        break
      case 403:
        ElMessage.error('没有权限访问')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error('网络错误，请稍后重试')
    }
    
    return Promise.reject(error)
  }
)

export default service