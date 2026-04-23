<template>
  <div class="app-page ai-container">
    <div class="app-page__inner">
    <!-- 科技感背景 -->
    <div class="tech-background">
      <div class="floating-elements">
        <div class="floating-circle circle-1"></div>
        <div class="floating-circle circle-2"></div>
        <div class="floating-circle circle-3"></div>
        <div class="floating-circle circle-4"></div>
      </div>
    </div>

    <!-- 页面头部 -->
    <SubPageHeader
      title="AI 心理助手"
      subtitle="智能对话 · 专业分析 · 温暖陪伴"
      eyebrow="星语航海 · AI 互动"
    />

    <div class="tech-badge">
      <span class="badge-item">
        <el-icon><Cpu /></el-icon>
        智能 AI
      </span>
      <span class="badge-item">
        <el-icon><Lock /></el-icon>
        隐私保护
      </span>
      <span class="badge-item">
        <el-icon><Lightning /></el-icon>
        实时响应
      </span>
    </div>

    <div class="ai-content">
      <!-- AI功能选择 -->
      <div class="function-selector reveal-up reveal-up--d2">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="function-card tech-card" @click="startChat('therapy')">
              <div class="card-glow"></div>
              <div class="function-icon therapy">
                <RobotOutlined />
              </div>
              <h3>AI疗愈师</h3>
              <p>情绪疏导、压力缓解、心理疗愈</p>
              <div class="tech-tags">
                <span class="tech-tag">情绪分析</span>
                <span class="tech-tag">压力管理</span>
                <span class="tech-tag">正念引导</span>
              </div>
              <el-button type="primary" size="small" class="tech-button">
                <MessageOutlined /> 开始对话
              </el-button>
            </div>
          </el-col>

          <el-col :span="12">
            <div class="function-card tech-card" @click="startChat('assessment')">
              <div class="card-glow"></div>
              <div class="function-icon assessment">
                <FileSearchOutlined />
              </div>
              <h3>AI心理评估</h3>
              <p>智能评估、专业分析、个性化建议</p>
              <div class="tech-tags">
                <span class="tech-tag">心理测评</span>
                <span class="tech-tag">数据分析</span>
                <span class="tech-tag">专业建议</span>
              </div>
              <el-button type="primary" size="small" class="tech-button">
                <BarChartOutlined /> 开始评估
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 聊天界面 -->
      <div v-if="activeChat" class="chat-container tech-chat reveal-up reveal-up--d3">
        <div class="chat-header">
          <div class="chat-title">
            <i class="el-icon-chat-dot-round"></i>
            <h3>{{ activeChat === 'therapy' ? 'AI疗愈师' : 'AI心理评估' }}</h3>
            <div class="status-indicator">
              <span class="status-dot" :class="{ active: !sending }"></span>
              <span class="status-text">{{ sending ? 'AI思考中...' : '在线' }}</span>
            </div>
          </div>
          <div class="chat-actions">
            <el-button type="text" class="back-button" @click="activeChat = null">
              <i class="el-icon-arrow-left"></i> 返回
            </el-button>
            <el-button type="text" class="close-button" @click="activeChat = null">
              <i class="el-icon-close"></i>
            </el-button>
          </div>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div
            v-for="message in messages"
            :key="message.id"
            :class="['message', message.type]"
          >
            <div class="message-avatar">
              <div class="avatar-tech" :class="message.type">
                <i :class="message.type === 'user' ? 'el-icon-user' : 'el-icon-robot'"></i>
              </div>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div class="typing-indicator" v-if="message.typing">
                  <span></span><span></span><span></span>
                </div>
                <p v-else>{{ message.content }}</p>
                <span class="message-time">{{ formatTime(message.time) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-input">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              :placeholder="activeChat === 'therapy' ? '分享您的感受或困扰...' : '描述您的情况或回答评估问题...'"
              @keydown.enter.prevent="sendMessage"
              class="tech-input"
            />
            <div class="input-features">
              <el-tooltip content="语音输入" placement="top">
                <el-button type="text" @click="toggleVoiceInput">
                  <i class="el-icon-microphone" :class="{ active: voiceInputActive }"></i>
                </el-button>
              </el-tooltip>
              <el-tooltip content="发送消息" placement="top">
                <el-button
                  type="primary"
                  :loading="sending"
                  @click="sendMessage"
                  class="send-button"
                  :disabled="!inputMessage.trim()"
                >
                  <i class="el-icon-send"></i>
                </el-button>
              </el-tooltip>
            </div>
          </div>

          <div class="quick-actions">
            <div class="quick-questions">
              <el-button
                v-for="question in quickQuestions"
                :key="question"
                size="small"
                @click="sendQuickQuestion(question)"
                class="quick-button"
              >
                {{ question }}
              </el-button>
            </div>
            <div class="ai-suggestions">
              <span class="suggestion-label">AI建议：</span>
              <el-button
                v-for="suggestion in aiSuggestions"
                :key="suggestion"
                type="text"
                size="small"
                @click="sendQuickQuestion(suggestion)"
              >
                {{ suggestion }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 使用指南 -->
      <div v-if="!activeChat" class="usage-guide">
        <el-card>
          <template #header>
            <div class="guide-header">
              <span>使用指南</span>
            </div>
          </template>

          <div class="guide-content">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="guide-item">
                  <i class="el-icon-chat-line-round"></i>
                  <h4>自由对话</h4>
                  <p>与AI进行自然对话，分享您的想法和感受</p>
                </div>
              </el-col>

              <el-col :span="8">
                <div class="guide-item">
                  <i class="el-icon-reading"></i>
                  <h4>专业建议</h4>
                  <p>基于心理学理论提供专业的建议和指导</p>
                </div>
              </el-col>

              <el-col :span="8">
                <div class="guide-item">
                  <i class="el-icon-lock"></i>
                  <h4>隐私保护</h4>
                  <p>所有对话内容严格保密，保护您的隐私安全</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </div>

      <!-- 历史记录 -->
      <div v-if="!activeChat" class="history-section">
        <el-card>
          <template #header>
            <div class="history-header">
              <span>最近对话</span>
              <el-button type="text" @click="clearHistory">清空记录</el-button>
            </div>
          </template>

          <div class="history-list">
            <div v-if="chatHistory.length === 0" class="empty-history">
              <i class="el-icon-chat-line-square"></i>
              <p>暂无对话记录</p>
            </div>

            <div
              v-for="history in chatHistory"
              :key="history.id"
              class="history-item"
              @click="loadHistory(history)"
            >
              <div class="history-info">
                <h4>{{ history.title }}</h4>
                <p>{{ history.userMessage }}</p>
                <span class="history-time">{{ formatTime(history.time) }}</span>
              </div>
              <el-button type="text" @click.stop="deleteHistory(history.id)">
                <i class="el-icon-delete"></i>
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import SubPageHeader from '@/components/SubPageHeader.vue'
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { format } from 'date-fns'
import {
  RobotOutlined,
  MessageOutlined,
  FileSearchOutlined,
  BarChartOutlined
} from '@ant-design/icons-vue'

const router = useRouter()

const activeChat = ref(null)
const inputMessage = ref('')
const sending = ref(false)
const messagesContainer = ref(null)
const voiceInputActive = ref(false)

const userAvatar = '/api/placeholder/40x40?text=用户'
const aiAvatar = '/api/placeholder/40x40?text=AI'

const messages = ref([
  {
    id: 1,
    type: 'ai',
    content: '您好！我是您的AI心理助手，很高兴为您服务。请问有什么可以帮助您的吗？',
    time: new Date()
  }
])

const quickQuestions = ref([
  '最近压力很大怎么办？',
  '如何改善睡眠质量？',
  '怎样应对焦虑情绪？',
  '如何提升自信心？'
])

const aiSuggestions = ref([
  '推荐一些放松技巧',
  '帮我分析最近的情绪',
  '提供正念练习指导',
  '评估我的心理状态'
])

const chatHistory = ref([])

const startChat = (type) => {
  activeChat.value = type
  messages.value = [{
    id: 1,
    type: 'ai',
    content: type === 'therapy'
      ? '欢迎来到AI疗愈师！我是您的专属心理助手，可以倾听您的心声，为您提供情绪支持和心理疏导。请告诉我您最近的感受或困扰。'
      : '欢迎使用AI心理评估！我将通过对话了解您的心理状态，为您提供专业的评估和建议。请回答以下问题或分享您的感受。',
    time: new Date()
  }]
  inputMessage.value = ''
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const userMessage = {
    id: Date.now(),
    type: 'user',
    content: inputMessage.value,
    time: new Date()
  }

  messages.value.push(userMessage)
  inputMessage.value = ''
  sending.value = true

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  try {
    // 添加AI思考中的状态
    const thinkingMessage = {
      id: Date.now() + 0.5,
      type: 'ai',
      content: 'AI正在思考中...',
      time: new Date(),
      typing: true
    }
    messages.value.push(thinkingMessage)
    await nextTick()
    scrollToBottom()

    // 获取当前用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id || 4 // 使用存在的用户ID

    // 调用真实AI API
    const apiUrl = activeChat.value === 'therapy'
      ? 'http://localhost:8080/ai/therapy'
      : 'http://localhost:8080/ai/assessment'

    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        message: userMessage.content,
        userId: userId
      })
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const result = await response.json()

    // 移除思考中的消息
    messages.value = messages.value.filter(m => m.id !== thinkingMessage.id)

    if (result.success) {
      const aiMessage = {
        id: Date.now() + 1,
        type: 'ai',
        content: result.data.response,
        time: new Date(),
        typing: false
      }

      messages.value.push(aiMessage)
      updateChatHistory(aiMessage.content)
    } else {
      throw new Error(result.message || 'AI回复失败')
    }
  } catch (error) {
    console.error('AI对话错误:', error)

    // 移除思考中的消息
    messages.value = messages.value.filter(m => m.typing !== true)

    // 显示错误回复
    const errorMessage = {
      id: Date.now() + 1,
      type: 'ai',
      content: '抱歉，AI服务暂时不可用。请检查后端服务是否启动，或稍后重试。',
      time: new Date(),
      typing: false
    }
    messages.value.push(errorMessage)
    ElMessage.error('AI服务连接失败，请检查后端配置')
  } finally {
    sending.value = false
    await nextTick()
    scrollToBottom()
  }
}

const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

const toggleVoiceInput = () => {
  voiceInputActive.value = !voiceInputActive.value
  if (voiceInputActive.value) {
    ElMessage.info('语音输入功能开发中...')
    // 这里可以集成Web Speech API
    setTimeout(() => {
      voiceInputActive.value = false
    }, 3000)
  }
}

// 测试AI服务连接（静默检查，不显示成功提示）
const testAIConnection = async () => {
  try {
    const response = await fetch('http://localhost:8080/ai/health')
    if (response.ok) {
      const result = await response.json()
      // 静默成功，不显示提示
      console.log(`AI服务连接正常: ${result.data.model}`)
    } else {
      // 失败时也不显示，避免打扰用户
      console.warn('AI服务未启动')
    }
  } catch (error) {
    // 连接失败时静默处理
    console.error('无法连接到AI服务')
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const formatTime = (time) => {
  return format(new Date(time), 'HH:mm')
}

const updateChatHistory = (lastMessage) => {
  // 这里不需要手动更新，因为历史记录会从数据库实时获取
  // 保持空实现
}

const loadHistory = async (history) => {
  activeChat.value = history.type === 'AI疗愈' ? 'therapy' : 'assessment'

  try {
    // 从数据库加载真实的对话记录
    const response = await fetch(`http://localhost:8080/ai/history?userId=${history.userId}&sessionId=recent`)
    const result = await response.json()

    if (result.success) {
      messages.value = result.data.map(record => ({
        id: record.id,
        type: 'ai',
        content: record.aiResponse,
        time: new Date(record.createTime)
      }))

      // 添加用户消息
      const userMessage = {
        id: history.id,
        type: 'user',
        content: history.userMessage,
        time: new Date(history.createTime)
      }
      messages.value.unshift(userMessage)
    } else {
      messages.value = [{
        id: 1,
        type: 'ai',
        content: '继续我们之前的对话...',
        time: new Date()
      }]
    }
  } catch (error) {
    console.error('加载历史记录失败:', error)
    messages.value = [{
      id: 1,
      type: 'ai',
      content: '继续我们之前的对话...',
      time: new Date()
    }]
  }
}

const deleteHistory = async (historyId) => {
  try {
    // 获取当前用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id || 1

    // 调用后端API删除记录
    const response = await fetch(`http://localhost:8080/ai/history/${historyId}?userId=${userId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      }
    })

    const result = await response.json()

    if (result.success) {
      // 从前端列表中移除
      chatHistory.value = chatHistory.value.filter(h => h.id !== historyId)
      // 静默删除成功
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error) {
    console.error('删除历史记录失败:', error)
    ElMessage.error('删除失败，请稍后重试')
  }
}

const clearHistory = async () => {
  try {
    // 获取当前用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id || 1

    // 调用后端API清空所有记录
    const response = await fetch(`http://localhost:8080/ai/history/clear?userId=${userId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      }
    })

    const result = await response.json()

    if (result.success) {
      // 清空前端列表
      chatHistory.value = []
      // 静默清空成功
    } else {
      ElMessage.error(result.message || '清空失败')
    }
  } catch (error) {
    console.error('清空历史记录失败:', error)
    ElMessage.error('清空失败，请稍后重试')
  }
}

const loadRealChatHistory = async () => {
  try {
    // 获取当前用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id || 1 // 默认使用用户ID=1

    // 从数据库加载真实的对话历史
    const response = await fetch(`http://localhost:8080/ai/history?userId=${userId}&sessionId=recent`)
    const result = await response.json()

    if (result.success && result.data.length > 0) {
      // 转换数据库记录为前端格式
      chatHistory.value = result.data.map(record => ({
        id: record.id,
        userId: record.userId,
        type: record.interactionType,
        title: `${record.interactionType}对话`,
        userMessage: record.userMessage,
        lastMessage: record.aiResponse ? record.aiResponse.substring(0, 30) + '...' : '暂无回复',
        createTime: record.createTime,
        time: new Date(record.createTime)
      }))
    } else {
      // 如果没有历史记录，显示空状态
      chatHistory.value = []
    }
  } catch (error) {
    console.error('加载对话历史失败:', error)
    chatHistory.value = []
  }
}

const backToMain = () => {
  router.push('/home')
}

onMounted(() => {
  // 自动测试AI服务连接
  testAIConnection()
  // 加载真实的对话历史
  loadRealChatHistory()
})
</script>

<style lang="scss" scoped>
.ai-container {
  position: relative;
  padding: 0;
  min-height: auto;
  background: transparent;
  isolation: isolate;
}

.tech-background {
  position: fixed;
  inset: 0;
  z-index: -2;
  overflow: hidden;
  background:
    radial-gradient(ellipse at 12% 18%, rgba(99, 102, 241, 0.18), transparent 52%),
    radial-gradient(ellipse at 86% 12%, rgba(14, 165, 233, 0.14), transparent 55%),
    radial-gradient(ellipse at 72% 88%, rgba(168, 85, 247, 0.12), transparent 55%),
    linear-gradient(180deg, #fbfcff 0%, #f3f6fd 55%, #eef3fc 100%);
}

.tech-background::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(1px 1px at 18% 22%, rgba(129, 140, 248, 0.55), transparent 60%),
    radial-gradient(1.2px 1.2px at 78% 64%, rgba(56, 189, 248, 0.45), transparent 60%),
    radial-gradient(1px 1px at 42% 82%, rgba(168, 85, 247, 0.4), transparent 60%),
    radial-gradient(1.4px 1.4px at 88% 24%, rgba(99, 102, 241, 0.5), transparent 60%);
  opacity: 0.65;
  pointer-events: none;
}

.floating-elements {
  position: relative;
  width: 100%;
  height: 100%;
}

.floating-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(56px);
  opacity: 0.55;
  animation: floatOrb 14s ease-in-out infinite;

  &.circle-1 {
    width: 320px;
    height: 320px;
    top: 8%;
    left: 6%;
    background: radial-gradient(circle, rgba(99, 102, 241, 0.55), transparent 70%);
    animation-delay: 0s;
  }

  &.circle-2 {
    width: 240px;
    height: 240px;
    top: 52%;
    right: 10%;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.45), transparent 70%);
    animation-delay: 4s;
  }

  &.circle-3 {
    width: 180px;
    height: 180px;
    bottom: 14%;
    left: 22%;
    background: radial-gradient(circle, rgba(168, 85, 247, 0.4), transparent 70%);
    animation-delay: 8s;
  }

  &.circle-4 {
    width: 140px;
    height: 140px;
    top: 26%;
    right: 28%;
    background: radial-gradient(circle, rgba(236, 72, 153, 0.3), transparent 70%);
    animation-delay: 2s;
  }
}

