<template>
  <div class="app-page app-page--wide assessment-container">
    <div class="app-page__inner">
      <SubPageHeader
        title="心理测评中心"
        subtitle="专业量表评估，帮助你更好地了解当前心理状态"
      />

      <div class="category-filter">
        <el-radio-group v-model="activeCategory">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button
            v-for="category in categories"
            :key="category"
            :label="category"
          >
            {{ category }}
          </el-radio-button>
        </el-radio-group>
      </div>

      <div class="assessment-list">
        <div
          v-for="assessment in filteredAssessments"
          :key="assessment.id"
          class="assessment-item"
        >
          <div class="assessment-info">
            <div class="assessment-item-header">
              <h3 class="assessment-title">{{ assessment.title }}</h3>
              <el-tag :type="getCategoryTagType(assessment.category)">
                {{ assessment.category }}
              </el-tag>
            </div>

            <p class="assessment-description">{{ assessment.description }}</p>

            <div class="assessment-meta">
              <div class="meta-item">
                <i class="el-icon-time"></i>
                <span>约 {{ assessment.estimatedTime }} 分钟</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-document"></i>
                <span>{{ assessment.questionsCount }} 题</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-user"></i>
                <span>{{ assessment.participantsCount }} 人参与</span>
              </div>
            </div>

            <div class="assessment-rating">
              <el-rate
                v-model="assessment.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value} 分"
              />
            </div>
          </div>

          <div class="assessment-actions">
            <el-button type="primary" size="large" @click="startAssessment(assessment.id)">
              开始测评
            </el-button>
            <el-button size="large" @click="viewDetails(assessment.id)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>

      <el-dialog
        v-model="showDetailDialog"
        :title="currentAssessment?.title || '测评详情'"
        width="600px"
      >
        <div v-if="currentAssessment" class="assessment-detail">
          <div class="detail-section">
            <h4>测评介绍</h4>
            <p>{{ currentAssessment.description }}</p>
          </div>

          <div class="detail-section">
            <h4>适用人群</h4>
            <p>{{ currentAssessment.suitableFor }}</p>
          </div>

          <div class="detail-section">
            <h4>测评说明</h4>
            <ul class="instruction-list">
              <li v-for="instruction in currentAssessment.instructions" :key="instruction">
                {{ instruction }}
              </li>
            </ul>
          </div>

          <div class="detail-meta">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="meta-card">
                  <div class="meta-label">题目数量</div>
                  <div class="meta-value">{{ currentAssessment.questionsCount }} 题</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="meta-card">
                  <div class="meta-label">预计时长</div>
                  <div class="meta-value">{{ currentAssessment.estimatedTime }} 分钟</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="meta-card">
                  <div class="meta-label">参与人数</div>
                  <div class="meta-value">{{ currentAssessment.participantsCount }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>

        <template #footer>
          <el-button @click="showDetailDialog = false">取消</el-button>
          <el-button type="primary" @click="startAssessment(currentAssessment?.id)">
            开始测评
          </el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import SubPageHeader from '@/components/SubPageHeader.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const activeCategory = ref('')
const showDetailDialog = ref(false)
const currentAssessment = ref(null)

const categories = ref(['抑郁', '焦虑', '压力', '人格', '情绪', '睡眠', '人际关系'])
const assessments = ref([])

const filteredAssessments = computed(() => {
  if (!activeCategory.value) return assessments.value
  return assessments.value.filter(item => item.category === activeCategory.value)
})

const getCategoryTagType = (category) => {
  const typeMap = {
    抑郁: 'danger',
    焦虑: 'warning',
    压力: 'info',
    人格: 'success',
    情绪: 'primary',
    睡眠: '',
    人际关系: 'success'
  }
  return typeMap[category] || 'info'
}

const startAssessment = (assessmentId) => {
  if (!assessmentId) return
  router.push(`/assessment/taking/${assessmentId}`)
  showDetailDialog.value = false
}

const viewDetails = (assessmentId) => {
  currentAssessment.value = assessments.value.find(item => item.id === assessmentId) || null
  showDetailDialog.value = true
}

const getDefaultAssessments = () => [
  {
    id: 1,
    title: '抑郁自评量表',
    category: '抑郁',
    description: '评估个体最近一周的抑郁情绪与相关症状水平。',
    questionsCount: 20,
    participantsCount: 0,
    estimatedTime: 10,
    rating: 5.0,
    suitableFor: '18岁及以上成年人',
    instructions: ['请根据最近一周的感受作答', '选择最符合你情况的选项', '所有题目都需要作答']
  },
  {
    id: 2,
    title: '焦虑自评量表',
    category: '焦虑',
    description: '评估焦虑症状程度，帮助识别与管理焦虑问题。',
    questionsCount: 20,
    participantsCount: 0,
    estimatedTime: 10,
    rating: 5.0,
    suitableFor: '18岁及以上成年人',
    instructions: ['请根据最近一周的感受作答', '选择最符合你情况的选项', '所有题目都需要作答']
  }
]

const loadAssessments = async () => {
  try {
    const response = await fetch('http://localhost:8080/assessments/list')
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.code !== 200) throw new Error(result.message || '获取测评数据失败')

    const data = result.data || []
    assessments.value = data.map(assessment => ({
      id: assessment.id || assessment.assessmentId || 0,
      title: assessment.title || assessment.assessmentName || '心理测评',
      category: assessment.category || '通用',
      description: assessment.description || '专业心理测评量表。',
      questionsCount: assessment.questionsCount || assessment.questionCount || 0,
      participantsCount: assessment.participantsCount || assessment.participantCount || 0,
      estimatedTime: assessment.estimatedTime || 10,
      rating: assessment.rating || 5,
      suitableFor: assessment.suitableFor || '18岁及以上成年人',
      instructions: assessment.instructions || ['请根据最近一周的感受作答', '选择最符合你情况的选项', '所有题目都需要作答']
    }))
  } catch (error) {
    assessments.value = getDefaultAssessments()
    ElMessage.error(`加载测评数据失败: ${error.message || error}`)
  }
}

