<template>
  <div class="app-page expert-consultation-container">
    <div class="app-page__inner">
      <SubPageHeader
        title="专家咨询"
        subtitle="选择专业心理咨询师，获得个性化心理支持"
      />

      <div class="search-section app-card">
        <div class="app-card__body">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索专家姓名或擅长领域"
                clearable
                @clear="handleSearch"
                @keyup.enter="handleSearch"
              >
                <template #append>
                  <el-button @click="handleSearch">
                    <i class="el-icon-search"></i>
                  </el-button>
                </template>
              </el-input>
            </el-col>
            <el-col :span="8">
              <el-select
                v-model="selectedSpecialty"
                placeholder="选择擅长领域"
                clearable
                @change="handleSearch"
              >
                <el-option label="情绪管理" value="情绪管理" />
                <el-option label="人际关系" value="人际关系" />
                <el-option label="青少年心理" value="青少年心理" />
                <el-option label="婚姻家庭" value="婚姻家庭" />
                <el-option label="压力管理" value="压力管理" />
                <el-option label="创伤治疗" value="创伤治疗" />
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-select
                v-model="sortBy"
                placeholder="排序方式"
                @change="handleSearch"
              >
                <el-option label="评分最高" value="rating_desc" />
                <el-option label="经验最丰富" value="experience_desc" />
                <el-option label="价格最低" value="price_asc" />
              </el-select>
            </el-col>
          </el-row>
        </div>
      </div>

      <div class="experts-section">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <div v-else-if="experts.length === 0" class="empty-container app-empty">
          <div class="app-empty__icon el-icon-user"></div>
          <div class="app-empty__text">暂无符合条件的专家</div>
          <div class="app-empty__hint">请尝试调整搜索条件</div>
        </div>

        <div v-else class="experts-grid app-grid app-grid--3">
          <div
            v-for="expert in experts"
            :key="expert.id"
            class="expert-card app-card app-card--hover"
          >
            <div class="expert-header">
              <div class="expert-avatar">
                <el-avatar :size="80" :src="resolveAvatarUrl(expert.avatar) || defaultAvatar" />
              </div>
              <div class="expert-info">
                <h3 class="expert-name">{{ expert.realName }}</h3>
                <p class="expert-title">{{ expert.title }}</p>
                <div class="expert-rating">
                  <el-rate
                    v-model="expert.rating"
                    disabled
                    show-score
                    text-color="#ff9900"
                    score-template="{value} 分"
                  />
                </div>
              </div>
            </div>

            <div class="expert-details">
              <div class="detail-item">
                <i class="el-icon-medal"></i>
                <span>从业 {{ expert.experienceYears }} 年</span>
              </div>
              <div class="detail-item">
                <i class="el-icon-coin"></i>
                <span>¥{{ expert.hourlyRate }}/小时</span>
              </div>
              <div class="detail-item">
                <i class="el-icon-star"></i>
                <span>{{ expert.specialty }}</span>
              </div>
            </div>

            <div class="expert-intro">
              <p>{{ expert.introduction }}</p>
            </div>

            <div class="expert-actions">
              <el-button type="primary" @click="openConsultationDialog(expert)">
                <i class="el-icon-chat-dot-round"></i>
                预约咨询
              </el-button>
              <el-button @click="openExpertDetailDialog(expert)">
                <i class="el-icon-view"></i>
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <el-dialog
        v-model="showConsultationDialog"
        title="预约咨询"
        width="500px"
        :before-close="handleCloseDialog"
      >
        <div v-if="selectedExpert" class="consultation-dialog">
          <div class="expert-info">
            <el-avatar :size="60" :src="resolveAvatarUrl(selectedExpert.avatar) || defaultAvatar" />
            <div class="expert-details">
              <h4>{{ selectedExpert.realName }}</h4>
              <p>{{ selectedExpert.title }}</p>
              <p class="price">咨询费用: ¥{{ selectedExpert.hourlyRate }}/小时</p>
            </div>
          </div>

          <div class="consultation-form">
            <el-form :model="consultationForm" label-width="80px">
              <el-form-item label="咨询主题">
                <el-input
                  v-model="consultationForm.topic"
                  placeholder="请输入你希望咨询的主题"
                  type="textarea"
                  :rows="2"
                />
              </el-form-item>
              <el-form-item label="咨询描述">
                <el-input
                  v-model="consultationForm.description"
                  placeholder="请详细描述问题背景、困扰点和期望目标"
                  type="textarea"
                  :rows="4"
                  show-word-limit
                  maxlength="500"
                />
              </el-form-item>
              <el-form-item label="期望时间">
                <el-date-picker
                  v-model="consultationForm.preferredTime"
                  type="datetime"
                  placeholder="选择期望咨询时间"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-form-item>
              <el-form-item label="联系方式">
                <el-input
                  v-model="consultationForm.contactInfo"
                  placeholder="请输入手机号或微信号"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>

        <template #footer>
          <el-button @click="handleCloseDialog">取消</el-button>
          <el-button type="primary" @click="submitConsultation" :loading="submitting">
            提交预约
          </el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="showExpertDetailDialog"
        title="专家详情"
        width="600px"
        :before-close="handleCloseExpertDetail"
      >
        <div v-if="selectedExpertDetail" class="expert-detail-dialog">
          <div class="expert-header">
            <el-avatar :size="100" :src="resolveAvatarUrl(selectedExpertDetail.avatar) || defaultAvatar" />
            <div class="expert-basic-info">
              <h3>{{ selectedExpertDetail.realName }}</h3>
              <p class="expert-title">{{ selectedExpertDetail.title }}</p>
              <div class="expert-rating">
                <el-rate
                  v-model="selectedExpertDetail.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} 分"
                />
              </div>
            </div>
          </div>

          <div class="expert-details-section">
            <h4>基本信息</h4>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="detail-item">
                  <i class="el-icon-medal"></i>
                  <span>从业年限：{{ selectedExpertDetail.experienceYears }} 年</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <i class="el-icon-coin"></i>
                  <span>咨询费用：¥{{ selectedExpertDetail.hourlyRate }}/小时</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <i class="el-icon-star"></i>
                  <span>擅长领域：{{ selectedExpertDetail.specialty }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-item">
                  <i class="el-icon-message"></i>
                  <span>联系方式：{{ selectedExpertDetail.phone || '可通过平台联系' }}</span>
                </div>
              </el-col>
            </el-row>
          </div>

          <div class="expert-intro-section">
            <h4>专家介绍</h4>
            <div class="intro-content">
              {{ selectedExpertDetail.introduction || '该专家暂未提供详细介绍。' }}
            </div>
          </div>

          <div class="expert-contact-section">
            <h4>咨询说明</h4>
            <ul class="consultation-notes">
              <li>咨询前请准备好问题重点，提升沟通效率。</li>
              <li>请按预约时间准时参与，如需改期请提前联系。</li>
              <li>咨询过程中建议保持安静、稳定的网络环境。</li>
              <li>如有紧急情况请及时联系平台客服。</li>
            </ul>
          </div>
        </div>

        <template #footer>
          <el-button @click="handleCloseExpertDetail">关闭</el-button>
          <el-button type="primary" @click="openConsultationDialog(selectedExpertDetail)">
            立即预约咨询
          </el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import SubPageHeader from '@/components/SubPageHeader.vue'
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPsychologists, bookConsultation, createConsultationAppointment } from '@/api/consultation'

