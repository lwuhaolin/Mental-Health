// pages/bottle/bottle.js
const bottleApi = require('../../api/bottle')
const util = require('../../utils/util')
const storage = require('../../utils/storage')

Page({
  data: {
    bottles: [],
    loading: false,
    showDialog: false,
    showDetailDialog: false,
    publishContent: '',
    isAnonymous: true,
    selectedMood: 'happy',
    userInfo: {},
    currentBottle: null,
    currentBottleIndex: -1,
    replies: [],
    replyContent: '',
    replyAnonymous: false,
    moods: [
      { value: 'happy', label: '开心', emoji: '😊' },
      { value: 'sad', label: '难过', emoji: '😢' },
      { value: 'angry', label: '生气', emoji: '😠' },
      { value: 'anxious', label: '焦虑', emoji: '😰' },
      { value: 'calm', label: '平静', emoji: '😌' }
    ]
  },

  onLoad(options) {
    this.loadUserInfo()
    this.loadBottles()
  },

  onShow() {
    // 每次显示时刷新列表
    this.loadBottles()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    this.setData({ userInfo })
  },

  // 加载漂流瓶
  loadBottles() {
    this.setData({ loading: true })

    bottleApi.getBottles(10).then(res => {
      this.setData({ loading: false })
      
      if (res.success && res.bottles) {
        const bottles = res.bottles.map(item => ({
          id: item.id,
          userId: item.userId,
          username: item.username || '未知用户',
          content: item.content,
          mood: item.mood,
          isAnonymous: item.isAnonymous,
          likeCount: item.likeCount || 0,
          replyCount: item.replyCount || 0,
          time: util.relativeTime(item.createTime),
          isLiked: false
        }))
        
        this.setData({ bottles })
      }
    }).catch(err => {
      this.setData({ loading: false })
      console.error('加载漂流瓶失败', err)
      util.showError('加载失败')
    })
  },

  // 加载更多
  loadMore() {
    util.showLoading('加载中...')
    this.loadBottles()
    setTimeout(() => {
      util.hideLoading()
    }, 500)
  },

  // 显示发布弹窗
  showPublishDialog() {
    this.setData({
      showDialog: true,
      publishContent: '',
      isAnonymous: true,
      selectedMood: 'happy'
    })
  },

  // 隐藏发布弹窗
  hidePublishDialog() {
    this.setData({ showDialog: false })
  },

  // 输入内容
  onContentInput(e) {
    this.setData({ publishContent: e.detail.value })
  },

  // 匿名选项
  onAnonymousChange(e) {
    this.setData({
      isAnonymous: e.detail.value.includes('anonymous')
    })
  },

  // 选择心情
  selectMood(e) {
    this.setData({
      selectedMood: e.currentTarget.dataset.mood
    })
  },

  // 发布漂流瓶
  publishBottle() {
    const { publishContent, isAnonymous, selectedMood, userInfo } = this.data

    if (!publishContent.trim()) {
      util.showError('请输入内容')
      return
    }

    util.showLoading('发布中...')

    bottleApi.publishBottle({
      userId: userInfo.id,
      content: publishContent,
      isAnonymous,
      mood: selectedMood
    }).then(res => {
      util.hideLoading()
      
      if (res.success) {
        util.showSuccess('发布成功')
        this.hidePublishDialog()
        
        // 刷新列表
        setTimeout(() => {
          this.loadBottles()
        }, 500)
      } else {
        util.showError(res.message || '发布失败')
      }
    }).catch(err => {
      util.hideLoading()
      console.error('发布失败', err)
      util.showError('发布失败')
    })
  },

  // 点赞
  likeBottle(e) {
    const { id, index } = e.currentTarget.dataset
    
    bottleApi.likeBottle(id).then(res => {
      if (res.success) {
        // 更新点赞状态
        const bottles = this.data.bottles
        bottles[index].isLiked = !bottles[index].isLiked
        bottles[index].likeCount += bottles[index].isLiked ? 1 : -1
        
        this.setData({ bottles })
      }
    }).catch(err => {
      console.error('点赞失败', err)
    })
  },

  // 查看详情（包含回复）
  viewBottleDetail(e) {
    const { id, index } = e.currentTarget.dataset
    const bottle = this.data.bottles[index]
    
    this.setData({
      showDetailDialog: true,
      currentBottle: bottle,
      currentBottleIndex: index,
      replies: [],
      replyContent: '',
      replyAnonymous: false
    })
    
    // 加载回复列表
    this.loadReplies(id)
  },

  // 隐藏详情弹窗
  hideDetailDialog() {
    this.setData({ showDetailDialog: false })
  },

  // 加载回复列表
  loadReplies(bottleId) {
    bottleApi.getBottleReplies(bottleId).then(res => {
      if (res.success && res.replies) {
        const replies = res.replies.map(item => ({
          id: item.id,
          userId: item.userId,
          username: item.username || '未知用户',
          content: item.content,
          isAnonymous: item.isAnonymous,
          likeCount: item.likeCount || 0,
          time: util.relativeTime(item.createTime),
          isLiked: false
        }))
        
        this.setData({ replies })
      }
    }).catch(err => {
      console.error('加载回复失败', err)
    })
  },

  // 回复输入
  onReplyInput(e) {
    this.setData({ replyContent: e.detail.value })
  },

  // 匿名回复选项
  onReplyAnonymousChange(e) {
    this.setData({
      replyAnonymous: e.detail.value.includes('anonymous')
    })
  },

  // 提交回复
  submitReply() {
    const { replyContent, replyAnonymous, currentBottle, userInfo } = this.data

    if (!replyContent.trim()) {
      util.showError('请输入回复内容')
      return
    }

    util.showLoading('回复中...')

    bottleApi.replyBottle({
      bottleId: currentBottle.id,
      replyUserId: userInfo.id,
      content: replyContent,
      isAnonymous: replyAnonymous
    }).then(res => {
      util.hideLoading()
      
      if (res.success) {
        util.showSuccess('回复成功')
        this.setData({ replyContent: '' })
        
        // 刷新回复列表
        this.loadReplies(currentBottle.id)
        
        // 更新主列表的回复数
        const bottles = this.data.bottles
        bottles[this.data.currentBottleIndex].replyCount++
        this.setData({ bottles })
      } else {
        util.showError(res.message || '回复失败')
      }
    }).catch(err => {
      util.hideLoading()
      console.error('回复失败', err)
      util.showError('回复失败')
    })
  },

  // 点赞回复
  likeReply(e) {
    const { id, index } = e.currentTarget.dataset
    
    bottleApi.likeReply(id).then(res => {
      if (res.success) {
        const replies = this.data.replies
        replies[index].isLiked = !replies[index].isLiked
        replies[index].likeCount += replies[index].isLiked ? 1 : -1
        
        this.setData({ replies })
      }
    }).catch(err => {
      console.error('点赞回复失败', err)
    })
  },

  // 删除回复
  deleteReply(e) {
    const { id, index } = e.currentTarget.dataset
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条回复吗？',
      success: (res) => {
        if (res.confirm) {
          util.showLoading('删除中...')
          
          bottleApi.deleteReply(id).then(res => {
            util.hideLoading()
            
            if (res.success) {
              util.showSuccess('删除成功')
              
              // 从回复列表中移除
              const replies = this.data.replies
              replies.splice(index, 1)
              
              // 更新主列表的回复数
              const bottles = this.data.bottles
              bottles[this.data.currentBottleIndex].replyCount--
              
              this.setData({ replies, bottles })
            } else {
              util.showError(res.message || '删除失败')
            }
          }).catch(err => {
            util.hideLoading()
            console.error('删除回复失败', err)
            util.showError('删除失败')
          })
        }
      }
    })
  },

  // 删除漂流瓶
  deleteBottle(e) {
    const { id, index } = e.currentTarget.dataset
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个漂流瓶吗？',
      success: (res) => {
        if (res.confirm) {
          util.showLoading('删除中...')
          
          bottleApi.deleteBottle(id).then(res => {
            util.hideLoading()
            
            if (res.success) {
              util.showSuccess('删除成功')
              
              // 从列表中移除
              const bottles = this.data.bottles
              bottles.splice(index, 1)
              
              this.setData({ bottles })
            } else {
              util.showError(res.message || '删除失败')
            }
          }).catch(err => {
            util.hideLoading()
            console.error('删除漂流瓶失败', err)
            util.showError('删除失败')
          })
        }
      }
    })
  },

  // 获取心情表情
  getMoodEmoji(mood) {
    const moodMap = {
      happy: '😊',
      sad: '😢',
      angry: '😠',
      anxious: '😰',
      calm: '😌'
    }
    return moodMap[mood] || '😊'
  }
})

