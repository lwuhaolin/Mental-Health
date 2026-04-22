// pages/consultation/list/list.js
const consultationApi = require('../../../api/consultation')
const util = require('../../../utils/util')
const storage = require('../../../utils/storage')

Page({
  data: {
    consultations: [],
    allConsultations: [],
    statusFilter: 'all',
    userInfo: {}
  },

  onLoad(options) {
    this.loadUserInfo()
    this.loadConsultations()
  },

  onShow() {
    // 每次显示时刷新列表
    this.loadConsultations()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    this.setData({ userInfo })
  },

  // 加载咨询列表
  loadConsultations() {
    const { userInfo } = this.data
    if (!userInfo || !userInfo.id) return

    util.showLoading('加载中...')

    consultationApi.getUserConsultations(userInfo.id).then(res => {
      util.hideLoading()
      
      console.log('咨询列表响应:', res)
      
      // 后端返回格式: {success: true, consultations: [...]}
      if (res.success && res.consultations) {
        const consultations = res.consultations.map(this.formatConsultation)
        this.setData({
          consultations,
          allConsultations: consultations
        })
      } else if (res.data) {
        // 兼容其他返回格式
        const consultations = res.data.map(this.formatConsultation)
        this.setData({
          consultations,
          allConsultations: consultations
        })
      } else {
        // 没有数据，显示空状态
        this.setData({
          consultations: [],
          allConsultations: []
        })
      }
    }).catch(err => {
      util.hideLoading()
      console.error('加载咨询列表失败', err)
      
      // 显示空状态
      this.setData({
        consultations: [],
        allConsultations: []
      })
    })
  },

  // 格式化咨询数据
  formatConsultation(item) {
    const statusMap = {
      '待接单': '待接单',
      '进行中': '进行中',
      '已完成': '已完成',
      '已取消': '已取消',
      'pending': '待确认',
      'accepted': '已接单',
      'in_progress': '进行中',
      'completed': '已完成',
      'cancelled': '已取消'
    }

    // 格式化时间
    let appointmentTime = ''
    if (item.scheduledTime) {
      appointmentTime = util.formatDateTime(item.scheduledTime)
    } else if (item.appointmentTime) {
      appointmentTime = util.formatDateTime(item.appointmentTime)
    }

    return {
      id: item.id,
      expertName: item.psychologistName || item.expertName || '专家',
      expertTitle: item.psychologistTitle || item.expertTitle || '心理咨询师',
      expertAvatar: item.psychologistAvatar || item.expertAvatar || '',
      status: item.status,
      statusText: statusMap[item.status] || item.status || '未知',
      consultationType: item.consultationType || '在线咨询',
      appointmentTime: appointmentTime || '待定',
      duration: item.duration || 60,
      price: item.cost || item.price || 0,
      title: item.title || '心理咨询',
      description: item.description || ''
    }
  },

  // 模拟数据
  setMockConsultations() {
    const mockData = [
      {
        id: 1,
        expertName: '张医生',
        expertTitle: '国家二级心理咨询师',
        expertAvatar: '',
        status: 'accepted',
        statusText: '已接单',
        consultationType: '在线咨询',
        appointmentTime: '2024-01-20 14:00',
        duration: 60,
        price: 300
      },
      {
        id: 2,
        expertName: '李咨询师',
        expertTitle: '资深心理咨询师',
        expertAvatar: '',
        status: 'completed',
        statusText: '已完成',
        consultationType: '在线咨询',
        appointmentTime: '2024-01-15 10:00',
        duration: 60,
        price: 250
      }
    ]

    this.setData({
      consultations: mockData,
      allConsultations: mockData
    })
  },

  // 状态筛选
  filterByStatus(e) {
    const status = e.currentTarget.dataset.status
    this.setData({ statusFilter: status })

    if (status === 'all') {
      this.setData({
        consultations: this.data.allConsultations
      })
    } else {
      const consultations = this.data.allConsultations.filter(item => item.status === status)
      this.setData({ consultations })
    }
  },

  // 去预约
  goToExperts() {
    wx.navigateTo({
      url: '/pages/consultation/experts/experts'
    })
  }
})

