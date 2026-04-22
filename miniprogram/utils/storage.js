// 本地存储工具
const storage = {
  // 设置存储
  set(key, value) {
    try {
      wx.setStorageSync(key, value)
      return true
    } catch (e) {
      console.error('存储失败', e)
      return false
    }
  },

  // 获取存储
  get(key) {
    try {
      return wx.getStorageSync(key)
    } catch (e) {
      console.error('获取存储失败', e)
      return null
    }
  },

  // 删除存储
  remove(key) {
    try {
      wx.removeStorageSync(key)
      return true
    } catch (e) {
      console.error('删除存储失败', e)
      return false
    }
  },

  // 清空存储
  clear() {
    try {
      wx.clearStorageSync()
      return true
    } catch (e) {
      console.error('清空存储失败', e)
      return false
    }
  },

  // 获取用户信息
  getUserInfo() {
    return this.get('userInfo')
  },

  // 设置用户信息
  setUserInfo(userInfo) {
    return this.set('userInfo', userInfo)
  },

  // 获取Token
  getToken() {
    return this.get('token')
  },

  // 设置Token
  setToken(token) {
    return this.set('token', token)
  },

  // 清除登录信息
  clearAuth() {
    this.remove('token')
    this.remove('userInfo')
  }
}

module.exports = storage

