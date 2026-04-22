<template>
  <div class="app-page assessment-taking-container">
    <div class="app-page__inner">
      <SubPageHeader
        title="正在测评"
        subtitle="请根据最近一周的真实感受作答"
      />

      <div class="assessment-header">
        <div class="progress-section">
          <div class="progress-info">
            <span class="progress-text">进度: {{ currentQuestionIndex + 1 }}/{{ questions.length }}</span>
            <el-progress
              :percentage="progressPercentage"
              :stroke-width="8"
              :show-text="false"
            />
          </div>
          <h1 class="assessment-title">{{ assessmentTitle }}</h1>
        </div>

        <div class="time-section">
          <el-tag type="info" size="large">
            <i class="el-icon-time"></i>
            用时: {{ formatTime(elapsedTime) }}
          </el-tag>
        </div>
      </div>

      <div class="question-container" v-if="currentQuestion">
        <div class="question-card">
          <div class="question-header">
            <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
            <el-tag v-if="currentQuestion.questionType === 'single'" type="primary">单选题</el-tag>
            <el-tag v-else-if="currentQuestion.questionType === 'multiple'" type="warning">多选题</el-tag>
            <el-tag v-else type="success">量表题</el-tag>
          </div>

          <div class="question-content">
            <h3 class="question-text">{{ currentQuestion.questionText }}</h3>

            <div v-if="currentQuestion.questionType === 'single'" class="options-section">
              <el-radio-group v-model="currentAnswer" @change="onAnswerChange" class="options-list">
                <el-radio
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  :label="index"
                  class="option-item"
                >
                  <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
                  <span class="option-text">{{ option }}</span>
                </el-radio>
              </el-radio-group>
            </div>

            <div v-else-if="currentQuestion.questionType === 'multiple'" class="options-section">
              <el-checkbox-group v-model="currentAnswers" @change="onAnswerChange" class="options-list">
                <el-checkbox
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  :label="index"
                  class="option-item"
                >
                  <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
                  <span class="option-text">{{ option }}</span>
                </el-checkbox>
              </el-checkbox-group>
            </div>

            <div v-else class="scale-section">
              <div class="scale-options">
                <el-radio-group v-model="currentAnswer" @change="onAnswerChange" class="scale-list">
                  <el-radio
                    v-for="(option, index) in currentQuestion.options"
                    :key="index"
                    :label="index"
                    class="scale-option"
                  >
                    <div class="scale-content">
                      <div class="scale-value">{{ index + 1 }}</div>
                      <div class="scale-text">{{ option }}</div>
                    </div>
                  </el-radio>
                </el-radio-group>
              </div>

              <div class="scale-labels">
                <span class="scale-min">{{ scaleLabels.min }}</span>
                <span class="scale-max">{{ scaleLabels.max }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="navigation-buttons">
        <el-button
          :disabled="currentQuestionIndex === 0"
          @click="goToPreviousQuestion"
          size="large"
        >
          <i class="el-icon-arrow-left"></i>
          上一题
        </el-button>

        <el-button
          v-if="currentQuestionIndex < questions.length - 1"
          type="primary"
          @click="goToNextQuestion"
          size="large"
          :disabled="!isCurrentQuestionAnswered"
        >
          下一题
          <i class="el-icon-arrow-right"></i>
        </el-button>

        <el-button
          v-else
          type="success"
          @click="submitAssessment"
          size="large"
          :disabled="!isCurrentQuestionAnswered"
        >
          <i class="el-icon-check"></i>
          提交测评
        </el-button>
      </div>

      <div class="question-nav-panel">
        <div class="nav-header">
          <h4>题目导航</h4>
          <span>{{ answeredCount }}/{{ questions.length }} 已答</span>
        </div>

        <div class="question-grid">
          <div
            v-for="(question, index) in questions"
            :key="index"
            :class="['question-dot', {
              current: index === currentQuestionIndex,
              answered: answers[index] !== undefined && answers[index] !== null,
              unanswered: answers[index] === undefined || answers[index] === null
            }]"
            @click="goToQuestion(index)"
          >
            {{ index + 1 }}
          </div>
        </div>
      </div>

      <el-dialog
        v-model="showSubmitDialog"
        title="确认提交测评"
        width="400px"
      >
        <div class="submit-confirm">
          <p>提交后将生成测评报告，答题内容将无法修改。</p>
          <p class="submit-tip">是否确认提交？</p>
        </div>

        <template #footer>
          <el-button @click="showSubmitDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmit">确认提交</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import SubPageHeader from '@/components/SubPageHeader.vue'
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const assessmentId = ref(route.params.id)
const assessmentTitle = ref('心理测评')
const questions = ref([])
const currentQuestionIndex = ref(0)
const answers = ref({})
const currentAnswer = ref('')
const currentAnswers = ref([])
const elapsedTime = ref(0)
const timer = ref(null)
const showSubmitDialog = ref(false)

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])

