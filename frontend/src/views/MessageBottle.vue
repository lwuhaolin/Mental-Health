<template>
  <div class="app-page bottle-container">
    <div class="app-page__inner">
      <SubPageHeader
        title="漂流瓶"
        subtitle="分享你的心情，也倾听他人的故事"
      />

      <div class="publish-card">
        <div class="publish-header">
          <h3>发布漂流瓶</h3>
        </div>
        <div class="publish-form">
          <el-input
            v-model="newBottleContent"
            type="textarea"
            :rows="4"
            placeholder="写下你的心情，让它随风飘向远方..."
            maxlength="300"
            show-word-limit
          />
          <div class="publish-options">
            <div class="option-group">
              <el-select v-model="newBottleMood" placeholder="选择心情" class="mood-select">
                <el-option
                  v-for="mood in moods"
                  :key="mood.value"
                  :label="mood.label"
                  :value="mood.value"
                >
                  <span>{{ mood.emoji }} {{ mood.label }}</span>
                </el-option>
              </el-select>
              <el-checkbox v-model="publishAnonymous" class="anonymous-checkbox">
                <span class="checkbox-label">匿名发布</span>
              </el-checkbox>
            </div>
            <el-button
              type="primary"
              @click="publishBottle"
              :loading="publishing"
              :disabled="!newBottleContent.trim()"
              class="publish-btn"
            >
              <i class="el-icon-position"></i>
              发布漂流瓶
            </el-button>
          </div>
        </div>
      </div>

      <div class="filter-bar">
        <el-radio-group v-model="filterMood" @change="filterBottles" class="mood-filter">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button
            v-for="mood in moods"
            :key="mood.value"
            :label="mood.value"
          >
            {{ mood.emoji }}
          </el-radio-button>
        </el-radio-group>
        <el-select v-model="sortBy" @change="sortBottles" placeholder="排序方式" class="sort-select">
          <el-option label="最新发布" value="time" />
          <el-option label="点赞最多" value="likes" />
          <el-option label="回复最多" value="replies" />
        </el-select>
      </div>

      <div class="bottle-list">
        <div v-for="bottle in filteredBottles" :key="bottle.id" class="bottle-item">
          <div class="bottle-header">
            <div class="user-info">
              <el-avatar :size="40" :src="bottle.userAvatar" />
              <div class="user-details">
                <span class="username">
                  {{ bottle.isAnonymous ? '匿名用户' : bottle.username }}
                  <el-tag v-if="bottle.userId === userStore.userInfo?.id" size="mini" type="success">我的</el-tag>
                </span>
                <span class="time">{{ formatTime(bottle.createTime) }}</span>
              </div>
            </div>
            <div class="mood-tag">
              <el-tag :color="getMoodColor(bottle.mood)" effect="dark">
                {{ getMoodEmoji(bottle.mood) }} {{ getMoodLabel(bottle.mood) }}
              </el-tag>
            </div>
          </div>

          <div class="bottle-content">
            <p>{{ bottle.content }}</p>
          </div>

          <div class="bottle-actions">
            <div class="action-left">
              <el-button
                :type="bottle.isLiked ? 'danger' : ''"
                size="small"
                class="action-btn like-btn"
                @click="toggleLike(bottle)"
              >
                <i :class="bottle.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                {{ bottle.likeCount || 0 }}
              </el-button>
              <el-button
                type="primary"
                size="small"
                class="action-btn reply-btn"
                @click="toggleReply(bottle)"
              >
                <i class="el-icon-chat-line-round"></i>
                回复 {{ bottle.replyCount || 0 }}
              </el-button>
              <el-button
                type="info"
                size="small"
                plain
                class="action-btn share-btn"
                @click="shareBottle(bottle)"
              >
                <i class="el-icon-share"></i>
                分享
              </el-button>
            </div>
            <div class="action-right" v-if="bottle.userId === userStore.userInfo?.id">
              <el-button
                type="danger"
                size="mini"
                plain
                @click="deleteBottle(bottle)"
              >
                删除
              </el-button>
            </div>
          </div>

          <div v-if="bottle.showReply" class="reply-section">
            <div class="reply-form">
              <div class="reply-input-wrapper">
                <el-input
                  v-model="bottle.replyContent"
                  type="textarea"
                  :rows="3"
                  placeholder="写下你的回复，送上温暖与支持..."
                  maxlength="200"
                  show-word-limit
                  class="reply-textarea"
                />
              </div>
              <div class="reply-actions">
                <div class="reply-options">
                  <el-checkbox v-model="bottle.replyAnonymous" class="anonymous-checkbox">
                    <span class="checkbox-label">匿名回复</span>
                  </el-checkbox>
                </div>
                <div class="reply-buttons">
                  <el-button size="small" @click="cancelReply(bottle)" plain>
                    <i class="el-icon-close"></i> 取消
                  </el-button>
                  <el-button
                    type="primary"
                    size="small"
                    @click="submitReply(bottle)"
                    :loading="bottle.replying"
                    :disabled="!bottle.replyContent?.trim()"
                  >
                    <i class="el-icon-s-promotion"></i> 发送回复
                  </el-button>
                </div>
              </div>
            </div>

            <div class="reply-list-header" v-if="bottle.replies && bottle.replies.length > 0">
              <span class="reply-count">共 {{ bottle.replies.length }} 条回复</span>
            </div>
            <div v-if="bottle.replies && bottle.replies.length > 0" class="reply-list">
              <div v-for="reply in bottle.replies" :key="reply.id" class="reply-item">
                <div class="reply-header">
                  <el-avatar :size="32" :src="reply.userAvatar" />
                  <div class="reply-info">
                    <span class="reply-username">
                      {{ reply.isAnonymous ? '匿名用户' : reply.username }}
                      <el-tag v-if="reply.userId === userStore.userInfo?.id" size="mini" type="success">我的</el-tag>
                    </span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                </div>
                <div class="reply-content">
                  <p>{{ reply.content }}</p>
                </div>
                <div class="reply-actions-bar">
                  <el-button
                    :type="reply.isLiked ? 'danger' : ''"
                    size="mini"
                    class="reply-like-btn"
                    @click="toggleReplyLike(reply)"
                  >
                    <i :class="reply.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                    {{ reply.likeCount || 0 }}
                  </el-button>
                  <el-button
                    v-if="reply.userId === userStore.userInfo?.id"
                    type="danger"
                    size="mini"
                    plain
                    @click="deleteReply(bottle, reply)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>
            <div v-else class="no-replies">
              <p>暂无回复，快来抢沙发吧~</p>
            </div>
          </div>
        </div>
      </div>

      <div class="load-more">
        <el-button
          v-if="hasMore"
          type="text"
          :loading="loadingMore"
          @click="loadMoreBottles"
        >
          加载更多漂流瓶
        </el-button>
        <p v-else class="no-more">没有更多漂流瓶了</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import SubPageHeader from '@/components/SubPageHeader.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { format } from 'date-fns'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const newBottleContent = ref('')
