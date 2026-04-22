// pages/encyclopedia/encyclopedia.js
const util = require('../../utils/util')

Page({
  data: {
    searchKeyword: '',
    categories: [
      {
        id: 1,
        name: '心理障碍',
        icon: '🧠',
        color: '#667eea',
        count: 12
      },
      {
        id: 2,
        name: '情绪认知',
        icon: '💭',
        color: '#f093fb',
        count: 15
      },
      {
        id: 3,
        name: '心理疗法',
        icon: '🩺',
        color: '#4facfe',
        count: 10
      },
      {
        id: 4,
        name: '心理测评',
        icon: '📊',
        color: '#43e97b',
        count: 8
      },
      {
        id: 5,
        name: '发展心理',
        icon: '👶',
        color: '#fa709a',
        count: 11
      },
      {
        id: 6,
        name: '社会心理',
        icon: '👥',
        color: '#fee140',
        count: 9
      }
    ],
    hotTopics: [
      { id: 1, name: '抑郁症', views: 5632 },
      { id: 2, name: '焦虑症', views: 4821 },
      { id: 3, name: '认知行为疗法', views: 3945 },
      { id: 4, name: '正念冥想', views: 3234 },
      { id: 5, name: '情绪管理', views: 2987 },
      { id: 6, name: '睡眠障碍', views: 2756 },
      { id: 7, name: '强迫症', views: 2543 },
      { id: 8, name: '社交恐惧', views: 2198 }
    ],
    articles: [
      {
        id: 1,
        title: '什么是抑郁症？',
        summary: '抑郁症是一种常见的心理障碍，表现为持续的情绪低落、兴趣丧失...',
        category: '心理障碍',
        updateTime: '2024-10-20',
        views: 5632,
        likes: 234
      },
      {
        id: 2,
        title: '认知行为疗法(CBT)详解',
        summary: 'CBT是一种有效的心理治疗方法，通过改变思维模式来改善情绪和行为...',
        category: '心理疗法',
        updateTime: '2024-10-18',
        views: 3945,
        likes: 189
      },
      {
        id: 3,
        title: '焦虑的成因与应对',
        summary: '了解焦虑产生的原因，学习科学有效的应对方法...',
        category: '心理障碍',
        updateTime: '2024-10-15',
        views: 4821,
        likes: 203
      },
      {
        id: 4,
        title: '情绪ABC理论',
        summary: 'ABC理论揭示了事件、信念和结果之间的关系，帮助我们理解情绪...',
        category: '情绪认知',
        updateTime: '2024-10-12',
        views: 2876,
        likes: 145
      },
      {
        id: 5,
        title: '青少年心理发展特点',
        summary: '了解青少年时期的心理发展规律和特点，促进健康成长...',
        category: '发展心理',
        updateTime: '2024-10-10',
        views: 3124,
        likes: 167
      }
    ]
  },

  onLoad(options) {
    // 页面加载
  },

  // 搜索
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },

  onSearch() {
    const { searchKeyword } = this.data
    if (!searchKeyword.trim()) {
      wx.showToast({
        title: '请输入搜索关键词',
        icon: 'none'
      })
      return
    }

    wx.showToast({
      title: '搜索功能开发中',
      icon: 'none'
    })
  },

  // 查看分类
  viewCategory(e) {
    const { id, name } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/encyclopedia/category?id=${id}&name=${name}`
    })
  },

  // 查看词条详情
  viewArticle(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/encyclopedia/detail?id=${id}`
    })
  },

  // 查看热门话题
  viewTopic(e) {
    const { id, name } = e.currentTarget.dataset
    wx.showToast({
      title: `正在查看：${name}`,
      icon: 'none'
    })
  },

  // 查看全部文章
  viewAllArticles() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  }
})