const API_BASE = 'http://localhost:8080'
const resolveAvatarUrl = (path) => {
  if (!path) return ''
  if (/^https?:\/\//.test(path) || path.startsWith('data:')) return path
  if (path.startsWith('/avatars/') || path.startsWith('avatars/')) return ''
  if (path.startsWith('/')) return API_BASE + path
  return `${API_BASE}/${path}`
}
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=expert&backgroundColor=1e90ff'

const searchKeyword = ref('')
const selectedSpecialty = ref('')
const sortBy = ref('rating_desc')

const experts = ref([])
const loading = ref(false)

const showConsultationDialog = ref(false)
const selectedExpert = ref(null)
const consultationForm = ref({
  topic: '',
  description: '',
  preferredTime: '',
  contactInfo: ''
})
const submitting = ref(false)

const showExpertDetailDialog = ref(false)
const selectedExpertDetail = ref(null)

const loadExperts = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (selectedSpecialty.value) params.specialty = selectedSpecialty.value
    if (sortBy.value) params.sortBy = sortBy.value

    const response = await getPsychologists(params)
    if (response.success) {
      experts.value = response.psychologists || []
      experts.value.forEach((expert) => {
        expert.rating = expert.rating || 5.0
        expert.experienceYears = expert.experienceYears || 5
        expert.hourlyRate = expert.hourlyRate || 200
        if (!expert.avatar) expert.avatar = defaultAvatar
      })
    } else {
      ElMessage.error('加载专家列表失败')
    }
  } catch (error) {
    console.error('加载专家列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadExperts()
}

const openConsultationDialog = (expert) => {
  selectedExpert.value = expert
  consultationForm.value = {
    topic: '',
    description: '',
    preferredTime: '',
    contactInfo: ''
  }
  showConsultationDialog.value = true
}

const handleCloseDialog = () => {
  showConsultationDialog.value = false
  selectedExpert.value = null
}

const submitConsultation = async () => {
  if (!consultationForm.value.topic.trim()) {
    ElMessage.warning('请输入咨询主题')
    return
  }
  if (!consultationForm.value.preferredTime) {
    ElMessage.warning('请选择期望咨询时间')
    return
  }
  if (!consultationForm.value.contactInfo.trim()) {
    ElMessage.warning('请输入联系方式')
    return
  }

  submitting.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id
    if (!userId) {
      ElMessage.error('请先登录')
      submitting.value = false
      return
    }

    let scheduledTime = consultationForm.value.preferredTime
    let preferredTime = consultationForm.value.preferredTime
    if (scheduledTime) {
      const dateObj = typeof scheduledTime === 'string' ? new Date(scheduledTime) : scheduledTime
      scheduledTime = dateObj.toISOString()
      preferredTime = dateObj.toISOString().replace('T', ' ').substring(0, 19)
    }

    const appointmentData = {
      userId,
      psychologistId: selectedExpert.value.id,
      topic: consultationForm.value.topic,
      preferredTime,
      contactInfo: consultationForm.value.contactInfo
    }

    const [consultationResponse, appointmentResponse] = await Promise.all([
      bookConsultation({
        userId,
        psychologistId: selectedExpert.value.id,
        title: consultationForm.value.topic,
        description: consultationForm.value.description || consultationForm.value.topic,
        consultationType: '在线咨询',
        scheduledTime,
        cost: selectedExpert.value.hourlyRate || 200,
        status: '待确认'
      }),
      createConsultationAppointment(appointmentData)
    ])

    if (consultationResponse.success && appointmentResponse.success) {
      ElMessage.success('预约提交成功')
      handleCloseDialog()
    } else {
      const errorMsg = consultationResponse.message || appointmentResponse.message || '预约提交失败，请稍后重试'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('预约提交失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const openExpertDetailDialog = (expert) => {
  selectedExpertDetail.value = expert
  showExpertDetailDialog.value = true
}

const handleCloseExpertDetail = () => {
  showExpertDetailDialog.value = false
  selectedExpertDetail.value = null
}

onMounted(() => {
  loadExperts()
})
</script>

<style lang="scss" scoped>
.expert-consultation-container {
  padding: 0;
  background: transparent;
  min-height: auto;
  position: relative;
}

.expert-consultation-container::before {
  content: none;
  z-index: -1;
}

.search-section {
  background: transparent;
  backdrop-filter: none;
  padding: 0;
  border-radius: 0;
  margin-bottom: 18px;
  box-shadow: none;
  border: none;

  .el-row {
    align-items: center;
  }
}

.experts-grid {
  display: grid;
  grid-template-columns: none;
  gap: 16px;
}

.expert-card {
  background: transparent;
  backdrop-filter: none;
  border-radius: 0;
  padding: 16px;
  box-shadow: none;
  border: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: rgba(37, 99, 235, 0.10);
    transition: left 0.6s;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 22px rgba(15, 23, 42, 0.1);

    &::before {
      left: 100%;
    }
  }
}

.expert-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;

  .expert-avatar {
    margin-right: 20px;

    .el-avatar {
      border: 2px solid #dbe2ea;
      box-shadow: none;
      transition: transform 0.2s ease;

      &:hover {
        transform: scale(1.1);
      }
    }
  }

  .expert-info {
    flex: 1;

    .expert-name {
      font-size: 18px;
      font-weight: 800;
      margin: 0 0 6px 0;
      color: #0f172a;
    }

    .expert-title {
      font-size: 13px;
      color: #64748b;
      margin: 0 0 10px 0;
      font-weight: 500;
    }
  }
}

.expert-details {
  margin-bottom: 10px;

  .detail-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    font-size: 13px;
    color: #334155;

    i {
      margin-right: 8px;
      color: #1d4ed8;
    }
  }
}

