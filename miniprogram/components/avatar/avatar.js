// components/avatar/avatar.js
const avatarUtil = require('../../utils/avatar')

Component({
  properties: {
    // 用户信息
    userInfo: {
      type: Object,
      value: {}
    },
    // 头像大小
    size: {
      type: Number,
      value: 80
    }
  },

  data: {
    avatarData: null,
    showVirtual: false
  },

  observers: {
    'userInfo': function(userInfo) {
      this.updateAvatar(userInfo)
    }
  },

  lifetimes: {
    attached() {
      this.updateAvatar(this.properties.userInfo)
    }
  },

  methods: {
    updateAvatar(userInfo) {
      if (!userInfo || !userInfo.username) {
        this.setData({
          showVirtual: true,
          avatarData: avatarUtil.generateVirtualAvatar('User')
        })
        return
      }

      // 检查是否有真实头像
      if (userInfo.avatar && userInfo.avatar.trim() !== '' && !userInfo.avatar.includes('ui-avatars.com')) {
        this.setData({
          showVirtual: false
        })
      } else {
        // 使用虚拟头像
        this.setData({
          showVirtual: true,
          avatarData: avatarUtil.generateVirtualAvatar(userInfo.username)
        })
      }
    }
  }
})