@keyframes floatOrb {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -40px) scale(1.05);
  }
  66% {
    transform: translate(-25px, 28px) scale(0.95);
  }
}

@media (prefers-reduced-motion: reduce) {
  .floating-circle {
    animation: none;
  }
}

.tech-badge {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin: 20px 0 28px;
  flex-wrap: wrap;

  .badge-item {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 9px 18px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.72);
    backdrop-filter: blur(14px);
    -webkit-backdrop-filter: blur(14px);
    border: 1px solid rgba(99, 102, 241, 0.18);
    color: #475569;
    font-size: 12.5px;
    font-weight: 700;
    letter-spacing: 0.3px;
    box-shadow: 0 4px 16px rgba(15, 23, 42, 0.05);
    transition: transform 0.35s cubic-bezier(0.2, 0.8, 0.2, 1),
                background-color 0.3s ease,
                border-color 0.3s ease,
                box-shadow 0.3s ease,
                color 0.3s ease;

    .el-icon {
      font-size: 14px;
      color: #6366f1;
      transition: transform 0.35s cubic-bezier(0.2, 0.8, 0.2, 1);
    }

    &:hover {
      transform: translateY(-2px);
      background: #ffffff;
      border-color: rgba(99, 102, 241, 0.45);
      color: #1e293b;
      box-shadow: 0 10px 22px rgba(99, 102, 241, 0.18);

      .el-icon {
        transform: rotate(-8deg) scale(1.08);
        color: #4f46e5;
      }
    }
  }
}

