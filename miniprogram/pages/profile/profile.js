// pages/profile/profile.js
const storage = require('../../utils/storage')
const util = require('../../utils/util')

Page({
  data: {
    userInfo: {},
    stats: {
      assessmentCount: 0,
      consultationCount: 0,
      daysCount: 0
    }
  },

  onLoad(options) {
    this.loadUserInfo()
    this.loadStats()
  },

  onShow() {
    // 每次显示时刷新用户信息
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    if (userInfo) {
      this.setData({ userInfo })
    } else {
      // 未登录，跳转到登录页
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }
  },

  // 加载统计数据
  loadStats() {
    // 这里可以调用API获取真实数据
    // 现在使用模拟数据
    const userInfo = storage.getUserInfo()
    if (!userInfo) return

    // 模拟数据
    this.setData({
      stats: {
        assessmentCount: Math.floor(Math.random() * 10) + 1,
        consultationCount: Math.floor(Math.random() * 5),
        daysCount: Math.floor(Math.random() * 30) + 1
      }
    })
  },

  // 页面跳转
  navigateTo(e) {
    const url = e.currentTarget.dataset.url
    if (url.includes('assessment') || url.includes('consultation')) {
      wx.navigateTo({ url })
    } else {
      wx.navigateTo({ url })
    }
  },

  // 编辑资料
  editProfile() {
    wx.navigateTo({
      url: '/pages/profile/edit'
    })
  },

  // 关于我们
  showAbout() {
    wx.showModal({
      title: '关于我们',
      content: '心理健康助手 v1.0.0\n\n致力于为用户提供专业的心理健康服务，包括AI疗愈、心理评估、专家咨询等功能。\n\n让我们一起关注心理健康，守护美好生活！',
      showCancel: false,
      confirmText: '知道了'
    })
  },

  // 帮助与反馈
  showHelp() {
    wx.showModal({
      title: '帮助与反馈',
      content: '如有任何问题或建议，欢迎通过以下方式联系我们：\n\n客服电话：400-xxx-xxxx\n客服邮箱：service@example.com\n\n工作时间：周一至周五 9:00-18:00',
      showCancel: false,
      confirmText: '知道了'
    })
  },

  // 即将推出
  showComingSoon() {
    wx.showToast({
      title: '功能即将推出',
      icon: 'none'
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除登录信息
          storage.clearAuth()
          
          // 更新全局状态
          const app = getApp()
          app.globalData.isLogin = false
          app.globalData.userInfo = null
          
          util.showSuccess('已退出登录')
          
          // 跳转到登录页
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            })
          }, 1500)
        }
      }
    })
  }
})

