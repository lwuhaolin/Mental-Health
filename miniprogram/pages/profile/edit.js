// pages/profile/edit.js
const storage = require('../../utils/storage')
const util = require('../../utils/util')

Page({
  data: {
    userInfo: {
      username: '',
      nickname: '',
      realName: '',
      email: '',
      phone: '',
      age: '',
      avatar: ''
    },
    genderArray: ['男', '女', '其他'],
    genderIndex: 0,
    tempAvatar: '' // 临时头像路径
  },

  onLoad(options) {
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = storage.getUserInfo()
    if (userInfo) {
      // 设置性别索引
      let genderIndex = 0
      if (userInfo.gender === '女') genderIndex = 1
      else if (userInfo.gender === '其他') genderIndex = 2

      this.setData({
        userInfo: {
          username: userInfo.username || '',
          nickname: userInfo.nickname || '',
          realName: userInfo.realName || '',
          email: userInfo.email || '',
          phone: userInfo.phone || '',
          age: userInfo.age || '',
          avatar: userInfo.avatar || ''
        },
        genderIndex,
        tempAvatar: userInfo.avatar || ''
      })
    }
  },

  // 表单输入
  onNicknameInput(e) {
    this.setData({
      'userInfo.nickname': e.detail.value
    })
  },

  onRealNameInput(e) {
    this.setData({
      'userInfo.realName': e.detail.value
    })
  },

  onEmailInput(e) {
    this.setData({
      'userInfo.email': e.detail.value
    })
  },

  onPhoneInput(e) {
    this.setData({
      'userInfo.phone': e.detail.value
    })
  },

  onAgeInput(e) {
    this.setData({
      'userInfo.age': e.detail.value
    })
  },

  onGenderChange(e) {
    this.setData({
      genderIndex: e.detail.value
    })
  },

  // 选择头像
  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0]
        this.setData({
          tempAvatar: tempFilePath,
          'userInfo.avatar': tempFilePath
        })
      },
      fail: (err) => {
        console.error('选择头像失败', err)
      }
    })
  },

  // 保存资料
  saveProfile() {
    const { userInfo, genderIndex, genderArray } = this.data

    // 验证必填项
    if (!userInfo.nickname || !userInfo.nickname.trim()) {
      util.showError('请输入昵称')
      return
    }

    // 验证邮箱格式
    if (userInfo.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(userInfo.email)) {
      util.showError('邮箱格式不正确')
      return
    }

    // 验证手机号格式
    if (userInfo.phone && !/^1[3-9]\d{9}$/.test(userInfo.phone)) {
      util.showError('手机号格式不正确')
      return
    }

    // 验证年龄
    if (userInfo.age && (userInfo.age < 1 || userInfo.age > 150)) {
      util.showError('年龄不合法')
      return
    }

    util.showLoading('保存中...')

    // 构造更新数据
    const updateData = {
      ...userInfo,
      gender: genderArray[genderIndex]
    }

    // 这里应该调用API更新用户信息
    // 暂时直接更新本地存储
    setTimeout(() => {
      const currentUser = storage.getUserInfo()
      const newUserInfo = {
        ...currentUser,
        ...updateData
      }
      
      storage.setUserInfo(newUserInfo)
      
      util.hideLoading()
      util.showSuccess('保存成功')
      
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }, 1000)
  },

  // 取消编辑
  cancelEdit() {
    wx.navigateBack()
  }
})

