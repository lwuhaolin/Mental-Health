// pages/consultation/detail/detail.js
const consultationApi = require('../../../api/consultation')
const util = require('../../../utils/util')
const storage = require('../../../utils/storage')

Page({
  data: {
    expertId: 0,
    consultationId: 0,
    expert: {},
    consultation: {},
    messages: [],
    messageInput: '',
    scrollToView: '',
    
    // 预约表单
    consultationTypes: ['在线咨询', '电话咨询', '面对面咨询'],
    typeIndex: 0,
    bookingDate: '',
    today: '',
    timeSlots: ['09:00-10:00', '10:00-11:00', '14:00-15:00', '15:00-16:00', '16:00-17:00'],
    timeIndex: 0,
    durations: ['30分钟', '60分钟', '90分钟'],
    durationIndex: 1,
    description: '',
    totalPrice: 0,
    
    userInfo: {}
  },

  onLoad(options) {
    const { expertId, id } = options
    
    // 设置今天的日期
    const today = util.formatDate(new Date())
    this.setData({ 
      expertId: expertId || 0,
      consultationId: id || 0,
      today,
      bookingDate: today
    })

    this.loadUserInfo()
    
    if (id) {
      // 查看咨询详情
      this.loadConsultationDetail()
      this.loadMessages()
    } else if (expertId) {
      // 预约咨询
      this.loadExpertInfo()
    }
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    this.setData({ userInfo })
  },

  // 加载专家信息
  loadExpertInfo() {
    const { expertId } = this.data

    consultationApi.getPsychologistDetail(expertId).then(res => {
      // 后端返回格式：{success: true, psychologist: {...}}
      if (res.success && res.psychologist) {
        const psychologist = res.psychologist
        const expert = {
          id: psychologist.id,
          name: psychologist.realName || psychologist.name || '专家',
          title: psychologist.title || '心理咨询师',
          rating: psychologist.rating || 5.0,
          introduction: psychologist.introduction || '专业心理咨询师',
          specialties: psychologist.specialty ? psychologist.specialty.split(',') : ['心理咨询'],
          price: psychologist.hourlyRate || 200,
          avatar: psychologist.avatar || ''
        }
        
        this.setData({ expert })
        this.calculatePrice()
      } else {
        // 使用模拟数据
        this.setData({
          expert: {
            id: expertId,
            name: '张医生',
            title: '国家二级心理咨询师',
            rating: 4.9,
            introduction: '从事心理咨询工作10年，擅长认知行为疗法',
            specialties: ['情绪管理', '焦虑症', '抑郁症'],
            price: 300
          }
        })
        this.calculatePrice()
      }
    }).catch(err => {
      console.error('加载专家信息失败', err)
      
      // 模拟数据
      this.setData({
        expert: {
          id: expertId,
          name: '张医生',
          title: '国家二级心理咨询师',
          rating: 4.9,
          introduction: '从事心理咨询工作10年，擅长认知行为疗法',
          specialties: ['情绪管理', '焦虑症', '抑郁症'],
          price: 300
        }
      })
      this.calculatePrice()
    })
  },

  // 加载咨询详情
  loadConsultationDetail() {
    const { consultationId } = this.data

    consultationApi.getConsultationDetail(consultationId).then(res => {
      if (res.data) {
        const consultation = {
          ...res.data,
          statusText: this.getStatusText(res.data.status),
          appointmentTime: util.formatDateTime(res.data.appointmentTime)
        }
        
        this.setData({ 
          consultation,
          expert: {
            id: res.data.psychologistId,
            name: res.data.psychologistName,
            title: res.data.psychologistTitle,
            avatar: res.data.psychologistAvatar
          }
        })
      }
    }).catch(err => {
      console.error('加载咨询详情失败', err)
    })
  },

  // 加载消息列表
  loadMessages() {
    const { consultationId } = this.data

    consultationApi.getMessages(consultationId).then(res => {
      if (res.data) {
        const messages = res.data.map(item => ({
          ...item,
          time: util.relativeTime(item.createdAt)
        }))
        
        this.setData({ messages })
        this.scrollToBottom()
      }
    }).catch(err => {
      console.error('加载消息失败', err)
    })
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'pending': '待确认',
      'accepted': '已接单',
      'in_progress': '进行中',
      'completed': '已完成',
      'cancelled': '已取消'
    }
    return statusMap[status] || '未知'
  },

  // 表单输入处理
  onTypeChange(e) {
    this.setData({ typeIndex: e.detail.value })
  },

  onDateChange(e) {
    this.setData({ bookingDate: e.detail.value })
  },

  onTimeChange(e) {
    this.setData({ timeIndex: e.detail.value })
  },

  onDurationChange(e) {
    this.setData({ durationIndex: e.detail.value })
    this.calculatePrice()
  },

  onDescriptionInput(e) {
    this.setData({ description: e.detail.value })
  },

  // 计算价格
  calculatePrice() {
    const { expert, durationIndex } = this.data
    const basePrice = expert.price || 200
    const multiplier = durationIndex === 0 ? 0.5 : (durationIndex === 1 ? 1 : 1.5)
    
    this.setData({
      totalPrice: Math.round(basePrice * multiplier)
    })
  },

  // 提交预约
  submitBooking() {
    const { expertId, userInfo, consultationTypes, typeIndex, bookingDate, timeSlots, timeIndex, durationIndex, description, totalPrice, expert } = this.data

    if (!userInfo || !userInfo.id) {
      util.showError('请先登录')
      return
    }

    if (!bookingDate) {
      util.showError('请选择预约日期')
      return
    }

    if (!description || !description.trim()) {
      util.showError('请填写咨询问题描述')
      return
    }

    // 格式化预约时间
    const timeStart = timeSlots[timeIndex].split('-')[0]
    const appointmentTime = `${bookingDate} ${timeStart}:00`  // yyyy-MM-dd HH:mm:ss 格式
    const duration = [30, 60, 90][durationIndex]
    
    // 转换为 ISO 格式用于 consultation 表
    const dateObj = new Date(appointmentTime)
    const scheduledTimeISO = dateObj.toISOString()

    util.showLoading('提交中...')

    // 同时提交到两个表，参考网页端逻辑
    Promise.all([
      // 1. 保存到咨询记录表(consultations)
      consultationApi.bookConsultation({
        userId: userInfo.id,
        psychologistId: expertId,
        title: description.substring(0, 50) || '心理咨询',
        description: description,
        consultationType: consultationTypes[typeIndex],
        scheduledTime: scheduledTimeISO,  // ISO 格式
        cost: totalPrice,
        status: '待接单'
      }),
      
      // 2. 保存到预约表(consultation_appointments)
      consultationApi.createAppointment({
        userId: userInfo.id,
        psychologistId: expertId,
        description: description,
        title: description.substring(0, 50) || '心理咨询',
        appointmentTime: appointmentTime,  // yyyy-MM-dd HH:mm:ss 格式
        scheduledTime: appointmentTime,
        contactInfo: userInfo.phone || userInfo.email || '暂无',
        phone: userInfo.phone || '暂无'
      })
    ]).then(([consultationRes, appointmentRes]) => {
      util.hideLoading()
      
      console.log('咨询记录响应:', consultationRes)
      console.log('预约记录响应:', appointmentRes)
      
      // 检查两个请求是否都成功
      if (consultationRes.success && appointmentRes.success) {
        util.showSuccess('预约提交成功！专家将在24小时内联系您')
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        const errorMsg = consultationRes.message || appointmentRes.message || '预约提交失败，请稍后重试'
        util.showError(errorMsg)
      }
    }).catch(err => {
      util.hideLoading()
      console.error('预约失败详情:', err)
      
      // 显示错误信息
      let errorMsg = '预约失败'
      if (err && err.message) {
        errorMsg = err.message
      } else if (typeof err === 'string') {
        errorMsg = err
      }
      
      util.showError(errorMsg)
    })
  },

  // 发送消息
  onMessageInput(e) {
    this.setData({ messageInput: e.detail.value })
  },

  sendMessage() {
    const { consultationId, messageInput, userInfo } = this.data

    if (!messageInput.trim()) return

    consultationApi.sendMessage({
      consultationId,
      senderId: userInfo.id,
      senderType: 'user',
      content: messageInput
    }).then(res => {
      // 添加到消息列表
      const newMessage = {
        id: Date.now(),
        consultationId,
        senderId: userInfo.id,
        senderType: 'user',
        content: messageInput,
        time: '刚刚'
      }
      
      this.setData({
        messages: [...this.data.messages, newMessage],
        messageInput: ''
      })
      
      this.scrollToBottom()
    }).catch(err => {
      util.showError('发送失败')
      console.error('发送消息失败', err)
    })
  },

  // 滚动到底部
  scrollToBottom() {
    setTimeout(() => {
      const index = this.data.messages.length - 1
      this.setData({
        scrollToView: `msg-${index}`
      })
    }, 100)
  }
})