const newBottleMood = ref('happy')
const publishAnonymous = ref(false)
const publishing = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const pageSize = ref(10)
const filterMood = ref('all')
const sortBy = ref('time')
const filteredBottles = ref([])

const moods = ref([
  { value: 'happy', label: '开心', color: '#67C23A', emoji: '😀' },
  { value: 'sad', label: '难过', color: '#909399', emoji: '😢' },
  { value: 'angry', label: '生气', color: '#F56C6C', emoji: '😠' },
  { value: 'anxious', label: '焦虑', color: '#E6A23C', emoji: '😟' },
  { value: 'calm', label: '平静', color: '#409EFF', emoji: '🙂' }
])

const getAvatarUrl = (seed, size = 40) => {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}&size=${size}&backgroundColor=1e90ff,20b2aa,9370db,ffa500,ff6b6b`
}

const bottles = ref([])

const loadBottlesFromBackend = async () => {
  try {
    const response = await fetch(`http://localhost:8080/bottle/random?count=${pageSize.value}`)
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.success) {
      const data = result.bottles || []
      bottles.value = data.map(bottle => ({
        id: bottle.id || bottle.bottleId || 0,
        userId: bottle.userId || 0,
        username: bottle.username || bottle.userName || '用户',
        isAnonymous: bottle.isAnonymous || false,
        userAvatar: getAvatarUrl(bottle.username || 'user'),
        content: bottle.content || bottle.message || '漂流瓶内容',
        mood: bottle.mood || 'calm',
        createTime: bottle.createTime || bottle.createDate || new Date(),
        likeCount: bottle.likeCount || bottle.likes || 0,
        replyCount: bottle.replyCount || 0,
        isLiked: bottle.isLiked || false,
        showReply: false,
        replyContent: '',
        replyAnonymous: false,
        replying: false,
        replies: []
      }))
      filteredBottles.value = bottles.value
      sortBottles()
    } else {
      throw new Error(result.message || '加载漂流瓶数据失败')
    }
  } catch (error) {
    console.error('加载漂流瓶数据失败:', error)
    ElMessage.warning('加载漂流瓶失败，使用示例数据')
    bottles.value = [
      {
        id: 1,
        userId: 2,
        username: '心灵旅人',
        userAvatar: getAvatarUrl('traveler'),
        content: '今天工作压力很大，感觉有点喘不过气来。希望有人愿意听我说说。',
        mood: 'anxious',
        createTime: new Date(Date.now() - 2 * 60 * 60 * 1000),
        likeCount: 8,
        replyCount: 0,
        isLiked: false,
        showReply: false,
        replyContent: '',
        replyAnonymous: false,
        replying: false,
        replies: []
      },
      {
        id: 2,
        userId: 5,
        username: '星空梦想家',
        userAvatar: getAvatarUrl('dreamer'),
        content: '刚刚完成了一个重要项目，今天想把这份开心分享给大家。',
        mood: 'happy',
        createTime: new Date(Date.now() - 4 * 60 * 60 * 1000),
        likeCount: 15,
        replyCount: 0,
        isLiked: true,
        showReply: false,
        replyContent: '',
        replyAnonymous: false,
        replying: false,
        replies: []
      }
    ]
    filteredBottles.value = bottles.value
    sortBottles()
  }
}

