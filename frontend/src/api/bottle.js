import request from '@/utils/request'

// 发布漂流瓶
export const publishBottle = (data) => {
  return request({
    url: '/bottle/publish',
    method: 'post',
    data
  })
}

// 获取随机漂流瓶列表
export const getBottles = (count = 10) => {
  return request({
    url: `/bottle/random?count=${count}`,
    method: 'get'
  })
}

// 点赞漂流瓶
export const likeBottle = (id) => {
  return request({
    url: `/bottle/like/${id}`,
    method: 'post'
  })
}

// 取消点赞（后端没有取消点赞接口，使用点赞接口）
export const unlikeBottle = (id) => {
  return request({
    url: `/bottle/like/${id}`,
    method: 'post'
  })
}

// 回复漂流瓶
export const replyBottle = (data) => {
  return request({
    url: '/bottle/reply',
    method: 'post',
    data
  })
}

// 获取漂流瓶回复
export const getBottleReplies = (bottleId) => {
  return request({
    url: `/bottle/replies/${bottleId}`,
    method: 'get'
  })
}

// 点赞回复
export const likeReply = (replyId) => {
  return request({
    url: `/bottle/reply/like/${replyId}`,
    method: 'post'
  })
}

// 删除漂流瓶
export const deleteBottle = (id) => {
  return request({
    url: `/bottle/delete/${id}`,
    method: 'delete'
  })
}

// 删除回复
export const deleteReply = (replyId) => {
  return request({
    url: `/bottle/reply/delete/${replyId}`,
    method: 'delete'
  })
}