onMounted(async () => {
  await loadAssessments()
})
</script>

<style lang="scss" scoped>
.assessment-container {
  padding: 0;
  background: transparent;
  min-height: auto;
}

.assessment-container::before {
  content: none;
}

.category-filter {
  margin-bottom: 16px;
  text-align: center;

  .el-radio-group {
    display: inline-flex;
    flex-wrap: wrap;
    gap: 15px;
    justify-content: center;
    background: white;
    padding: 10px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
    border: 1px solid #dbe2ea;

    :deep(.el-radio-button) {
      .el-radio-button__inner {
        border-radius: 999px;
        border: 1px solid #d8e0ea;
        background: white;
        color: #666;
        font-weight: 700;
        font-size: 12px;
        padding: 8px 14px;
        transition: all 0.3s ease;

        &:hover {
          border-color: #334155;
          color: #334155;
        }
      }

      &.is-active .el-radio-button__inner {
        background: #1e293b;
        border-color: #1e293b;
        color: #f8fafc;
        box-shadow: none;
      }
    }
  }
}

.assessment-list {
  display: grid;
  gap: 12px;
}

.assessment-item {
  background: white;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  transition: all 0.2s ease;
  border: 1px solid #dbe2ea;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 4px;
    background: #334155;
    transform: scaleX(0);
    transition: transform 0.4s ease;
  }

  &:hover {
    box-shadow: 0 10px 22px rgba(15, 23, 42, 0.1);
    transform: translateY(-2px);

    &::before {
      transform: scaleX(1);
    }
  }
}

.assessment-info {
  flex: 1;
  margin-right: 25px;
}

.assessment-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;

  .assessment-title {
    font-size: 17px;
    font-weight: 800;
    color: #0f172a;
    margin: 0;
    flex: 1;
    margin-right: 15px;
    line-height: 1.4;
  }
}

.assessment-description {
  color: #64748b;
  line-height: 1.7;
  margin-bottom: 10px;
  font-size: 13px;
}

.assessment-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;

  .meta-item {
    display: flex;
    align-items: center;
    color: #64748b;
    font-size: 12px;
    font-weight: 700;

    i {
      margin-right: 6px;
      font-size: 14px;
      color: #1d4ed8;
    }

    span {
      background: #f8fafc;
      border: 1px solid #dbe2ea;
      padding: 4px 8px;
      border-radius: 12px;
      font-weight: 600;
    }
  }
}

.assessment-rating {
  :deep(.el-rate) {
    display: inline-flex;
    align-items: center;

    .el-rate__icon {
      font-size: 18px;
    }

    .el-rate__text {
      font-weight: 600;
      color: #f39c12;
      margin-left: 8px;
    }
  }
}

.assessment-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 160px;

  .el-button {
    border-radius: 9px;
    font-size: 13px;
    font-weight: 700;
    min-height: 34px;
    padding: 0 14px;
    transition: all 0.2s ease;

    &.el-button--primary {
      border: 1px solid #0f172a;
      background: #0f172a;
      box-shadow: none;

      &:hover {
        transform: translateY(-1px);
        box-shadow: none;
        border-color: #1e293b;
        background: #1e293b;
      }
    }

    &.el-button--default {
      border: 1px solid #d5deea;
      color: #334155;
      background: #ffffff;

      &:hover {
        background: #ffffff;
        border-color: #bfd0e6;
        transform: translateY(-1px);
      }
    }
  }
}

.assessment-detail {
  .detail-section {
    margin-bottom: 25px;

    h4 {
      font-size: 18px;
      font-weight: 700;
      color: #2c3e50;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 2px solid #ecf0f1;
    }

    p {
      color: #7f8c8d;
      line-height: 1.7;
      margin: 0;
      font-size: 15px;
    }
  }

  .instruction-list {
    color: #7f8c8d;
    padding-left: 20px;

    li {
      margin-bottom: 6px;
      line-height: 1.6;
      position: relative;

      &::before {
        content: '•';
        color: #1e90ff;
        font-weight: bold;
        margin-right: 8px;
      }
    }
  }
}

.detail-meta {
  margin-top: 30px;

  .meta-card {
    text-align: center;
    padding: 20px;
    background: #f3f4f6;
    border-radius: 12px;
    border: 1px solid rgba(30, 144, 255, 0.1);
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-3px);
    }

    .meta-label {
      font-size: 13px;
      color: #95a5a6;
      margin-bottom: 6px;
      font-weight: 500;
    }

    .meta-value {
      font-size: 20px;
      font-weight: 700;
      color: #1e90ff;
    }
  }
}

@media (max-width: 768px) {
  .assessment-item {
    flex-direction: column;
    align-items: stretch;
    padding: 20px;
  }

  .assessment-info {
    margin-right: 0;
    margin-bottom: 20px;
  }

  .assessment-actions {
    flex-direction: row;
    justify-content: space-between;
    min-width: auto;
  }

  .assessment-meta {
    flex-wrap: wrap;
    gap: 15px;
  }

  .category-filter .el-radio-group {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .assessment-meta {
    flex-direction: column;
    gap: 10px;
  }

  .assessment-actions {
    flex-direction: column;
  }
}
</style>