.ai-content {
  position: relative;
  z-index: 1;
}

.function-selector {
  margin-bottom: 60px;
}

.function-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border-radius: 18px;
  padding: 24px 20px;
  text-align: center;
  cursor: pointer;
  transition:
    transform 0.4s cubic-bezier(0.2, 0.8, 0.2, 1),
    box-shadow 0.4s cubic-bezier(0.2, 0.8, 0.2, 1),
    border-color 0.3s ease;
  box-shadow: 0 6px 24px rgba(15, 23, 42, 0.06);
  min-height: 260px;
  height: auto;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(99, 102, 241, 0.12);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.22);
    transition: left 0.6s ease;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 20px 40px rgba(99, 102, 241, 0.14);
    border-color: rgba(99, 102, 241, 0.32);

    &::before {
      left: 100%;
    }

    .card-glow {
      opacity: 1;
      transform: scale(1.1);
    }
  }
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.04);
  opacity: 0;
  transition: all 0.4s ease;
  pointer-events: none;
}

.function-icon {
  width: 58px;
  height: 58px;
  margin: 0 auto 12px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  position: relative;
  z-index: 2;
  box-shadow: none;
  transition: all 0.4s ease;

  :deep(svg) {
    width: 24px;
    height: 24px;
  }

  &::before {
    content: '';
    position: absolute;
    top: -5px;
    left: -5px;
    right: -5px;
    bottom: -5px;
    border-radius: 50%;
    background: inherit;
    filter: blur(15px);
    opacity: 0.6;
    z-index: -1;
    transition: all 0.4s ease;
  }

  &.therapy {
    background: #4f46e5;
  }

  &.assessment {
    background: #db2777;
  }
}

