// pages/index/index.js
const util = require('../../utils/util')
const storage = require('../../utils/storage')

Page({
  data: {
    userInfo: {},
    dailyQuote: '心若向阳，无畏悲伤。',
    quotes: [
      '心若向阳，无畏悲伤。',
      '接纳自己，是改变的开始。',
      '每一次呼吸，都是重新开始的机会。',
      '你比想象中更坚强。',
      '温柔地对待自己，就像对待好朋友一样。',
      '情绪无好坏，接纳它，理解它。',
      '成长的过程，就是不断认识自己的过程。',
      '勇敢地表达感受，是自我关怀的一部分。'
    ]
  },

  onLoad(options) {
    this.checkLogin()
    this.loadUserInfo()
    this.setDailyQuote()
  },

  onShow() {
    // 每次显示页面时刷新用户信息
    this.loadUserInfo()
  },

  // 检查登录状态
  checkLogin() {
    if (!util.checkLogin()) {
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    if (userInfo) {
      this.setData({
        userInfo
      })
    }
  },

  // 设置每日一句
  setDailyQuote() {
    const { quotes } = this.data
    const today = new Date().getDate()
    const index = today % quotes.length
    this.setData({
      dailyQuote: quotes[index]
    })
  },

  // 页面跳转
  navigateTo(e) {
    const url = e.currentTarget.dataset.url
    // 底部导航页面使用 switchTab (首页、AI疗愈、漂流瓶、我的)
    if (url.includes('bottle') || url.includes('/ai/ai') || url.includes('profile')) {
      wx.switchTab({ url })
    } else {
      wx.navigateTo({ url })
    }
  },

  // 即将推出
  showComingSoon() {
    wx.showToast({
      title: '功能即将推出',
      icon: 'none'
    })
  }
})