.expert-intro {
  margin-bottom: 12px;

  p {
    font-size: 13px;
    color: #475569;
    line-height: 1.7;
    margin: 0;
  }
}

.expert-actions {
  display: flex;
  gap: 10px;

  .el-button {
    flex: 1;
    border-radius: 9px;
    font-size: 13px;
    font-weight: 700;
    min-height: 34px;
    transition: all 0.2s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: rgba(255, 255, 255, 0.18);
      transition: left 0.6s;
    }

    &:hover {
      transform: translateY(-1px);
      box-shadow: none;

      &::before {
        left: 100%;
      }
    }

    &:first-child {
      background: #0f172a;
      border: 1px solid #0f172a;

      &:hover {
        background: #1e293b;
        border-color: #1e293b;
      }
    }

    &:last-child {
      background: #ffffff;
      color: #334155;
      border: 1px solid #d5deea;

      &:hover {
        background: #ffffff;
        border-color: #bfd0e6;
        color: #0f172a;
      }
    }
  }
}

.consultation-dialog {
  .expert-info {
    display: flex;
    align-items: center;
    margin-bottom: 14px;
    padding: 12px;
    background: #f8fafc;
    border: 1px solid #dbe2ea;
    border-radius: 12px;

    .el-avatar {
      border: 2px solid #dbe2ea;
      box-shadow: none;
    }

    .expert-details {
      margin-left: 20px;

      h4 {
        margin: 0 0 6px 0;
        font-size: 16px;
        font-weight: 800;
        color: #0f172a;
      }

      p {
        margin: 0 0 6px 0;
        color: #64748b;
        font-size: 13px;
      }

      .price {
        color: #0f766e;
        font-weight: 700;
        font-size: 14px;
      }
    }
  }
}

