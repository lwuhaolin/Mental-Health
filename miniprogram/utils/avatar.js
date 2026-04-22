// 虚拟头像工具
// 为用户生成虚拟头像

// 虚拟头像列表（使用emoji作为头像）
const AVATAR_EMOJIS = [
  '😊', '😃', '🙂', '😄', '😁', 
  '🤗', '🌟', '💫', '✨', '🎈',
  '🌸', '🌺', '🌻', '🌷', '🌹',
  '🦋', '🐱', '🐶', '🐰', '🐻'
]

// 虚拟头像颜色列表
const AVATAR_COLORS = [
  '#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A',
  '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E2',
  '#F8B500', '#52B788', '#E76F51', '#2A9D8F'
]

/**
 * 根据用户名生成虚拟头像数据
 * @param {string} username - 用户名
 * @returns {object} 包含emoji和颜色的头像数据
 */
function generateVirtualAvatar(username) {
  if (!username) {
    return {
      emoji: AVATAR_EMOJIS[0],
      color: AVATAR_COLORS[0],
      text: '?'
    }
  }

  // 使用用户名的第一个字符的charCode作为种子
  const seed = username.charCodeAt(0)
  const emojiIndex = seed % AVATAR_EMOJIS.length
  const colorIndex = seed % AVATAR_COLORS.length
  
  // 获取用户名的第一个字符作为文字头像
  const text = username.charAt(0).toUpperCase()

  return {
    emoji: AVATAR_EMOJIS[emojiIndex],
    color: AVATAR_COLORS[colorIndex],
    text: text
  }
}

/**
 * 获取用户头像URL，如果没有则返回虚拟头像数据
 * @param {object} userInfo - 用户信息对象
 * @returns {string} 头像URL或虚拟头像标识
 */
function getUserAvatar(userInfo) {
  if (!userInfo) {
    return null
  }

  // 如果有真实头像，直接返回
  if (userInfo.avatar && userInfo.avatar.trim() !== '') {
    return userInfo.avatar
  }

  // 返回虚拟头像标识
  return `virtual:${userInfo.username || 'user'}`
}

/**
 * 判断是否为虚拟头像
 * @param {string} avatar - 头像URL
 * @returns {boolean}
 */
function isVirtualAvatar(avatar) {
  return avatar && avatar.startsWith('virtual:')
}

/**
 * 从虚拟头像标识中提取用户名
 * @param {string} avatar - 头像URL
 * @returns {string}
 */
function extractUsername(avatar) {
  if (isVirtualAvatar(avatar)) {
    return avatar.replace('virtual:', '')
  }
  return ''
}

module.exports = {
  generateVirtualAvatar,
  getUserAvatar,
  isVirtualAvatar,
  extractUsername,
  AVATAR_EMOJIS,
  AVATAR_COLORS
}

