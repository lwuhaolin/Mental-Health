// pages/assessment/taking/taking.js
const assessmentApi = require('../../../api/assessment')
const util = require('../../../utils/util')
const storage = require('../../../utils/storage')

Page({
  data: {
    assessmentId: 0,
    questions: [],
    currentQuestion: {},
    currentIndex: 0,
    answers: {},
    progress: 0,
    showSheet: false,
    isAllAnswered: false,
    userInfo: {}
  },

  onLoad(options) {
    const { id } = options
    this.setData({ assessmentId: id })
    this.loadUserInfo()
    this.loadQuestions()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    this.setData({ userInfo })
  },

  // 加载题目
  loadQuestions() {
    util.showLoading('加载中...')

    assessmentApi.startAssessment(this.data.assessmentId).then(res => {
      util.hideLoading()
      
      console.log('题目接口返回:', res)
      
      // 后端返回格式: {code: 200, message: "...", data: [...]}
      if (res.code === 200 && res.data && res.data.length > 0) {
        // 格式化题目数据
        const formattedQuestions = res.data.map(item => ({
          id: item.id,
          questionText: item.questionText || item.content,
          questionType: item.questionType || '单选',
          options: this.parseOptions(item.options)
        }))
        
        this.setData({
          questions: formattedQuestions,
          currentQuestion: formattedQuestions[0]
        })
        this.updateProgress()
      } else {
        // 后端返回空数据，使用模拟数据
        console.log('题目数据为空，使用模拟数据')
        this.setMockQuestions()
      }
    }).catch(err => {
      util.hideLoading()
      console.error('加载题目失败', err)
      
      // 使用模拟数据
      util.showToast({
        title: '使用演示数据',
        icon: 'none'
      })
      setTimeout(() => {
        this.setMockQuestions()
      }, 500)
    })
  },
  
  // 解析选项数据
  parseOptions(options) {
    if (!options) return []
    
    // 如果是字符串，尝试解析JSON
    if (typeof options === 'string') {
      try {
        options = JSON.parse(options)
      } catch (e) {
        console.error('解析选项失败', e)
        return []
      }
    }
    
    // 如果是数组，格式化选项
    if (Array.isArray(options)) {
      return options.map((opt, index) => ({
        label: opt.label || String.fromCharCode(65 + index), // A, B, C, D...
        text: opt.text || opt.content || opt,
        value: opt.value !== undefined ? opt.value : (index + 1)
      }))
    }
    
    return []
  },

  // 模拟数据
  setMockQuestions() {
    const mockQuestions = []
    const questionTexts = [
      '我觉得比平常容易紧张或着急',
      '我无缘无故感到害怕',
      '我容易心烦意乱或感到惊恐',
      '我觉得我可能要发疯了',
      '我感到一切都很好，不会发生什么不幸',
      '我手脚发抖颤动',
      '我因头疼、颈疼和背疼而苦恼',
      '我感觉容易衰弱和疲乏',
      '我觉得心平气和，并且容易安静坐着',
      '我感到心跳很快',
      '我因阵阵眩晕而苦恼',
      '我有阵阵要晕倒的感觉',
      '我呼吸时感到很轻松',
      '我手脚麻木和刺痛',
      '我因胃痛和消化不良而苦恼',
      '我常常要小便',
      '我的手经常是温暖而干燥的',
      '我脸红发热',
      '我容易入睡，晚上休息得很好',
      '我做恶梦'
    ]
    
    for (let i = 0; i < 20; i++) {
      mockQuestions.push({
        id: i + 1,
        questionText: questionTexts[i],
        options: [
          { label: 'A', text: '没有或很少时间', value: 1 },
          { label: 'B', text: '小部分时间', value: 2 },
          { label: 'C', text: '相当多时间', value: 3 },
          { label: 'D', text: '绝大部分或全部时间', value: 4 }
        ]
      })
    }
    this.setData({ 
      questions: mockQuestions,
      currentQuestion: mockQuestions[0]
    })
    this.updateProgress()
  },

  // 获取当前题目
  getCurrentQuestion() {
    const { questions, currentIndex } = this.data
    return questions[currentIndex] || {}
  },

  // 选择选项
  selectOption(e) {
    const value = e.currentTarget.dataset.value
    const { currentIndex, answers } = this.data
    
    answers[currentIndex] = value
    
    this.setData({ answers })
    this.updateProgress()
  },

  // 上一题
  prevQuestion() {
    if (this.data.currentIndex > 0) {
      const newIndex = this.data.currentIndex - 1
      this.setData({
        currentIndex: newIndex,
        currentQuestion: this.data.questions[newIndex]
      })
    }
  },

  // 下一题
  nextQuestion() {
    if (this.data.currentIndex < this.data.questions.length - 1) {
      const newIndex = this.data.currentIndex + 1
      this.setData({
        currentIndex: newIndex,
        currentQuestion: this.data.questions[newIndex]
      })
    }
  },

  // 跳转到指定题目
  jumpToQuestion(e) {
    const index = e.currentTarget.dataset.index
    this.setData({
      currentIndex: index,
      currentQuestion: this.data.questions[index]
    })
  },

  // 更新进度
  updateProgress() {
    const { questions, answers } = this.data
    const answeredCount = Object.keys(answers).length
    const progress = questions.length > 0 ? (answeredCount / questions.length * 100) : 0
    
    this.setData({
      progress,
      isAllAnswered: answeredCount === questions.length
    })
  },

  // 切换答题卡
  toggleSheet() {
    this.setData({
      showSheet: !this.data.showSheet
    })
  },

  // 提交评估
  submitAssessment() {
    const { assessmentId, answers, questions, userInfo } = this.data

    if (Object.keys(answers).length !== questions.length) {
      util.showError('请完成所有题目')
      return
    }

    wx.showModal({
      title: '提示',
      content: '确定要提交评估吗？提交后将无法修改答案。',
      success: (res) => {
        if (res.confirm) {
          this.doSubmit()
        }
      }
    })
  },

  // 执行提交
  doSubmit() {
    const { assessmentId, answers, userInfo, questions } = this.data

    util.showLoading('提交中...')

    // 转换答案格式为后端期望的格式
    // 后端期望: {answers: {questionId: answer, ...}, timeSpent: 0}
    const answersMap = {}
    Object.keys(answers).forEach(key => {
      const questionId = questions[key].id
      answersMap[questionId] = answers[key]
    })

    const submitData = {
      assessmentId: assessmentId,
      userId: userInfo.id,
      answers: answersMap,
      timeSpent: 0  // 可以记录实际测评时间
    }

    console.log('提交评估数据:', submitData)

    assessmentApi.submitAssessment(submitData).then(res => {
      util.hideLoading()
      
      console.log('评估提交响应:', res)
      
      // 后端返回格式: {code: 200, message: "...", data: {...}}
      if (res.code === 200) {
        util.showSuccess('提交成功')
        
        // 跳转到结果页面，传递结果ID
        setTimeout(() => {
          const resultId = res.data && res.data.id ? res.data.id : ''
          wx.redirectTo({
            url: `/pages/assessment/result/result?id=${assessmentId}&resultId=${resultId}`
          })
        }, 1500)
      } else {
        util.showError(res.message || '提交失败')
      }
    }).catch(err => {
      util.hideLoading()
      console.error('提交失败', err)
      util.showError('提交失败，请重试')
    })
  },

  // 页面卸载前确认
  onUnload() {
    // 可以在这里添加离开确认
  }
})

