// pages/ai/ai.js
const aiApi = require('../../api/ai')
const util = require('../../utils/util')
const storage = require('../../utils/storage')

Page({
  data: {
    messages: [],
    inputMessage: '',
    isTyping: false,
    scrollToView: '',
    userInfo: {}
  },

  onLoad(options) {
    this.loadUserInfo()
    this.loadChatHistory()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    this.setData({ userInfo })
  },

  // 加载聊天历史
  loadChatHistory() {
    const { userInfo } = this.data
    if (!userInfo || !userInfo.id) return

    aiApi.getChatHistory({
      userId: userInfo.id,
      sessionType: 'therapy'
    }).then(res => {
      if (res.data && res.data.length > 0) {
        const messages = res.data.map(item => ({
          role: item.userMessage ? 'user' : 'assistant',
          content: item.userMessage || item.aiResponse,
          time: util.relativeTime(item.createdAt)
        }))
        this.setData({ messages })
        this.scrollToBottom()
      }
    }).catch(err => {
      console.error('加载历史记录失败', err)
    })
  },

  // 输入消息
  onInput(e) {
    this.setData({
      inputMessage: e.detail.value
    })
  },

  // 发送消息
  sendMessage() {
    const { inputMessage, userInfo } = this.data

    if (!inputMessage.trim()) {
      util.showError('请输入消息')
      return
    }

    if (!userInfo || !userInfo.id) {
      util.showError('请先登录')
      return
    }

    // 添加用户消息
    const userMsg = {
      role: 'user',
      content: inputMessage,
      time: util.formatTime(new Date())
    }

    this.setData({
      messages: [...this.data.messages, userMsg],
      inputMessage: '',
      isTyping: true
    })

    this.scrollToBottom()

    // 调用AI接口
    aiApi.therapyChat({
      userId: userInfo.id,
      message: inputMessage
    }).then(res => {
      const aiMsg = {
        role: 'assistant',
        content: res.data.response || res.data.message || '抱歉，我现在有点忙，请稍后再试。',
        time: util.formatTime(new Date())
      }

      this.setData({
        messages: [...this.data.messages, aiMsg],
        isTyping: false
      })

      this.scrollToBottom()
    }).catch(err => {
      console.error('AI回复失败', err)
      
      // 添加错误提示消息
      const errorMsg = {
        role: 'assistant',
        content: '抱歉，我遇到了一些问题。请稍后再试。',
        time: util.formatTime(new Date())
      }

      this.setData({
        messages: [...this.data.messages, errorMsg],
        isTyping: false
      })

      this.scrollToBottom()
    })
  },

  // 发送快捷消息
  sendQuickMessage(e) {
    const msg = e.currentTarget.dataset.msg
    this.setData({
      inputMessage: msg
    }, () => {
      this.sendMessage()
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
  },

  // 清空聊天记录
  clearHistory() {
    const { userInfo } = this.data
    
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }
    
    wx.showModal({
      title: '提示',
      content: '确定要清空所有聊天记录吗？',
      success: (res) => {
        if (res.confirm) {
          // 调用后端API清空数据库记录
          aiApi.clearHistory(userInfo.id).then(() => {
            // 清空本地显示的消息
            this.setData({
              messages: []
            })
            wx.showToast({
              title: '已清空',
              icon: 'success'
            })
          }).catch(err => {
            console.error('清空历史记录失败', err)
            wx.showToast({
              title: '清空失败',
              icon: 'error'
            })
          })
        }
      }
    })
  },

  // 显示使用提示
  showTips() {
    wx.showModal({
      title: '使用提示',
      content: 'AI心理助手可以帮助您：\n\n1. 倾听您的心声和烦恼\n2. 提供情绪支持和建议\n3. 引导您进行自我反思\n4. 推荐适合的心理练习\n\n注意：AI助手不能替代专业心理咨询，如有严重心理问题，请寻求专业帮助。',
      showCancel: false,
      confirmText: '知道了'
    })
  }
})

