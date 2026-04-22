// pages/assessment/list/list.js
const assessmentApi = require('../../../api/assessment')
const util = require('../../../utils/util')
const storage = require('../../../utils/storage')

Page({
  data: {
    activeTab: 'all',
    assessments: [],
    historyList: [],
    userInfo: {}
  },

  onLoad(options) {
    this.loadUserInfo()
    this.loadAssessments()
  },

  onShow() {
    // 每次显示时刷新历史记录
    if (this.data.activeTab === 'history') {
      this.loadHistory()
    }
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    this.setData({ userInfo })
  },

  // 切换Tab
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })

    if (tab === 'history') {
      this.loadHistory()
    }
  },

  // 加载评估列表
  loadAssessments() {
    util.showLoading('加载中...')

    assessmentApi.getAssessments().then(res => {
      util.hideLoading()
      
      if (res.data) {
        this.setData({
          assessments: res.data
        })
      }
    }).catch(err => {
      util.hideLoading()
      console.error('加载评估列表失败', err)
      
      // 使用模拟数据
      this.setData({
        assessments: [
          {
            id: 1,
            name: 'SDS抑郁自评量表',
            description: '评估抑郁情绪的严重程度，帮助了解当前的心理状态',
            questionCount: 20,
            estimatedTime: 10,
            category: '情绪评估'
          },
          {
            id: 2,
            name: 'SAS焦虑自评量表',
            description: '评估焦虑水平，识别焦虑症状',
            questionCount: 20,
            estimatedTime: 10,
            category: '情绪评估'
          },
          {
            id: 3,
            name: '心理压力测评',
            description: '评估当前的心理压力水平，提供压力管理建议',
            questionCount: 15,
            estimatedTime: 8,
            category: '压力评估'
          }
        ]
      })
    })
  },

  // 加载历史记录
  loadHistory() {
    const { userInfo } = this.data
    if (!userInfo || !userInfo.id) return

    util.showLoading('加载中...')

    assessmentApi.getAssessmentHistory(userInfo.id).then(res => {
      util.hideLoading()
      
      console.log('评估历史响应:', res)
      
      // 后端返回格式: {code: 200, data: [...]}
      if (res.code === 200 && res.data) {
        const historyList = res.data.map(item => ({
          id: item.id,
          assessmentId: item.assessmentId,
          assessmentTitle: item.assessmentTitle,
          category: item.category,
          score: item.totalScore,
          level: item.assessmentLevel,
          completedTime: util.formatDateTime(item.completionTime),
          suggestion: item.suggestion
        }))
        
        console.log('格式化后的历史记录:', historyList)
        this.setData({ historyList })
      } else if (res.data) {
        // 兼容其他返回格式
        const historyList = res.data.map(item => ({
          id: item.id,
          assessmentId: item.assessmentId,
          assessmentTitle: item.assessmentTitle,
          category: item.category,
          score: item.totalScore || item.score,
          level: item.assessmentLevel || item.level,
          completedTime: util.formatDateTime(item.completionTime || item.completedAt || item.createdAt),
          suggestion: item.suggestion
        }))
        
        this.setData({ historyList })
      } else {
        // 没有数据
        this.setData({ historyList: [] })
      }
    }).catch(err => {
      util.hideLoading()
      console.error('加载历史记录失败', err)
      
      // 显示空状态
      this.setData({ historyList: [] })
    })
  },

  // 开始评估
  startAssessment(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/assessment/taking/taking?id=${id}`
    })
  },

  // 查看结果
  viewResult(e) {
    const item = e.currentTarget.dataset.item
    const assessmentId = item.assessmentId || item.id
    const resultId = item.id
    
    console.log('查看结果:', assessmentId, resultId)
    
    wx.navigateTo({
      url: `/pages/assessment/result/result?id=${assessmentId}&resultId=${resultId}`
    })
  }
})

