<template>
  <div class="app-page assessment-result-container">
    <div class="app-page__inner">
      <div class="result-header">
        <div class="result-title-section">
          <h1 class="app-section__title">测评结果报告</h1>
          <p class="assessment-title">{{ assessmentTitle }}</p>
          <p class="completion-time">完成时间: {{ formatTime(resultData.completedTime) }}</p>
        </div>

        <div class="result-score-section">
          <div class="score-card">
            <div class="score-value">{{ resultData.score || 0 }}</div>
            <div class="score-label">得分<span v-if="resultData.maxScore"> / {{ resultData.maxScore }}</span></div>
          </div>
          <div class="level-card">
            <el-tag :type="getLevelTagType(resultData.level)" size="large">
              {{ resultData.level || '未知' }}
            </el-tag>
            <div class="level-label">评估等级</div>
          </div>
          <div class="completion-card" v-if="resultData.completionRate">
            <div class="completion-value">{{ resultData.completionRate }}</div>
            <div class="completion-label">完成率</div>
          </div>
        </div>
      </div>

      <div class="result-content">
        <div class="analysis-section">
          <h3 class="section-title">
            <i class="el-icon-document"></i>
            结果分析
          </h3>
          <div class="analysis-content">
            <p>{{ resultData.suggestion || '暂无分析建议，请稍后再试。' }}</p>
          </div>
        </div>

        <div class="suggestions-section">
          <h3 class="section-title">
            <i class="el-icon-lightbulb"></i>
            建议清单
          </h3>
          <div class="suggestions-content">
            <ul class="suggestions-list">
              <li
                v-for="(suggestion, index) in parsedSuggestions"
                :key="index"
                class="suggestion-item"
              >
                <i class="el-icon-check"></i>
                {{ suggestion }}
              </li>
            </ul>
          </div>
        </div>

        <div class="statistics-section">
          <h3 class="section-title">
            <i class="el-icon-data-analysis"></i>
            统计信息
          </h3>
          <div class="statistics-content">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-card">
                  <div class="stat-icon">
                    <i class="el-icon-time"></i>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ formatTime(resultData.completedTime) }}</div>
                    <div class="stat-label">完成时间</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-card">
                  <div class="stat-icon">
                    <i class="el-icon-trophy"></i>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">
                      {{ resultData.score || 0 }}<span v-if="resultData.maxScore"> / {{ resultData.maxScore }}</span>
                    </div>
                    <div class="stat-label">总分</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-card">
                  <div class="stat-icon">
                    <i class="el-icon-star-on"></i>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ resultData.level || '未知' }}</div>
                    <div class="stat-label">等级</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>

        <div class="actions-section">
          <h3 class="section-title">
            <i class="el-icon-guide"></i>
            推荐行动方案
          </h3>
          <div class="actions-content">
            <div class="action-cards">
              <div class="action-card" v-for="action in recommendedActions" :key="action.id">
                <div class="action-icon" :style="{ backgroundColor: action.color }">
                  <i :class="action.icon"></i>
                </div>
                <div class="action-info">
                  <h4>{{ action.title }}</h4>
                  <p>{{ action.description }}</p>
                </div>
                <el-button type="primary" size="small" @click="handleAction(action.id)">
                  {{ action.buttonText }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="action-buttons">
        <el-button @click="goBack" size="large">
          <i class="el-icon-arrow-left"></i>
          {{ backButtonText }}
        </el-button>

        <el-button type="primary" size="large" @click="saveResult">
          <i class="el-icon-download"></i>
          保存报告
        </el-button>

        <el-button type="warning" size="large" @click="consultExpert">
          <i class="el-icon-service"></i>
          咨询专家
        </el-button>
      </div>

      <el-dialog
        v-model="showSaveDialog"
        title="保存成功"
        width="400px"
      >
        <div class="save-success">
          <i class="el-icon-success" style="color: #67C23A; font-size: 48px;"></i>
          <p>测评报告已保存到你的个人中心，可随时查看。</p>
        </div>

        <template #footer>
          <el-button type="primary" @click="showSaveDialog = false">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAssessmentResult } from '@/api/assessment'

const route = useRoute()
const router = useRouter()

const assessmentId = ref(route.params.id)
const assessmentTitle = ref('心理测评')
const showSaveDialog = ref(false)

const fromProfile = ref(route.query.from === 'profile')
const backButtonText = ref(fromProfile.value ? '返回个人中心' : '返回测评中心')

const resultData = ref({})