.function-card:hover .function-icon {
  transform: scale(1.1) rotate(5deg);

  &::before {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

.function-card h3 {
  font-size: 18px;
  font-weight: 800;
  margin-bottom: 8px;
  color: #0f172a;
}

.function-card p {
  color: #475569;
  font-size: 13px;
  margin-bottom: 16px;
  line-height: 1.6;
}

.tech-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.tech-tag {
  background: #f8fafc;
  color: #334155;
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid #dbe2ea;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(102, 126, 234, 0.2);
    transform: translateY(-1px);
  }
}

.tech-button :deep(svg) {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

.tech-chat {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 20px;
  box-shadow: 0 18px 48px rgba(99, 102, 241, 0.12);
  height: 700px;
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(99, 102, 241, 0.14);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, #6366f1, #38bdf8, #a855f7);
  }
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(99, 102, 241, 0.12);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.85), rgba(245, 247, 255, 0.75));
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  .chat-title {
    display: flex;
    align-items: center;
    gap: 12px;

    i {
      font-size: 18px;
      color: #1d4ed8;
    }

    h3 {
      margin: 0;
      font-size: 17px;
      font-weight: 800;
      color: #0f172a;
    }
  }

  .status-indicator {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-left: 20px;

    .status-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #52c41a;
      transition: all 0.3s ease;

      &.active {
        background: #52c41a;
        box-shadow: 0 0 10px #52c41a;
      }
    }

    .status-text {
      font-size: 12px;
      color: #64748b;
      font-weight: 700;
    }
  }

  .chat-actions {
    display: flex;
    align-items: center;
    gap: 15px;

    .back-button {
      background: #ffffff;
      color: #334155;
      font-size: 13px;
      font-weight: 700;
      padding: 8px 12px;
      border-radius: 9px;
      border: 1px solid #d5deea;
      box-shadow: none;
      transition: all 0.2s ease;
      display: flex;
      align-items: center;
      gap: 8px;
      min-width: 98px;
      justify-content: center;

      &:hover {
        transform: translateY(-1px);
        background: #ffffff;
        border-color: #bfd0e6;
      }

      i {
        font-size: 18px;
        font-weight: bold;
      }
    }

    .close-button {
      background: #ffffff;
      color: #64748b;
      font-size: 14px;
      padding: 8px;
      border-radius: 50%;
      border: 1px solid #d5deea;
      transition: all 0.2s ease;

      &:hover {
        color: #1e293b;
        background: #f8fafc;
        border-color: #bfd0e6;
      }
    }
  }
}