const progressPercentage = computed(() => {
  if (!questions.value.length) return 0
  return ((currentQuestionIndex.value + 1) / questions.value.length) * 100
})

const answeredCount = computed(() => {
  return Object.values(answers.value).filter(answer =>
    answer !== undefined && answer !== null && answer !== ''
  ).length
})

const isCurrentQuestionAnswered = computed(() => {
  if (currentQuestion.value?.questionType === 'multiple') {
    return currentAnswers.value.length > 0
  }
  return currentAnswer.value !== undefined && currentAnswer.value !== null && currentAnswer.value !== ''
})

const scaleLabels = computed(() => ({ min: '从不', max: '总是' }))

const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

const loadQuestions = async () => {
  try {
    const response = await fetch(`http://localhost:8080/assessments/${assessmentId.value}/questions`)
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.code !== 200) throw new Error(result.message || '获取题目失败')

    questions.value = result.data || []

    const assessmentResponse = await fetch(`http://localhost:8080/assessments/${assessmentId.value}`)
    const assessmentResult = await assessmentResponse.json()
    assessmentTitle.value = assessmentResult.code === 200 ? assessmentResult.data.title : '心理测评'

    questions.value.forEach((question, index) => {
      answers.value[index] = undefined
      if (!Array.isArray(question.options)) question.options = []
    })
  } catch (error) {
    ElMessage.error(`加载题目失败: ${error.message || error}`)
    questions.value = getDefaultQuestions()
    questions.value.forEach((_, index) => {
      answers.value[index] = undefined
    })
  }
}

const getDefaultQuestions = () => ([
  {
    id: 1,
    questionNumber: 1,
    questionText: '我感到情绪低落或消沉。',
    questionType: 'scale',
    options: ['从不', '偶尔', '经常', '总是']
  },
  {
    id: 2,
    questionNumber: 2,
    questionText: '我近期容易紧张、焦虑。',
    questionType: 'scale',
    options: ['从不', '偶尔', '经常', '总是']
  },
  {
    id: 3,
    questionNumber: 3,
    questionText: '我能较好地调节自己的情绪。',
    questionType: 'scale',
    options: ['从不', '偶尔', '经常', '总是']
  }
])

const goToPreviousQuestion = () => {
  if (currentQuestionIndex.value <= 0) return
  saveCurrentAnswer()
  currentQuestionIndex.value--
  loadCurrentAnswer()
}

const goToNextQuestion = () => {
  if (currentQuestionIndex.value >= questions.value.length - 1) return
  saveCurrentAnswer()
  currentQuestionIndex.value++
  loadCurrentAnswer()
}

const goToQuestion = (index) => {
  saveCurrentAnswer()
  currentQuestionIndex.value = index
  loadCurrentAnswer()
}

const saveCurrentAnswer = () => {
  if (currentQuestion.value?.questionType === 'multiple') {
    answers.value[currentQuestionIndex.value] = [...currentAnswers.value]
  } else {
    answers.value[currentQuestionIndex.value] = currentAnswer.value
  }
}

const onAnswerChange = () => {
  saveCurrentAnswer()
}

const loadCurrentAnswer = () => {
  const savedAnswer = answers.value[currentQuestionIndex.value]
  if (currentQuestion.value?.questionType === 'multiple') {
    currentAnswers.value = Array.isArray(savedAnswer) ? [...savedAnswer] : []
    currentAnswer.value = ''
  } else {
    currentAnswer.value = savedAnswer ?? ''
    currentAnswers.value = []
  }
}

const submitAssessment = () => {
  saveCurrentAnswer()
  showSubmitDialog.value = true
}

const confirmSubmit = async () => {
  try {
    const unansweredQuestions = questions.value.filter((_, index) =>
      answers.value[index] === undefined || answers.value[index] === null || answers.value[index] === ''
    )

    if (unansweredQuestions.length > 0) {
      await ElMessageBox.confirm(
        `还有 ${unansweredQuestions.length} 题未作答，确认提交吗？`,
        '确认提交',
        {
          confirmButtonText: '确认提交',
          cancelButtonText: '继续作答',
          type: 'warning'
        }
      )
    }

    await doSubmit()
  } catch {
    showSubmitDialog.value = false
  }
}

const doSubmit = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (!userInfo.id) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const answersMap = {}
    questions.value.forEach((question, index) => {
      const answer = answers.value[index]
      if (answer === undefined || answer === null || answer === '') return
      answersMap[question.id] = Array.isArray(answer) ? answer.join(',') : String(answer)
    })

    const submission = {
      userId: userInfo.id,
      answers: answersMap,
      timeSpent: elapsedTime.value
    }

    const response = await fetch(`http://localhost:8080/assessments/${assessmentId.value}/submit`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(submission)
    })

    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success('提交成功，正在生成报告...')
      setTimeout(() => {
        router.push(`/assessment/result/${assessmentId.value}`)
      }, 1200)
    } else {
      ElMessage.error('提交失败: ' + result.message)
    }
  } catch (error) {
    ElMessage.error('提交失败，请重试')
    console.error('提交失败:', error)
  }
}

