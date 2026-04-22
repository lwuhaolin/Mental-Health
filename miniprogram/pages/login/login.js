// pages/login/login.js
const userApi = require('../../api/user')
const storage = require('../../utils/storage')
const util = require('../../utils/util')
const avatarUtil = require('../../utils/avatar')

Page({
  data: {
    activeTab: 'login',
    loginForm: {
      username: '',
      password: ''
    },
    registerForm: {
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      phone: ''
    }
  },

  onLoad(options) {
    // 检查是否已登录
    if (storage.getToken()) {
      wx.switchTab({
        url: '/pages/index/index'
      })
    }
  },

  // 切换Tab
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab
    })
  },

  // 登录表单输入
  onLoginInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({
      [`loginForm.${field}`]: e.detail.value
    })
  },

  // 注册表单输入
  onRegisterInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({
      [`registerForm.${field}`]: e.detail.value
    })
  },

  // 处理登录
  handleLogin() {
    const { username, password } = this.data.loginForm

    // 表单验证
    if (!username || !password) {
      util.showError('请填写完整信息')
      return
    }

    util.showLoading('登录中...')

    userApi.login({
      username,
      password
    }).then(res => {
      util.hideLoading()
      
      // 保存登录信息（后端返回格式：{success: true, message: "登录成功", user: {...}}）
      if (res.success && res.user) {
        // 生成一个简单的token（如果后端没有返回token）
        const token = res.user.id + '_' + Date.now()
        storage.setToken(token)
        
        // 如果用户没有头像，使用虚拟头像
        const userInfo = res.user
        if (!userInfo.avatar || userInfo.avatar.trim() === '') {
          const virtualAvatar = avatarUtil.generateVirtualAvatar(userInfo.username)
          userInfo.avatar = `https://ui-avatars.com/api/?name=${encodeURIComponent(virtualAvatar.text)}&background=${virtualAvatar.color.substring(1)}&color=fff&size=200`
        }
        
        storage.setUserInfo(userInfo)
        
        util.showSuccess('登录成功')
        
        // 更新全局数据
        const app = getApp()
        app.globalData.isLogin = true
        app.globalData.userInfo = userInfo
        
        // 跳转到首页
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      } else {
        util.showError(res.message || '登录失败，请重试')
      }
    }).catch(err => {
      util.hideLoading()
      util.showError(err.message || '登录失败')
    })
  },

  // 处理注册
  handleRegister() {
    const { username, password, confirmPassword, email, phone } = this.data.registerForm

    // 表单验证
    if (!username || !password || !confirmPassword) {
      util.showError('请填写必填信息')
      return
    }

    if (username.length < 3 || username.length > 20) {
      util.showError('用户名长度应为3-20个字符')
      return
    }

    if (password.length < 6) {
      util.showError('密码长度至少为6位')
      return
    }

    if (password !== confirmPassword) {
      util.showError('两次输入的密码不一致')
      return
    }

    // 邮箱验证（可选）
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      util.showError('请输入正确的邮箱格式')
      return
    }

    // 手机号验证（可选）
    if (phone && !/^1[3-9]\d{9}$/.test(phone)) {
      util.showError('请输入正确的手机号')
      return
    }

    util.showLoading('注册中...')

    userApi.register({
      username,
      password,
      email: email || undefined,
      phone: phone || undefined
    }).then(res => {
      util.hideLoading()
      
      if (res.success) {
        util.showSuccess('注册成功，请登录')
        
        // 切换到登录页面
        setTimeout(() => {
          this.setData({
            activeTab: 'login',
            loginForm: {
              username,
              password
            }
          })
        }, 1500)
      } else {
        util.showError(res.message || '注册失败')
      }
    }).catch(err => {
      util.hideLoading()
      util.showError(err.message || '注册失败')
    })
  },

  // 忘记密码
  forgotPassword() {
    wx.showModal({
      title: '提示',
      content: '请联系管理员重置密码',
      showCancel: false
    })
  }
})

