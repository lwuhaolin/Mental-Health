// app.js
App({
  onLaunch() {
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 检查登录状态
    this.checkLoginStatus()
  },

  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      this.globalData.isLogin = true
      this.globalData.userInfo = userInfo
    } else {
      this.globalData.isLogin = false
    }
  },

  globalData: {
    isLogin: false,
    userInfo: null,
    baseUrl: 'http://localhost:8080' // 后端地址（不需要/api后缀）
  }
})