.loading-container,
.empty-container {
  text-align: center;
  padding: 60px 20px;
}

.expert-detail-dialog {
  .expert-header {
    display: flex;
    align-items: center;
    margin-bottom: 14px;
    padding: 16px;
    background: #f8fafc;
    border-radius: 12px;
    border: 1px solid #dbe2ea;
    box-shadow: none;
    position: relative;
    overflow: hidden;
    transition: all 0.4s ease;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: #2563eb;
    }

    .el-avatar {
      border: 2px solid #dbe2ea;
      box-shadow: none;
      transition: transform 0.2s ease;

      &:hover {
        transform: scale(1.05);
      }
    }

    .expert-basic-info {
      margin-left: 30px;

      h3 {
        font-size: 22px;
        font-weight: 800;
        color: #0f172a;
        margin: 0 0 8px 0;
      }

      .expert-title {
        font-size: 14px;
        color: #64748b;
        margin: 0 0 8px 0;
        font-weight: 600;
      }
    }
  }

  .expert-details-section {
    margin-bottom: 12px;
    padding: 14px;
    background: #ffffff;
    border-radius: 12px;
    border: 1px solid #dbe2ea;
    box-shadow: none;

    h4 {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
      margin-bottom: 10px;
      display: flex;
      align-items: center;

      &::before {
        content: '•';
        margin-right: 10px;
        font-size: 16px;
      }
    }

    .detail-item {
      display: flex;
      align-items: center;
      margin-bottom: 8px;
      font-size: 13px;
      color: #334155;
      padding: 8px 10px;
      background: #f8fafc;
      border-radius: 8px;
      transition: all 0.2s ease;

      i {
        margin-right: 10px;
        color: #1d4ed8;
        font-size: 14px;
        width: 20px;
        text-align: center;
      }
    }
  }

  .expert-intro-section {
    margin-bottom: 12px;
    padding: 14px;
    background: #ffffff;
    border-radius: 12px;
    border: 1px solid #dbe2ea;
    box-shadow: none;

    h4 {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
      margin-bottom: 10px;
      display: flex;
      align-items: center;

      &::before {
        content: '•';
        margin-right: 10px;
        font-size: 16px;
      }
    }

    .intro-content {
      background: #f8fafc;
      padding: 10px 12px;
      border-radius: 10px;
      line-height: 1.7;
      color: #334155;
      font-size: 13px;
      border-left: 3px solid #94a3b8;
      box-shadow: none;
    }
  }

  .expert-contact-section {
    padding: 14px;
    background: #ffffff;
    border-radius: 12px;
    border: 1px solid #dbe2ea;
    box-shadow: none;

    h4 {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
      margin-bottom: 10px;
      display: flex;
      align-items: center;

      &::before {
        content: '•';
        margin-right: 10px;
        font-size: 16px;
      }
    }

    .consultation-notes {
      background: #f8fafc;
      padding: 10px 12px;
      border-radius: 10px;
      border-left: 3px solid #94a3b8;
      box-shadow: none;

      li {
        margin-bottom: 10px;
        color: #334155;
        line-height: 1.6;
        font-size: 13px;
        padding-left: 10px;
        position: relative;

        &::before {
          content: '•';
          position: absolute;
          left: -5px;
          color: #64748b;
          font-weight: bold;
        }

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .expert-consultation-container {
    padding: 10px;
  }

  .experts-grid {
    grid-template-columns: 1fr;
  }

  .expert-header {
    flex-direction: column;
    text-align: center;

    .expert-avatar {
      margin-right: 0;
      margin-bottom: 12px;
    }
  }

  .expert-actions {
    flex-direction: column;
  }

  .expert-detail-dialog {
    .expert-header {
      flex-direction: column;
      text-align: center;

      .expert-basic-info {
        margin-left: 0;
        margin-top: 16px;
      }
    }
  }
}
</style>