.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background:
    linear-gradient(180deg, rgba(238, 242, 255, 0.55), rgba(248, 250, 252, 0.7));

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.05);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(102, 126, 234, 0.3);
    border-radius: 3px;

    &:hover {
      background: rgba(102, 126, 234, 0.5);
    }
  }
}

.message {
  display: flex;
  gap: 15px;
  animation: messageSlideIn 0.3s ease-out;

  &.user {
    flex-direction: row-reverse;

    .message-bubble {
      background: #0f172a;
      color: white;
      border-radius: 12px 12px 4px 12px;
      box-shadow: none;
    }

    .avatar-tech {
      background: #4f46e5;
      box-shadow: none;
    }
  }

  &.ai {
    .message-bubble {
      background: #ffffff;
      color: #1e293b;
      border-radius: 12px 12px 12px 4px;
      box-shadow: none;
      border: 1px solid #dbe2ea;
    }

    .avatar-tech {
      background: #db2777;
      box-shadow: 0 4px 15px rgba(245, 87, 108, 0.3);
    }
  }
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  display: flex;
  align-items: flex-start;
}

.avatar-tech {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.1);
  }
}

.message-bubble {
  padding: 10px 12px;
  max-width: 80%;
  min-width: 120px;
  position: relative;
  transition: all 0.3s ease;
  word-wrap: break-word;
  word-break: break-word;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  }

  p {
    margin: 0 0 8px 0;
    line-height: 1.6;
    font-size: 14px;
  }

  .message-time {
    font-size: 11px;
    opacity: 0.7;
    display: block;
    text-align: right;
  }

  .typing-indicator {
    display: flex;
    gap: 4px;
    align-items: center;

    span {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: currentColor;
      animation: typing 1.4s ease-in-out infinite both;

      &:nth-child(1) { animation-delay: 0s; }
      &:nth-child(2) { animation-delay: 0.2s; }
      &:nth-child(3) { animation-delay: 0.4s; }
    }
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.chat-input {
  border-top: 1px solid #e2e8f0;
  padding: 12px 14px;
  background: #ffffff;
}

.input-wrapper {
  position: relative;
  margin-bottom: 15px;
}

.tech-input {
  :deep(.el-textarea__inner) {
    background: #ffffff;
    border: 1px solid #d8e0ea;
    border-radius: 10px;
    padding: 10px 44px 10px 12px;
    font-size: 13px;
    line-height: 1.7;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);

    &:focus {
      border-color: #1d4ed8;
      box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.14);
      background: #ffffff;
    }

    &::placeholder {
      color: #999;
    }
  }
}