const publishBottle = async () => {
  if (!newBottleContent.value.trim()) {
    ElMessage.warning('请输入漂流瓶内容')
    return
  }

  publishing.value = true
  try {
    const currentUserId = userStore.userInfo?.id || 1
    const bottleData = {
      userId: currentUserId,
      content: newBottleContent.value,
      isAnonymous: publishAnonymous.value,
      mood: newBottleMood.value
    }

    const response = await fetch('http://localhost:8080/bottle/publish', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(bottleData)
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`HTTP error! status: ${response.status}, response: ${errorText}`)
    }

    const result = await response.json()
    if (result.success) {
      await loadBottlesFromBackend()
      newBottleContent.value = ''
      newBottleMood.value = 'happy'
      publishAnonymous.value = false
      ElMessage.success('发布成功')
    } else {
      throw new Error(result.message || '发布失败')
    }
  } catch (error) {
    console.error('发布漂流瓶失败:', error)
    ElMessage.error('发布失败: ' + error.message)
  } finally {
    publishing.value = false
  }
}

const toggleLike = async (bottle) => {
  try {
    const response = await fetch(`http://localhost:8080/bottle/like/${bottle.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.success) {
      bottle.isLiked = !bottle.isLiked
      bottle.likeCount += bottle.isLiked ? 1 : -1
    } else {
      throw new Error(result.message || '点赞失败')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('点赞失败，请重试')
  }
}

const loadMoreBottles = async () => {
  loadingMore.value = true
  try {
    const loadedCount = bottles.value.length
    const response = await fetch(`http://localhost:8080/bottle/more?offset=${loadedCount}&limit=3`)
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.success) {
      const data = result.bottles || []
      const newBottles = data.map(bottle => ({
        id: bottle.id || bottle.bottleId || 0,
        userId: bottle.userId || 0,
        username: bottle.username || bottle.userName || '匿名用户',
        isAnonymous: bottle.isAnonymous || false,
        userAvatar: getAvatarUrl(bottle.username || 'user'),
        content: bottle.content || bottle.message || '漂流瓶内容',
        mood: bottle.mood || 'calm',
        createTime: bottle.createTime || bottle.createDate || new Date(),
        likeCount: bottle.likeCount || bottle.likes || 0,
        replyCount: bottle.replyCount || 0,
        isLiked: bottle.isLiked || false,
        showReply: false,
        replyContent: '',
        replyAnonymous: false,
        replying: false,
        replies: []
      }))

      if (newBottles.length > 0) {
        bottles.value.push(...newBottles)
        filterBottles()
        hasMore.value = true
      } else {
        hasMore.value = false
      }
    } else {
      hasMore.value = false
    }
  } catch (error) {
    console.error('加载更多漂流瓶失败:', error)
    hasMore.value = false
    ElMessage.info('没有更多漂流瓶了')
  } finally {
    loadingMore.value = false
  }
}