const parsedSuggestions = computed(() => {
  const raw = resultData.value?.suggestion || ''
  if (!raw) return ['保持规律作息，必要时寻求专业支持。']
  return raw
    .split(/[。；\n]/)
    .map(item => item.trim())
    .filter(Boolean)
})

const recommendedActions = ref([
  {
    id: 1,
    type: 'consult',
    title: 'AI 心理对话',
    description: '通过智能对话快速获得情绪支持与建议。',
    icon: 'el-icon-chat-line-round',
    color: '#2563eb',
    buttonText: '立即体验'
  },
  {
    id: 2,
    type: 'training',
    title: '放松训练',
    description: '进行呼吸训练与正念练习，缓解压力。',
    icon: 'el-icon-magic-stick',
    color: '#0f766e',
    buttonText: '开始训练'
  },
  {
    id: 3,
    type: 'course',
    title: '预约专家',
    description: '与专业心理咨询师预约一对一咨询。',
    icon: 'el-icon-user',
    color: '#d97706',
    buttonText: '去预约'
  }
])

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  if (Number.isNaN(date.getTime())) return '-'
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getLevelTagType = (level) => {
  const typeMap = {
    轻度: 'success',
    中度: 'warning',
    偏高: 'warning',
    重度: 'danger'
  }
  return typeMap[level] || 'info'
}

const goBack = () => {
  if (fromProfile.value) {
    router.push('/profile')
  } else {
    router.push('/assessment')
  }
}

const saveResult = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (!userInfo.id) {
      ElMessage.error('请先登录')
      return
    }

    const assessmentHistory = JSON.parse(localStorage.getItem('assessmentHistory') || '[]')

    const existingIndex = assessmentHistory.findIndex(
      item => item.userId === userInfo.id &&
        item.assessmentId === assessmentId.value &&
        item.completionTime === resultData.value.completedTime
    )

    const reportData = {
      id: existingIndex >= 0 ? assessmentHistory[existingIndex].id : Date.now(),
      userId: userInfo.id,
      assessmentId: assessmentId.value,
      assessmentTitle: assessmentTitle.value,
      completionTime: resultData.value.completedTime || new Date().toISOString(),
      totalScore: resultData.value.score || 0,
      maxScore: resultData.value.maxScore || 0,
      assessmentLevel: resultData.value.level || '未知',
      suggestion: resultData.value.suggestion || '',
      completionRate: resultData.value.completionRate || '100%',
      category: resultData.value.category || '',
      savedAt: new Date().toISOString()
    }

    if (existingIndex >= 0) {
      assessmentHistory[existingIndex] = reportData
    } else {
      assessmentHistory.unshift(reportData)
      const userHistory = assessmentHistory.filter(item => item.userId === userInfo.id)
      if (userHistory.length > 50) {
        const oldestRecord = userHistory[userHistory.length - 1]
        const oldestIndex = assessmentHistory.findIndex(item => item.id === oldestRecord.id)
        if (oldestIndex >= 0) assessmentHistory.splice(oldestIndex, 1)
      }
    }

    localStorage.setItem('assessmentHistory', JSON.stringify(assessmentHistory))
    showSaveDialog.value = true
  } catch (error) {
    console.error('保存报告失败:', error)
    ElMessage.error('保存报告失败')
  }
}

const consultExpert = () => {
  router.push('/expert-consultation')
}

const handleAction = (actionId) => {
  const action = recommendedActions.value.find(a => a.id === actionId)
  if (!action) return

  switch (action.type) {
    case 'consult':
      router.push('/ai-interaction')
      break
    case 'training':
      ElMessage.info('放松训练功能开发中...')
      break
    case 'course':
      router.push('/expert-consultation')
      break
    default:
      ElMessage.info('功能开发中...')
  }
}

onMounted(async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (!userInfo.id) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const userId = userInfo.id
    const response = await getAssessmentResult(assessmentId.value, userId)

    if (response && response.code === 200 && response.data) {
      resultData.value = response.data
      assessmentTitle.value = response.data.assessmentTitle || '心理测评'
    } else {
      ElMessage.error('获取测评结果失败: ' + (response?.message || '未知错误'))
    }
  } catch (error) {
    console.error('获取测评结果失败:', error)
    ElMessage.error('获取测评结果失败，请稍后重试')
  }
})
</script>

<style lang="scss" scoped>
.assessment-result-container {
  padding: 0;
}

