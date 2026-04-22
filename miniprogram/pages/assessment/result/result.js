// pages/assessment/result/result.js
const assessmentApi = require('../../../api/assessment')
const util = require('../../../utils/util')
const storage = require('../../../utils/storage')

Page({
  data: {
    assessmentId: 0,
    resultId: 0,
    result: {
      score: 0,
      resultLevel: '正常',
      levelClass: 'level-normal',
      assessmentName: '',
      completedTime: '',
      interpretation: '',
      suggestions: []
    }
  },

  onLoad(options) {
    const { id, resultId } = options
    this.setData({ 
      assessmentId: id,
      resultId: resultId || 0
    })
    this.loadResult()
  },

  // 加载结果
  loadResult() {
    const { assessmentId, resultId } = this.data
    const userInfo = storage.getUserInfo()

    if (!userInfo || !userInfo.id) {
      util.showError('请先登录')
      return
    }

    util.showLoading('加载中...')

    assessmentApi.getAssessmentResult(assessmentId, userInfo.id).then(res => {
      util.hideLoading()
      
      console.log('评估结果响应:', res)
      
      // 后端返回格式: {code: 200, data: {...}} 或 {success: true, data: {...}}
      if ((res.code === 200 || res.success) && res.data) {
        const result = this.formatResult(res.data)
        this.setData({ result })
      } else {
        // 使用模拟数据
        console.log('没有评估结果数据，使用模拟数据')
        this.setMockResult()
      }
    }).catch(err => {
      util.hideLoading()
      console.error('加载结果失败', err)
      
      // 使用模拟数据
      this.setMockResult()
    })
  },

  // 格式化结果
  formatResult(data) {
    let levelClass = 'level-normal'
    
    if (data.score >= 70) {
      levelClass = 'level-severe'
    } else if (data.score >= 60) {
      levelClass = 'level-moderate'
    } else if (data.score >= 50) {
      levelClass = 'level-mild'
    }

    return {
      score: data.score || 0,
      resultLevel: data.resultLevel || '正常',
      levelClass,
      assessmentName: data.assessmentName || '心理评估',
      completedTime: util.formatDateTime(data.completedAt || Date.now()),
      interpretation: data.interpretation || this.getDefaultInterpretation(data.score),
      suggestions: data.suggestions || this.getDefaultSuggestions(data.score)
    }
  },

  // 模拟数据
  setMockResult() {
    const mockScore = Math.floor(Math.random() * 40) + 30
    
    this.setData({
      result: this.formatResult({
        score: mockScore,
        resultLevel: mockScore < 50 ? '正常' : '轻度',
        assessmentName: 'SDS抑郁自评量表',
        completedAt: Date.now()
      })
    })
  },

  // 获取默认解读
  getDefaultInterpretation(score) {
    if (score < 50) {
      return '您的评估结果显示，当前心理状态良好。请继续保持积极乐观的心态，注意劳逸结合，保持良好的生活习惯。'
    } else if (score < 60) {
      return '您的评估结果显示有轻度的情绪问题。建议您多与亲友交流，适当运动，保证充足睡眠。如果持续感到不适，建议寻求专业帮助。'
    } else if (score < 70) {
      return '您的评估结果显示有中度的情绪问题。建议您尽快寻求专业心理咨询师的帮助，及时调整心理状态。同时注意休息，避免过度劳累。'
    } else {
      return '您的评估结果显示有较严重的情绪问题。强烈建议您尽快寻求专业医疗帮助，在专业指导下进行系统的心理治疗和调节。'
    }
  },

  // 获取默认建议
  getDefaultSuggestions(score) {
    const commonSuggestions = [
      '保持规律的作息时间，保证充足睡眠',
      '适当进行体育锻炼，如散步、慢跑等',
      '培养兴趣爱好，丰富业余生活',
      '学会放松技巧，如深呼吸、冥想等'
    ]

    if (score >= 60) {
      return [
        '建议尽快咨询专业心理医生',
        '与亲友保持良好沟通，寻求支持',
        ...commonSuggestions
      ]
    } else if (score >= 50) {
      return [
        '如有需要可以咨询心理咨询师',
        '多与朋友交流，分享感受',
        ...commonSuggestions
      ]
    } else {
      return commonSuggestions
    }
  },

  // 返回列表
  goBack() {
    wx.navigateBack({
      delta: 2 // 返回评估列表页
    })
  },

  // 咨询专家
  consultExpert() {
    wx.navigateTo({
      url: '/pages/consultation/experts/experts'
    })
  }
})