const toggleReply = (bottle) => {
  bottle.showReply = !bottle.showReply
  if (bottle.showReply && (!bottle.replies || bottle.replies.length === 0)) {
    loadReplies(bottle)
  }
}

const loadReplies = async (bottle) => {
  try {
    const response = await fetch(`http://localhost:8080/bottle/replies/${bottle.id}`)
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.success) {
      const data = result.replies || []
      bottle.replies = data.map(reply => ({
        id: reply.id || reply.replyId || 0,
        userId: reply.userId || 0,
        username: reply.username || reply.userName || '用户',
        isAnonymous: reply.isAnonymous || false,
        userAvatar: getAvatarUrl(reply.username || 'user'),
        content: reply.content || reply.message || '回复内容',
        createTime: reply.createTime || reply.createDate || new Date(),
        likeCount: reply.likeCount || reply.likes || 0,
        isLiked: reply.isLiked || false
      }))
    } else {
      throw new Error(result.message || '加载回复失败')
    }
  } catch (error) {
    console.error('加载漂流瓶回复失败:', error)
    bottle.replies = []
  }
}

const submitReply = async (bottle) => {
  if (!bottle.replyContent?.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  bottle.replying = true
  try {
    const currentUser = userStore.userInfo || { id: 1, username: '当前用户' }
    const replyData = {
      bottleId: bottle.id,
      replyUserId: currentUser.id || 1,
      content: bottle.replyContent,
      isAnonymous: bottle.replyAnonymous || false
    }

    const response = await fetch('http://localhost:8080/bottle/reply', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(replyData)
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`HTTP error! status: ${response.status}, response: ${errorText}`)
    }

    const result = await response.json()
    if (result.success) {
      await loadReplies(bottle)
      bottle.replyContent = ''
      bottle.replyAnonymous = false
      bottle.replyCount = (bottle.replyCount || 0) + 1
      ElMessage.success('回复成功')
    } else {
      throw new Error(result.message || '回复失败')
    }
  } catch (error) {
    console.error('提交回复失败:', error)
    ElMessage.error('回复失败: ' + error.message)
  } finally {
    bottle.replying = false
  }
}

const cancelReply = (bottle) => {
  bottle.showReply = false
  bottle.replyContent = ''
}

