// pages/consultation/experts/experts.js
const consultationApi = require('../../../api/consultation')
const util = require('../../../utils/util')

Page({
  data: {
    experts: [],
    allExperts: [],
    keyword: ''
  },

  onLoad(options) {
    this.loadExperts()
  },

  // 加载专家列表
  loadExperts() {
    util.showLoading('加载中...')

    consultationApi.getPsychologists().then(res => {
      util.hideLoading()
      
      // 后端返回格式：{success: true, psychologists: [...]}
      if (res.success && res.psychologists) {
        const experts = res.psychologists.map(item => ({
          id: item.id,
          name: item.realName || item.name || '匿名专家', // 后端用realName
          title: item.title || '心理咨询师',
          rating: item.rating || 5.0,
          consultationCount: item.consultationCount || 0,
          verified: item.status === '正常' || item.status === '1',
          specialties: item.specialty ? item.specialty.split(',') : ['心理咨询'], // 后端用specialty(单数)
          introduction: item.introduction || '专业心理咨询师',
          price: item.hourlyRate || 200, // 后端用hourlyRate
          avatar: item.avatar || ''
        }))
        
        this.setData({
          experts,
          allExperts: experts
        })
      } else {
        // 如果后端没有数据，使用模拟数据
        this.setMockExperts()
      }
    }).catch(err => {
      util.hideLoading()
      console.error('加载专家列表失败', err)
      
      // 使用模拟数据
      this.setMockExperts()
    })
  },

  // 模拟数据
  setMockExperts() {
    const mockExperts = [
      {
        id: 1,
        name: '张医生',
        title: '国家二级心理咨询师',
        rating: 4.9,
        consultationCount: 328,
        verified: true,
        specialties: ['情绪管理', '焦虑症', '抑郁症'],
        introduction: '从事心理咨询工作10年，擅长认知行为疗法，帮助来访者改善情绪问题',
        price: 300,
        avatar: ''
      },
      {
        id: 2,
        name: '李咨询师',
        title: '资深心理咨询师',
        rating: 4.8,
        consultationCount: 256,
        verified: true,
        specialties: ['压力管理', '职业困惑', '人际关系'],
        introduction: '擅长短程焦点解决治疗，帮助来访者快速识别问题并找到解决方案',
        price: 250,
        avatar: ''
      },
      {
        id: 3,
        name: '王老师',
        title: '心理咨询师',
        rating: 4.7,
        consultationCount: 189,
        verified: true,
        specialties: ['婚姻家庭', '亲子关系', '情感问题'],
        introduction: '专注于家庭治疗和婚姻咨询，帮助改善家庭关系',
        price: 200,
        avatar: ''
      }
    ]

    this.setData({
      experts: mockExperts,
      allExperts: mockExperts
    })
  },

  // 搜索专家
  onSearch(e) {
    const keyword = e.detail.value.trim()
    this.setData({ keyword })

    if (!keyword) {
      this.setData({
        experts: this.data.allExperts
      })
      return
    }

    const experts = this.data.allExperts.filter(expert => {
      const matchName = expert.name && expert.name.includes(keyword)
      const matchTitle = expert.title && expert.title.includes(keyword)
      const matchSpecialties = expert.specialties && expert.specialties.some(s => s.includes(keyword))
      const matchIntro = expert.introduction && expert.introduction.includes(keyword)
      
      return matchName || matchTitle || matchSpecialties || matchIntro
    })

    this.setData({ experts })
  },

  // 查看专家详情
  viewExpert(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/consultation/detail/detail?expertId=${id}`
    })
  }
})

