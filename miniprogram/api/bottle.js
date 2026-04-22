// 漂流瓶相关API
const request = require('../utils/request')

// 发布漂流瓶
const publishBottle = (data) => {
  return request.post('/bottle/publish', data)
}

// 获取随机漂流瓶
const getBottles = (count = 10) => {
  return request.get(`/bottle/random?count=${count}`)
}

// 点赞漂流瓶
const likeBottle = (id) => {
  return request.post(`/bottle/like/${id}`)
}

// 回复漂流瓶
const replyBottle = (data) => {
  return request.post('/bottle/reply', data)
}

// 获取漂流瓶回复
const getBottleReplies = (bottleId) => {
  return request.get(`/bottle/replies/${bottleId}`)
}

// 点赞回复
const likeReply = (replyId) => {
  return request.post(`/bottle/reply/like/${replyId}`)
}

// 删除漂流瓶
const deleteBottle = (id) => {
  return request.delete(`/bottle/delete/${id}`)
}

// 删除回复
const deleteReply = (replyId) => {
  return request.delete(`/bottle/reply/delete/${replyId}`)
}

module.exports = {
  publishBottle,
  getBottles,
  likeBottle,
  replyBottle,
  getBottleReplies,
  likeReply,
  deleteBottle,
  deleteReply
}