.input-features {
  position: absolute;
  right: 10px;
  bottom: 10px;
  display: flex;
  gap: 8px;
  align-items: center;

  .el-button {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;

    &.send-button {
      background: #0f172a;
      border: 1px solid #0f172a;
      color: white;

      &:hover:not(:disabled) {
        transform: scale(1.03);
        box-shadow: none;
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }

    &.el-button--text {
      color: #999;

      &:hover {
        color: #0f172a;
        background: #f8fafc;
      }

      i.active {
        color: #ff4d4f;
        animation: pulse 1.5s ease infinite;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.quick-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  gap: 20px;
}

.quick-questions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  flex: 1;
}

.quick-button {
  background: #f8fafc;
  border: 1px solid #dbe2ea;
  color: #334155;
  border-radius: 999px;
  padding: 6px 16px;
  font-size: 12px;
  transition: all 0.2s ease;

  &:hover {
    background: #ffffff;
    border-color: #c7d2e4;
    transform: translateY(-1px);
    box-shadow: none;
  }
}

.ai-suggestions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;

  .suggestion-label {
    font-size: 12px;
    color: #666;
    white-space: nowrap;
  }

  .el-button {
    font-size: 12px;
    color: #334155;
    padding: 4px 8px;

    &:hover {
      color: #0f172a;
      background: #f8fafc;
    }
  }
}

.usage-guide,
.history-section {
  margin-bottom: 30px;
}

.guide-content {
  .guide-item {
    text-align: center;
    padding: 20px;

    i {
      font-size: 36px;
      color: #1e90ff;
      margin-bottom: 12px;
      display: block;
    }

    h4 {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 8px;
      color: #333;
    }

    p {
      color: #666;
      font-size: 14px;
      line-height: 1.5;
      margin: 0;
    }
  }
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-list {
  .empty-history {
    text-align: center;
    padding: 40px;
    color: #999;

    i {
      font-size: 48px;
      margin-bottom: 12px;
      display: block;
    }
  }
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background: #f8f9fa;
    border-radius: 4px;
  }

  &:last-child {
    border-bottom: none;
  }
}

.history-info {
  flex: 1;

  h4 {
    font-size: 14px;
    font-weight: bold;
    margin: 0 0 4px 0;
    color: #333;
  }

  p {
    font-size: 12px;
    color: #666;
    margin: 0 0 4px 0;
  }

  .history-time {
    font-size: 12px;
    color: #999;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .tech-badge {
    flex-direction: column;
    gap: 10px;
  }

  .function-selector .el-col {
    margin-bottom: 20px;
  }

  .function-card {
    padding: 20px;
    height: auto;
    min-height: 200px;
  }

  .function-icon {
    width: 60px;
    height: 60px;
    font-size: 28px;
  }

  .chat-container {
    width: 100%;
    max-width: none;
    margin: 0;
    position: relative;
    min-height: auto;
    background: transparent;
    padding: 0;
  }

  .chat-header {
    padding: 15px 20px;

    .chat-title h3 {
      font-size: 16px;
    }
  }

  .chat-messages {
    padding: 20px;
  }

  .message-bubble {
    max-width: 85%;
    padding: 12px 16px;
  }

  .quick-actions {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .ai-suggestions {
    justify-content: center;
    flex-wrap: wrap;

    .suggestion-label {
      display: none;
    }
  }

  .guide-content .el-col {
    margin-bottom: 20px;
  }

  .history-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .ai-container {
    padding: 0;
  }

  .function-card h3 {
    font-size: 16px;
  }

  .function-card p {
    font-size: 12px;
  }

  .tech-tags .tech-tag {
    font-size: 10px;
    padding: 2px 6px;
  }

  .chat-container {
    height: 400px;
  }

  .message {
    gap: 8px;
  }

  .avatar-tech {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }

  .quick-questions {
    justify-content: center;
  }

  .quick-button {
    font-size: 10px;
    padding: 4px 10px;
  }
}
</style>