const toggleReplyLike = async (reply) => {
  try {
    const response = await fetch(`http://localhost:8080/bottle/reply/like/${reply.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.success) {
      reply.isLiked = !reply.isLiked
      reply.likeCount += reply.isLiked ? 1 : -1
    } else {
      throw new Error(result.message || '点赞失败')
    }
  } catch (error) {
    console.error('点赞回复失败:', error)
    ElMessage.error('点赞失败，请重试')
  }
}

const getMoodColor = (mood) => {
  const moodObj = moods.value.find(m => m.value === mood)
  return moodObj ? moodObj.color : '#909399'
}

const getMoodLabel = (mood) => {
  const moodObj = moods.value.find(m => m.value === mood)
  return moodObj ? moodObj.label : '未知'
}

const getMoodEmoji = (mood) => {
  const moodObj = moods.value.find(m => m.value === mood)
  return moodObj ? moodObj.emoji : '🙂'
}

const formatTime = (time) => {
  return format(new Date(time), 'MM-dd HH:mm')
}

const filterBottles = () => {
  if (filterMood.value === 'all') {
    filteredBottles.value = [...bottles.value]
  } else {
    filteredBottles.value = bottles.value.filter(b => b.mood === filterMood.value)
  }
  sortBottles()
}

const sortBottles = () => {
  if (sortBy.value === 'time') {
    filteredBottles.value.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else if (sortBy.value === 'likes') {
    filteredBottles.value.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  } else if (sortBy.value === 'replies') {
    filteredBottles.value.sort((a, b) => (b.replyCount || 0) - (a.replyCount || 0))
  }
}

const shareBottle = (bottle) => {
  const shareText = `${bottle.content}\n\n来自「星语航海」漂流瓶`

  if (navigator.share) {
    navigator.share({ title: '漂流瓶分享', text: shareText }).catch(() => {
      copyToClipboard(shareText)
    })
  } else {
    copyToClipboard(shareText)
  }
}

const copyToClipboard = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const deleteBottle = (bottle) => {
  ElMessageBox.confirm(
    '确定要删除这个漂流瓶吗？删除后无法恢复。',
    '提示',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await fetch(`http://localhost:8080/bottle/delete/${bottle.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        }
      })

      if (!response.ok) throw new Error('删除失败')
      const data = await response.json()
      if (data.success) {
        bottles.value = bottles.value.filter(b => b.id !== bottle.id)
        filterBottles()
        ElMessage.success('删除成功')
      } else {
        throw new Error(data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除漂流瓶失败:', error)
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {})
}

const deleteReply = (bottle, reply) => {
  ElMessageBox.confirm(
    '确定要删除这条回复吗？',
    '提示',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await fetch(`http://localhost:8080/bottle/reply/delete/${reply.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        }
      })

      if (!response.ok) throw new Error('删除失败')
      const data = await response.json()
      if (data.success) {
        bottle.replies = bottle.replies.filter(r => r.id !== reply.id)
        if (bottle.replyCount && bottle.replyCount > 0) bottle.replyCount--
        ElMessage.success('删除成功')
      } else {
        throw new Error(data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除回复失败:', error)
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {})
}

onMounted(async () => {
  userStore.checkLoginStatus()
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  await loadBottlesFromBackend()
  filterBottles()
})
</script>

<style lang="scss" scoped>
.bottle-container {
  padding: 0;
  min-height: auto;
  background: transparent;
  position: relative;
}

.bottle-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(243, 245, 248, 0.95);
  z-index: -1;
}

.publish-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 14px;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
  border: 1px solid #dbe2ea;
  transition: all 0.2s ease;

  &:hover {
    box-shadow: 0 10px 22px rgba(15, 23, 42, 0.1);
    transform: translateY(-2px);
  }

  .publish-header {
    margin-bottom: 20px;

    h3 {
      margin: 0;
      color: #0f172a;
      font-size: 18px;
      font-weight: 800;
    }
  }

  .publish-form {
    .publish-options {
      margin-top: 16px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;

      .option-group {
        display: flex;
        gap: 16px;
        align-items: center;
        flex: 1;

        .mood-select {
          width: 140px;
        }

        .anonymous-checkbox {
          .checkbox-label {
            font-size: 13px;
            color: #64748b;
            font-weight: 600;
          }
        }
      }

      .publish-btn {
        padding: 0 16px;
        height: 36px;
        font-size: 13px;
        font-weight: 700;
        border-radius: 9px;
        background: #0f172a;
        border: 1px solid #0f172a;
        box-shadow: none;
        transition: all 0.2s ease;

        &:hover {
          transform: translateY(-1px);
          box-shadow: none;
          background: #1e293b;
          border-color: #1e293b;
        }
      }
    }
  }
}

.filter-bar {
  background: #ffffff;
  border-radius: 12px;
  padding: 12px 14px;
  margin-bottom: 14px;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
  border: 1px solid #dbe2ea;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;

  .mood-filter {
    flex: 1;
  }

  .sort-select {
    width: 150px;
  }
}

.bottle-list {
  .bottle-item {
    background: #ffffff;
    border-radius: 14px;
    padding: 14px;
    margin-bottom: 12px;
    box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
    border: 1px solid #dbe2ea;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 22px rgba(15, 23, 42, 0.1);
      border-color: #c7d2e4;
    }

    .bottle-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;

      .user-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .user-details {
          display: flex;
          flex-direction: column;

          .username {
            font-weight: 700;
            color: #0f172a;
            font-size: 14px;
          }

          .time {
            font-size: 12px;
            color: #64748b;
            margin-top: 2px;
          }
        }
      }

      .mood-tag {
        .el-tag {
          font-weight: 600;
        }
      }
    }

    .bottle-content {
      margin-bottom: 16px;

      p {
        margin: 0;
        line-height: 1.75;
        color: #334155;
        font-size: 13px;
      }
    }

    .bottle-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 16px;

      .action-left {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;

        .action-btn {
          border-radius: 999px;
          padding: 6px 12px;
          font-size: 12px;
          font-weight: 700;
          transition: all 0.2s ease;

          &:hover {
            transform: translateY(-2px);
          }

          &.reply-btn {
            background: #0f172a;
            border: 1px solid #0f172a;
            color: white;

            &:hover {
              box-shadow: none;
              background: #1e293b;
              border-color: #1e293b;
            }
          }
        }
      }

      .action-right {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.load-more {
  text-align: center;
  margin-top: 30px;

  .no-more {
    color: #999;
    font-size: 14px;
  }
}

.reply-section {
  margin-top: 12px;
  border-top: 1px solid #e2e8f0;
  padding-top: 12px;
  background: #f8fafc;
  border-radius: 10px;
  padding: 12px;

  .reply-form {
    margin-bottom: 20px;

    .reply-input-wrapper {
      margin-bottom: 12px;

      .reply-textarea {
        border-radius: 12px;
      }
    }

    .reply-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;

      .reply-options {
        flex: 1;

        .anonymous-checkbox {
          .checkbox-label {
            font-size: 12px;
            color: #64748b;
            font-weight: 600;
          }
        }
      }

      .reply-buttons {
        display: flex;
        gap: 10px;
      }
    }
  }

  .reply-list-header {
    margin: 20px 0 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e2e8f0;

    .reply-count {
      font-size: 13px;
      color: #334155;
      font-weight: 700;
    }
  }

  .reply-list {
    .reply-item {
      background: #ffffff;
      border-radius: 10px;
      padding: 12px;
      margin-bottom: 10px;
      border: 1px solid #dbe2ea;
      transition: all 0.2s ease;

      &:hover {
        box-shadow: 0 6px 14px rgba(15, 23, 42, 0.08);
        transform: translateX(2px);
      }

      .reply-header {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 12px;

        .reply-info {
          display: flex;
          flex-direction: column;
          gap: 4px;
          flex: 1;

          .reply-username {
            font-weight: 700;
            color: #0f172a;
            font-size: 13px;
            display: flex;
            align-items: center;
            gap: 8px;
          }

          .reply-time {
            font-size: 12px;
            color: #64748b;
          }
        }
      }

      .reply-content {
        margin-bottom: 12px;

        p {
          margin: 0;
          line-height: 1.7;
          color: #334155;
          font-size: 13px;
        }
      }

      .reply-actions-bar {
        display: flex;
        justify-content: flex-end;
        gap: 8px;

        .reply-like-btn {
          border-radius: 20px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
          }
        }
      }
    }
  }

  .no-replies {
    text-align: center;
    padding: 40px 20px;
    color: #999;
    font-size: 14px;

    p {
      margin: 0;
    }
  }
}
</style>