const startTimer = () => {
  timer.value = setInterval(() => {
    elapsedTime.value++
  }, 1000)
}

const stopTimer = () => {
  if (!timer.value) return
  clearInterval(timer.value)
  timer.value = null
}

onMounted(() => {
  loadQuestions()
  startTimer()
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style lang="scss" scoped>
.assessment-taking-container {
  padding: 0;
}

.assessment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  background: white;
  padding: 18px;
  border-radius: var(--app-radius-lg);
  border: 1px solid var(--app-border);
  box-shadow: var(--app-shadow-1);
}

.progress-section {
  flex: 1;

  .progress-info {
    margin-bottom: 10px;

    .progress-text {
      font-size: 14px;
      color: #64748b;
      margin-bottom: 8px;
      display: block;
    }
  }

  .assessment-title {
    font-size: 24px;
    font-weight: 800;
    color: #0f172a;
    margin: 0;
  }
}

.time-section {
  .el-tag {
    font-size: 13px;
    font-weight: 700;
    border-radius: 999px;
    padding: 6px 14px;
  }
}

.question-container {
  margin-bottom: 16px;
}

.question-card {
  background: white;
  border-radius: var(--app-radius-lg);
  padding: 18px;
  border: 1px solid var(--app-border);
  box-shadow: var(--app-shadow-1);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--app-border);

  .question-number {
    font-size: 16px;
    font-weight: 700;
    color: #334155;
  }
}

.question-content {
  .question-text {
    font-size: 20px;
    line-height: 1.7;
    color: #0f172a;
    margin-bottom: 14px;
    text-align: left;
  }
}

.options-section {
  .options-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
  }

  .option-item {
    display: flex;
    align-items: flex-start;
    padding: 14px 16px;
    border: 1px solid var(--app-border);
    border-radius: 10px;
    transition: all var(--app-dur-fast) var(--app-ease-standard);
    margin: 0;

    &:hover {
      border-color: rgba(15, 23, 42, 0.24);
      background: #f8fafc;
    }

    .option-label {
      font-weight: bold;
      margin-right: 10px;
      min-width: 30px;
      color: #0f172a;
    }

    .option-text {
      flex: 1;
      line-height: 1.6;
      color: #334155;
    }
  }
}

.scale-section {
  .scale-options {
    margin-bottom: 15px;
  }

  .scale-list {
    display: flex;
    justify-content: space-between;
    width: 100%;
  }

  .scale-option {
    flex: 1;
    margin: 0 5px;

    .scale-content {
      text-align: center;
      padding: 15px;
      border: 1px solid var(--app-border);
      border-radius: 10px;
      transition: all var(--app-dur-fast) var(--app-ease-standard);

      .scale-value {
        font-size: 18px;
        font-weight: bold;
        color: #0f172a;
        margin-bottom: 5px;
      }

      .scale-text {
        font-size: 12px;
        color: #64748b;
        line-height: 1.3;
      }
    }

    &:hover .scale-content {
      border-color: rgba(15, 23, 42, 0.24);
      background: #f8fafc;
    }
  }

  .scale-labels {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
    color: #64748b;
    font-size: 12px;
  }
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .el-button {
    min-width: 124px;
    height: 40px;
    padding: 0 16px;
    font-size: 14px;
    font-weight: 700;
    border-radius: 10px;
  }
}

.question-nav-panel {
  background: white;
  border-radius: var(--app-radius-lg);
  padding: 18px;
  border: 1px solid var(--app-border);
  box-shadow: var(--app-shadow-1);

  .nav-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    h4 {
      margin: 0;
      color: #0f172a;
      font-weight: 800;
    }

    span {
      color: #64748b;
      font-size: 12px;
      font-weight: 600;
    }
  }

  .question-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
    gap: 10px;
  }

  .question-dot {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all var(--app-dur-fast) var(--app-ease-standard);
    font-size: 14px;
    font-weight: 700;

    &.current {
      background: #0f172a;
      color: white;
      transform: scale(1.1);
    }

    &.answered {
      background: #0f766e;
      color: white;
    }

    &.unanswered {
      background: #f8fafc;
      color: #64748b;
      border: 1px solid var(--app-border);
    }

    &:hover {
      transform: scale(1.05);
    }
  }
}

.submit-confirm {
  text-align: center;

  p {
    margin: 10px 0;
    line-height: 1.6;
  }

  .submit-tip {
    color: #f56c6c;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .assessment-taking-container {
    padding: 10px;
  }

  .assessment-header {
    flex-direction: column;
    gap: 15px;
  }

  .question-card {
    padding: 20px;
  }

  .scale-list {
    flex-direction: column;
    gap: 10px;
  }

  .scale-option {
    margin: 0;
  }

  .navigation-buttons {
    flex-direction: column;
    gap: 10px;

    .el-button {
      width: 100%;
    }
  }
}
</style>
