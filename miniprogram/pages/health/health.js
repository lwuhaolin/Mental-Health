// pages/health/health.js
const util = require('../../utils/util')

Page({
  data: {
    articles: [],
    categories: ['全部', '情绪管理', '压力应对', '睡眠健康', '人际关系', '自我成长'],
    activeCategory: 0
  },

  onLoad(options) {
    this.loadArticles()
  },

  // 切换分类
  switchCategory(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ 
      activeCategory: index 
    })
    this.loadArticles()
  },

  // 加载文章列表
  loadArticles() {
    const { activeCategory, categories } = this.data
    const category = categories[activeCategory]
    
    // 虚拟数据
    const allArticles = [
      {
        id: 1,
        title: '如何识别和管理焦虑情绪',
        summary: '焦虑是一种常见的情绪反应，学会识别和管理焦虑情绪对心理健康至关重要...',
        category: '情绪管理',
        author: '张医生',
        readCount: 1256,
        likes: 89,
        cover: '',
        publishTime: '2024-10-20'
      },
      {
        id: 2,
        title: '职场压力应对的5个实用技巧',
        summary: '现代职场压力越来越大，掌握正确的应对方法可以帮助我们更好地平衡工作与生活...',
        category: '压力应对',
        author: '李咨询师',
        readCount: 2134,
        likes: 156,
        cover: '',
        publishTime: '2024-10-18'
      },
      {
        id: 3,
        title: '改善睡眠质量的7个科学方法',
        summary: '良好的睡眠是心理健康的基础，本文将介绍科学有效的睡眠改善方法...',
        category: '睡眠健康',
        author: '王老师',
        readCount: 3421,
        likes: 234,
        cover: '',
        publishTime: '2024-10-15'
      },
      {
        id: 4,
        title: '建立健康人际关系的关键要素',
        summary: '人际关系对我们的心理健康有重要影响，学会建立和维护健康的人际关系很重要...',
        category: '人际关系',
        author: '刘咨询师',
        readCount: 1867,
        likes: 123,
        cover: '',
        publishTime: '2024-10-12'
      },
      {
        id: 5,
        title: '自我接纳：心理成长的第一步',
        summary: '接纳自己是心理成长的起点，本文将探讨如何培养自我接纳的能力...',
        category: '自我成长',
        author: '赵医生',
        readCount: 2567,
        likes: 198,
        cover: '',
        publishTime: '2024-10-10'
      },
      {
        id: 6,
        title: '情绪日记：记录和理解自己的情绪',
        summary: '通过写情绪日记，我们可以更好地理解自己的情绪模式，提高情绪觉察能力...',
        category: '情绪管理',
        author: '张医生',
        readCount: 1432,
        likes: 87,
        cover: '',
        publishTime: '2024-10-08'
      },
      {
        id: 7,
        title: '正念冥想：缓解压力的有效方法',
        summary: '正念冥想是一种科学验证的压力缓解技术，每天练习可以显著改善心理状态...',
        category: '压力应对',
        author: '李咨询师',
        readCount: 2890,
        likes: 212,
        cover: '',
        publishTime: '2024-10-05'
      },
      {
        id: 8,
        title: '培养积极心态的实用策略',
        summary: '积极心态是心理健康的重要标志，本文分享培养积极心态的具体方法...',
        category: '自我成长',
        author: '王老师',
        readCount: 1678,
        likes: 145,
        cover: '',
        publishTime: '2024-10-02'
      }
    ]

    // 根据分类筛选
    let articles = allArticles
    if (category !== '全部') {
      articles = allArticles.filter(item => item.category === category)
    }

    this.setData({ articles })
  },

  // 查看文章详情
  viewArticle(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/health/detail?id=${id}`
    })
  },

  // 搜索
  onSearch() {
    wx.showToast({
      title: '搜索功能开发中',
      icon: 'none'
    })
  }
})

