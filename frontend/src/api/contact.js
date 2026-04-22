import request from '@/utils/request'

/**
 * 联系我们页面API（统一返回结构）
 */

// 提交留言
export const submitContactMessage = async (data) => {
  const res = await request({
    url: '/contact/submit',
    method: 'post',
    data
  })
  return {
    code: res.success ? 200 : 500,
    message: res.message || (res.success ? '成功' : '失败'),
    data: res.data || null
  }
}

// 获取团队专家列表
export const getTeamPsychologists = async () => {
  const res = await request({
    url: '/contact/team',
    method: 'get'
  })
  return {
    code: res.success ? 200 : 500,
    message: res.message || '',
    psychologists: res.psychologists || []
  }
}

// 获取所有留言（管理员用）
export const getAllContactMessages = async () => {
  const res = await request({
    url: '/contact/messages',
    method: 'get'
  })
  return {
    code: res.success ? 200 : 500,
    message: res.message || '',
    messages: res.messages || []
  }
}

// 回复留言（管理员用）
export const replyContactMessage = async (data) => {
  const res = await request({
    url: '/contact/reply',
    method: 'post',
    data
  })
  return {
    code: res.success ? 200 : 500,
    message: res.message || (res.success ? '成功' : '失败')
  }
}