.result-header {
  background: white;
  border-radius: var(--app-radius-lg);
  border: 1px solid var(--app-border);
  padding: 20px;
  margin-bottom: 18px;
  box-shadow: var(--app-shadow-1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-title-section {
  flex: 1;

  .app-section__title {
    font-size: 28px;
    font-weight: 800;
    color: #0f172a;
    margin-bottom: 6px;
  }

  .assessment-title {
    font-size: 16px;
    color: #334155;
    margin-bottom: 6px;
  }

  .completion-time {
    font-size: 13px;
    color: #64748b;
    font-weight: 500;
  }
}

.result-score-section {
  display: flex;
  gap: 20px;
  align-items: center;
}

.score-card,
.level-card,
.completion-card {
  text-align: center;
  min-width: 112px;
  padding: 10px 12px;
  border: 1px solid var(--app-border);
  border-radius: 12px;
  background: #f8fafc;

  .score-value,
  .completion-value {
    font-size: 34px;
    font-weight: 800;
    color: #0f172a;
    line-height: 1;

    span {
      font-size: 18px;
      color: #64748b;
    }
  }

  .score-label,
  .completion-label {
    font-size: 12px;
    color: #64748b;
    margin-top: 8px;
    font-weight: 600;

    span {
      font-size: 12px;
    }
  }
}

.level-card {
  :deep(.el-tag) {
    border-radius: 999px;
    font-weight: 700;
    padding: 0 12px;
  }

  .level-label {
    font-size: 12px;
    color: #64748b;
    margin-top: 8px;
    font-weight: 600;
  }
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.analysis-section,
.suggestions-section,
.statistics-section,
.actions-section {
  background: white;
  border-radius: var(--app-radius-lg);
  border: 1px solid var(--app-border);
  padding: 18px;
  box-shadow: var(--app-shadow-1);
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 14px;
  display: flex;
  align-items: center;

  i {
    margin-right: 8px;
    font-size: 18px;
    color: #334155;
  }
}

.analysis-content p {
  line-height: 1.7;
  color: #334155;
  font-size: 15px;
}

.suggestions-list {
  list-style: none;
  padding: 0;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  line-height: 1.6;
  color: #334155;

  i {
    color: #0f766e;
    margin-right: 8px;
    margin-top: 4px;
  }
}

.statistics-content {
  .stat-card {
    display: flex;
    align-items: center;
    padding: 16px;
    background: #f8fafc;
    border: 1px solid var(--app-border);
    border-radius: 12px;

    .stat-icon {
      width: 42px;
      height: 42px;
      border-radius: 50%;
      background: #0f172a;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;

      i {
        font-size: 18px;
        color: white;
      }
    }

    .stat-value {
      font-size: 18px;
      font-weight: 700;
      color: #0f172a;
    }

    .stat-label {
      font-size: 12px;
      color: #64748b;
      margin-top: 2px;
    }
  }
}

.actions-content {
  .action-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 14px;
  }

  .action-card {
    display: flex;
    align-items: center;
    padding: 14px;
    border: 1px solid var(--app-border);
    border-radius: 12px;
    background: #ffffff;
    transition: all var(--app-dur-fast) var(--app-ease-standard);

    &:hover {
      border-color: rgba(15, 23, 42, 0.24);
      box-shadow: var(--app-shadow-1);
      transform: translateY(-1px);
    }

    .action-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;

      i {
        font-size: 18px;
        color: white;
      }
    }

    .action-info {
      flex: 1;

      h4 {
        margin: 0 0 4px 0;
        font-size: 15px;
        color: #0f172a;
        font-weight: 700;
      }

      p {
        margin: 0;
        font-size: 13px;
        color: #64748b;
        line-height: 1.5;
      }
    }

    .el-button {
      height: 36px;
      border-radius: 10px;
      font-weight: 700;
      background: #0f172a;
      border-color: #0f172a;
    }
  }
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 18px;

  .el-button {
    min-width: 132px;
    height: 40px;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 700;
  }

  .el-button--primary {
    background: #0f172a;
    border-color: #0f172a;
  }

  .el-button--warning {
    background: #ffffff;
    border-color: var(--app-border);
    color: #0f172a;
  }
}

.save-success {
  text-align: center;
  padding: 20px;

  p {
    margin-top: 12px;
    font-size: 15px;
    color: #334155;
  }
}

@media (max-width: 768px) {
  .assessment-result-container {
    padding: 10px;
  }

  .result-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    text-align: left;
  }

  .result-score-section {
    width: 100%;
    flex-wrap: wrap;
    gap: 10px;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;

    .el-button {
      width: 100%;
      max-width: 300px;
    }
  }

  .actions-content .action-cards {
    grid-template-columns: 1fr;
  }

  .action-card {
    flex-direction: column;
    text-align: center;

    .action-icon {
      margin-right: 0;
      margin-bottom: 12px;
    }
  }
}
</style>
