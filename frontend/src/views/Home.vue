<template>
  <div class="app-page app-page--wide home-container">
    <div class="app-page__inner">
    <!-- 顶部导航栏 -->
    <nav class="top-navbar">
      <div class="navbar-content">
        <div class="navbar-logo">
          <i class="el-icon-star-off"></i>
          <span class="logo-text">星语航海</span>
        </div>
        <div class="navbar-menu">
          <a class="menu-item" @click="scrollToSection('consultations')">
            <i class="el-icon-view"></i>
            <span>咨询进程</span>
          </a>
          <a class="menu-item" @click="$router.push('/assessment')">
            <i class="el-icon-document-checked"></i>
            <span>心理测评</span>
          </a>
          <a class="menu-item" @click="$router.push('/expert-consultation')">
            <i class="el-icon-user-solid"></i>
            <span>专家咨询</span>
          </a>
          <a class="menu-item" @click="$router.push('/message-bottle')">
            <i class="el-icon-chat-line-square"></i>
            <span>漂流瓶</span>
          </a>
          <a class="menu-item" @click="$router.push('/ai-interaction')">
            <i class="el-icon-cpu"></i>
            <span>AI互动</span>
          </a>
          <a class="menu-item" @click="$router.push('/profile')">
            <i class="el-icon-user"></i>
            <span>个人中心</span>
          </a>
          <a class="menu-item" @click="$router.push('/contact')">
            <i class="el-icon-phone"></i>
            <span>联系我们</span>
          </a>
        </div>
        <div class="navbar-user">
          <span class="user-name">{{ userInfo?.username || '用户' }}</span>
          <el-button type="danger" size="small" @click="handleLogout" class="logout-btn">
            退出登录
          </el-button>
        </div>
      </div>
    </nav>

    <!-- 轮播图 -->
    <section class="banner-section-top">
      <el-carousel height="600px" :interval="5000" arrow="always" indicator-position="inside">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
            <div class="banner-overlay"></div>
            <div class="banner-content">
              <h2 class="banner-title">{{ item.title }}</h2>
              <p class="banner-desc">{{ item.description }}</p>
              <el-button type="primary" size="large" class="banner-btn" @click="item.action">
                <i class="el-icon-right"></i>
                {{ item.buttonText }}
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 咨询进程区域 -->
    <section id="consultations" class="consultations-section app-section">
      <div class="app-section__head">
        <div>
          <h2 class="app-section__title">我的咨询进程</h2>
          <p class="app-section__desc">实时查看您的咨询记录，并对专家提供反馈</p>
        </div>
      </div>

      <div class="consultations-container">
        <el-empty v-if="myConsultations.length === 0" description="暂无咨询记录"></el-empty>

        <div v-else class="consultation-cards">
          <div v-for="consultation in myConsultations" :key="consultation.id" class="consultation-card">
            <div class="card-header">
              <div class="consult-info">
                <h3 class="consult-title">{{ consultation.title }}</h3>
                <div class="psychologist-info">
                  <i class="el-icon-user"></i>
                  <span>专家：{{ consultation.psychologistName }}</span>
                </div>
              </div>
              <el-tag :type="getConsultStatusType(consultation.status)">
                {{ consultation.status }}
              </el-tag>
            </div>

            <div class="card-body">
              <div class="consult-detail">
                <div class="detail-item">
                  <i class="el-icon-time"></i>
                  <span>咨询时间：{{ formatConsultTime(consultation.scheduledTime) }}</span>
                </div>
                <div class="detail-item" v-if="consultation.consultationType">
                  <i class="el-icon-folder"></i>
                  <span>咨询类型：{{ consultation.consultationType }}</span>
                </div>
                <div class="detail-item" v-if="consultation.duration">
                  <i class="el-icon-timer"></i>
                  <span>咨询时长：{{ consultation.duration }} 分钟</span>
                </div>
                <div class="detail-item" v-if="consultation.cost">
                  <i class="el-icon-coin"></i>
                  <span>咨询费用：¥{{ consultation.cost }}</span>
                </div>
                <div class="detail-item" v-if="consultation.description">
                  <i class="el-icon-document"></i>
                  <span>描述：{{ consultation.description }}</span>
                </div>
              </div>

              <div class="feedback-section" v-if="consultation.status === '已完成'">
                <div class="my-feedback" v-if="consultation.userRating">
                  <div class="feedback-title">我的反馈</div>
                  <el-rate v-model="consultation.userRating" disabled size="large"></el-rate>
                  <p class="feedback-text">{{ consultation.userFeedback }}</p>
                </div>
                <div class="feedback-actions" v-else>
                  <el-button type="primary" size="small" @click="openFeedbackDialog(consultation)">
                    <i class="el-icon-edit"></i>
                    提供反馈
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 反馈对话框 -->
    <el-dialog
      v-model="showFeedbackDialog"
      title="咨询反馈"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="feedbackForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="feedbackForm.rating" size="large" show-text></el-rate>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="feedbackForm.feedback"
            type="textarea"
            :rows="5"
            placeholder="请输入您对本次咨询的反馈..."
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showFeedbackDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFeedback" :loading="submittingFeedback">
          提交反馈
        </el-button>
      </template>
    </el-dialog>

    <!-- 平台优势 -->
    <section class="advantages-section app-section">
      <div class="app-section__head">
        <div>
          <h2 class="app-section__title">星语航海平台优势</h2>
          <p class="app-section__desc">我们致力于为您提供最专业、贴心的心理健康服务</p>
        </div>
      </div>

      <div class="advantages-grid">
        <article
          v-for="(advantage, index) in advantages"
          :key="advantage.id"
          class="advantage-card"
          :style="{ '--advantage-accent': advantage.color }"
        >
          <div class="advantage-body">
            <span class="advantage-index">{{ String(index + 1).padStart(2, '0') }}</span>
            <h3 class="advantage-title">{{ advantage.title }}</h3>
            <p class="advantage-desc">{{ advantage.description }}</p>
          </div>
          <figure class="advantage-media">
            <img :src="advantage.image" :alt="advantage.title" loading="lazy" />
          </figure>
        </article>
      </div>
    </section>

    <!-- 快速入口 -->
    <section class="quick-access-section">
      <div class="app-section__head">
        <div>
          <h2 class="app-section__title">快速入口</h2>
          <p class="app-section__desc">选择您需要的服务，开启心灵之旅</p>
        </div>
      </div>

      <div class="quick-access-grid">
        <div class="access-card" @click="$router.push('/assessment')">
          <div class="access-icon assessment">
            <el-icon><DocumentChecked /></el-icon>
          </div>
          <h3>心理测评</h3>
          <p>专业心理量表评估</p>
          <div class="access-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="access-card" @click="$router.push('/expert-consultation')">
          <div class="access-icon expert">
            <el-icon><UserFilled /></el-icon>
          </div>
          <h3>专家咨询</h3>
          <p>专业心理咨询师</p>
          <div class="access-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="access-card" @click="$router.push('/message-bottle')">
          <div class="access-icon bottle">
            <el-icon><ChatLineSquare /></el-icon>
          </div>
          <h3>漂流瓶</h3>
          <p>心情分享与交流</p>
          <div class="access-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="access-card" @click="$router.push('/ai-interaction')">
          <div class="access-icon ai">
            <el-icon><Cpu /></el-icon>
          </div>
          <h3>AI互动</h3>
          <p>智能心理助手</p>
          <div class="access-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="access-card" @click="$router.push('/contact')">
          <div class="access-icon contact">
            <el-icon><PhoneFilled /></el-icon>
          </div>
          <h3>联系我们</h3>
          <p>提交留言与团队联系</p>
          <div class="access-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门测评 -->
    <section class="assessment-section">
      <div class="app-section__head">
        <div>
          <h2 class="app-section__title">热门心理测评</h2>
          <p class="app-section__desc">专业量表，科学评估您的心理健康状况</p>
        </div>
      </div>

      <div class="assessment-grid">
        <div v-for="assessment in popularAssessments" :key="assessment.id" class="assessment-card">
          <div class="assessment-header">
            <h3 class="assessment-title">{{ assessment.title }}</h3>
            <el-tag :type="getCategoryTagType(assessment.category)">
              {{ assessment.category }}
            </el-tag>
          </div>
          <p class="assessment-desc">{{ assessment.description }}</p>
          <div class="assessment-meta">
            <span class="meta-item">
              <i class="el-icon-time"></i>
              {{ assessment.estimatedTime }}分钟
            </span>
            <span class="meta-item">
              <i class="el-icon-document"></i>
              {{ assessment.questionsCount }}题
            </span>
          </div>
          <el-button type="primary" class="assessment-btn" @click="startAssessment(assessment.id)">
            <i class="el-icon-edit"></i>
            开始测评
          </el-button>
        </div>
      </div>
    </section>

    <!-- 底部联系信息区域 -->
    <footer class="footer-section">
      <div class="footer-content">
        <div class="footer-column">
          <h3 class="footer-title">星语航海</h3>
          <p class="footer-desc">
            专业的心理测评与分析平台，<br>
            陪伴您探索内心的海洋，<br>
            助您更健康的心灵成长。
          </p>
        </div>

        <div class="footer-column">
          <h3 class="footer-title">快速链接</h3>
          <ul class="footer-links">
            <li><a @click="$router.push('/assessment')">心理测评</a></li>
            <li><a @click="$router.push('/expert-consultation')">漂流瓶</a></li>
            <li><a @click="$router.push('/ai-interaction')">AI互动</a></li>
            <li><a @click="$router.push('/contact')">联系我们</a></li>
          </ul>
        </div>

        <div class="footer-column">
          <h3 class="footer-title">联系我们</h3>
          <ul class="footer-contact">
            <li>
              <i class="el-icon-phone"></i>
              <span>400-123-4567</span>
            </li>
            <li>
              <i class="el-icon-message"></i>
              <span>contact@xingyu.com</span>
            </li>
            <li>
              <i class="el-icon-location"></i>
              <span>北京市海淀区XX大厦</span>
            </li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <p>© 2023 星语航海 - 心理测评与分析平台 版权所有</p>
      </div>
    </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserConsultations, submitConsultationFeedback } from '@/api/consultation'

const router = useRouter()
const userStore = useUserStore()

// 获取用户信息
const userInfo = computed(() => userStore.userInfo)

// 咨询进程相关
const myConsultations = ref([])
const showFeedbackDialog = ref(false)
const feedbackForm = ref({
  consultationId: null,
  rating: 5,
  feedback: ''
})
const submittingFeedback = ref(false)

// 滚动到指定区域
const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 加载用户的咨询列表
const loadMyConsultations = async () => {
  try {
    const userId = userInfo.value.id
    if (userId) {
      const response = await getUserConsultations(userId)
      if (response.success) {
        myConsultations.value = response.consultations || []
        console.log('加载咨询列表成功，共 ' + myConsultations.value.length + ' 条记录')
      }
    }
  } catch (error) {
    console.error('加载咨询列表失败:', error)
  }
}

// 获取咨询状态类型
const getConsultStatusType = (status) => {
  const typeMap = {
    '待确认': 'info',
    '已确认': 'warning',
    '进行中': 'primary',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return typeMap[status] || 'info'
}

// 格式化咨询时间
const formatConsultTime = (timeStr) => {
  if (!timeStr) return '未安排'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 打开反馈对话框
const openFeedbackDialog = (consultation) => {
  feedbackForm.value = {
    consultationId: consultation.id,
    rating: 5,
    feedback: ''
  }
  showFeedbackDialog.value = true
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackForm.value.feedback.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }

  submittingFeedback.value = true
  try {
    const response = await submitConsultationFeedback(
      feedbackForm.value.consultationId,
      feedbackForm.value.rating,
      feedbackForm.value.feedback
    )

    if (response.success) {
      // 静默提交成功
      showFeedbackDialog.value = false
      // 重新加载咨询列表
      await loadMyConsultations()
    } else {
      ElMessage.error(response.message || '反馈提交失败')
    }
  } catch (error) {
    console.error('提交反馈失败:', error)
    ElMessage.error('反馈提交失败')
  } finally {
    submittingFeedback.value = false
  }
}

// 退出登录处理
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    userStore.logout()
    // 静默退出登录
    router.push('/login')
  } catch (error) {
    // 用户取消退出
  }
}

// 轮播图数据
const banners = ref([
  {
    id: 1,
    image: new URL('../assets/dashboard/22312ef8e626afa06ad44f796bdafa4b.png', import.meta.url).href,
    title: '专业心理测评与咨询平台',
    description: '为您提供专业的心理健康服务，让心灵得到温暖的呵护',
    buttonText: '立即体验',
    action: () => router.push('/assessment')
  },
  {
    id: 2,
    image: new URL('../assets/dashboard/332ad1108f2cb83cd8a14e12803ee378.png', import.meta.url).href,
    title: 'AI智能心理助手',
    description: '24小时在线，随时为您提供心理支持和指导',
    buttonText: '开始对话',
    action: () => router.push('/ai-interaction')
  },
  {
    id: 3,
    image: new URL('../assets/dashboard/aaff87b0e49410f8f46c148cc107728a.png', import.meta.url).href,
    title: '专业心理咨询团队',
    description: '资深心理咨询师，为您提供个性化咨询服务',
    buttonText: '预约咨询',
    action: () => router.push('/expert-consultation')
  }
])

const advantages = ref([
  {
    id: 1,
    title: '专业团队',
    description: '拥有一支经验丰富的心理咨询师团队，为您提供专业的心理评估与咨询服务',
    color: '#4f46e5',
    image: new URL('../assets/dashboard/zhuanyetuandui.jpg', import.meta.url).href
  },
  {
    id: 2,
    title: '隐私保护',
    description: '严格遵守私密性保护，确保您的信息和咨询内容得到妥善保护',
    color: '#db2777',
    image: new URL('../assets/dashboard/yinsibaohu.jpg', import.meta.url).href
  },
  {
    id: 3,
    title: 'AI智能',
    description: '先进的AI心理评估系统，结合专业心理学理论，为您提供个性化的心理分析',
    color: '#0284c7',
    image: new URL('../assets/dashboard/aizhineng.jpg', import.meta.url).href
  },
  {
    id: 4,
    title: '丰富资源',
    description: '提供大量心理健康资源和专业自助工具，帮助您建立积极的心理状态',
    color: '#16a34a',
    image: new URL('../assets/dashboard/fengfuziyuan.jpg', import.meta.url).href
  },
  {
    id: 5,
    title: '便捷服务',
    description: '随时随地的访问平台，在线完成测评及咨询预约，节省您的时间和精力',
    color: '#d97706',
    image: new URL('../assets/dashboard/xinlizixun.jpg', import.meta.url).href
  },
  {
    id: 6,
    title: '温暖陪伴',
    description: '我们理解您的感受，用温暖和专业陪伴您度过每一个心理困扰时刻',
    color: '#c2410c',
    image: new URL('../assets/dashboard/wen_nuan_pei_ban.jpg', import.meta.url).href
  }
])

const popularAssessments = ref([])

const getCategoryTagType = (category) => {
  const typeMap = {
    '抑郁': 'danger',
    '焦虑': 'warning',
    '压力': 'info',
    '人格': 'success'
  }
  return typeMap[category] || 'info'
}

const startAssessment = (assessmentId) => {
  router.push('/assessment')
}

// 从后端API获取测评数据
const loadRealAssessments = async () => {
  try {
    console.log('正在从后端加载真实测评数据...')

    const response = await fetch('http://localhost:8080/assessments/list')

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const result = await response.json()
    console.log('后端返回的真实测评数据:', result)

    if (result.code === 200) {
      const data = result.data || []

      // 转换数据格式以匹配前端需求
      popularAssessments.value = data.map(assessment => ({
        id: assessment.id || assessment.assessmentId || 0,
        title: assessment.title || assessment.assessmentName || '心理测评',
        category: assessment.category || '通用',
        description: assessment.description || '专业心理测评量表',
        questionsCount: assessment.questionsCount || assessment.questionCount || 0,
        estimatedTime: assessment.estimatedTime || 10
      }))

      console.log('处理后的热门测评数据:', popularAssessments.value)
      // 静默加载，不显示成功提示
    } else {
      throw new Error(result.message || '获取测评数据失败')
    }
  } catch (error) {
    console.error('加载真实测评数据失败:', error)
    ElMessage.error('加载测评数据失败，使用默认数据')

    // 如果后端不可用，使用默认数据
    popularAssessments.value = [
      {
        id: 1,
        title: '抑郁自评量表',
        category: '抑郁',
        description: '评估个体近一周的抑郁症状严重程度，帮助了解情绪状态',
        estimatedTime: 10,
        questionsCount: 20
      },
      {
        id: 2,
        title: '焦虑自评量表',
        category: '焦虑',
        description: '检测焦虑症状的程度，帮助识别和管理焦虑情绪',
        estimatedTime: 8,
        questionsCount: 20
      },
      {
        id: 3,
        title: '压力测试量表',
        category: '压力',
        description: '评估当前生活压力水平和应对能力，提供改善建议',
        estimatedTime: 5,
        questionsCount: 15
      },
      {
        id: 4,
        title: '人格特质测试',
        category: '人格',
        description: '基于大五人格理论，深入了解个人性格特点',
        estimatedTime: 15,
        questionsCount: 50
      }
    ]
  }
}

onMounted(async () => {
  // 检查登录状态
  userStore.checkLoginStatus()
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  // 加载真实测评数据
  await loadRealAssessments()

  // 加载用户的咨询列表
  await loadMyConsultations()
})
</script>

<style lang="scss" scoped>
.home-container {
  padding: 0;
  background: transparent;
  min-height: auto;
  position: relative;
}

.top-navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(15, 23, 42, 0.94);
  border-bottom: 1px solid rgba(148, 163, 184, 0.24);
  box-shadow: 0 10px 24px rgba(2, 6, 23, 0.16);
  backdrop-filter: blur(8px);
}

.navbar-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 66px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #f8fafc;
  font-size: 22px;
  font-weight: 800;
  cursor: pointer;

  i {
    font-size: 22px;
  }

  .logo-text {
    letter-spacing: 0.3px;
    color: #f8fafc;
  }
}

.navbar-menu {
  display: flex;
  gap: 8px;
  align-items: center;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #cbd5e1;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 9px;
  transition: background-color 0.2s ease, color 0.2s ease;

  &:hover {
    background: rgba(148, 163, 184, 0.18);
    color: #f8fafc;
  }

  i {
    font-size: 14px;
  }
}

.navbar-user {
  display: flex;
  align-items: center;
  gap: 10px;

  .user-name {
    color: #e2e8f0;
    font-weight: 700;
    font-size: 12px;
    padding: 7px 12px;
    background: rgba(30, 41, 59, 0.9);
    border: 1px solid rgba(148, 163, 184, 0.3);
    border-radius: 999px;
  }

  .logout-btn {
    border-radius: 9px;
    padding: 8px 12px;
    height: 32px;
    font-size: 12px;
    font-weight: 700;
    background: #ffffff;
    color: #334155;
    border: 1px solid #d5deea;
    box-shadow: none;
    transition: all 0.2s ease;

    &:hover {
      border-color: #bfd0e6;
      color: #0f172a;
      background: #ffffff;
    }
  }
}

.banner-section-top {
  margin-top: 0;
  margin-bottom: 22px;

  :deep(.el-carousel) {
    border-radius: 16px;
    overflow: hidden;
    border: 1px solid #dbe2ea;
    box-shadow: 0 14px 32px rgba(15, 23, 42, 0.14);

    .el-carousel__indicator {
      button {
        background: rgba(255, 255, 255, 0.45);
        width: 24px;
        height: 3px;
        border-radius: 999px;
      }

      &.is-active button {
        background: #ffffff;
        width: 30px;
      }
    }
  }
}

.consultations-section {
  background: transparent;
  padding: 0 0 30px;
  position: relative;
}

.consultations-container {
  max-width: 100%;
}

.consultation-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 14px;
}

.consultation-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  border: 1px solid #dbe2ea;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 24px rgba(15, 23, 42, 0.1);
    border-color: #c7d2e4;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
    padding-bottom: 10px;
    border-bottom: 1px solid #e2e8f0;
  }

  .consult-info {
    flex: 1;
  }

  .consult-title {
    font-size: 17px;
    font-weight: 800;
    color: #0f172a;
    margin: 0 0 6px 0;
  }

  .psychologist-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #64748b;
    font-size: 12px;
    font-weight: 600;

    i {
      color: #1d4ed8;
      font-size: 14px;
    }
  }

  .card-body {
    .consult-detail {
      margin-bottom: 20px;
    }

    .detail-item {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 8px;
      color: #334155;
      font-size: 13px;

      i {
        color: #1d4ed8;
        font-size: 14px;
      }
    }

    .feedback-section {
      margin-top: 12px;
      padding-top: 10px;
      border-top: 1px solid #e2e8f0;
    }

    .my-feedback {
      .feedback-title {
        font-size: 14px;
        font-weight: 700;
        color: #0f172a;
        margin-bottom: 8px;
      }

      .feedback-text {
        margin-top: 8px;
        padding: 10px;
        background: #f8fafc;
        border: 1px solid #e2e8f0;
        border-radius: 10px;
        color: #334155;
        line-height: 1.7;
        font-size: 13px;
      }
    }

    .feedback-actions {
      text-align: center;
    }
  }
}

.banner-item {
  height: 600px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(2, 6, 23, 0.42);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  max-width: 860px;
  padding: 0 40px;
  animation: fadeInUp 0.7s ease;
}

.banner-title {
  font-size: 48px;
  font-weight: 800;
  margin-bottom: 14px;
  text-shadow: 0 4px 30px rgba(0, 0, 0, 0.4);
  color: white;
  letter-spacing: 0.4px;
  line-height: 1.2;
}

.banner-desc {
  font-size: 17px;
  margin-bottom: 24px;
  line-height: 1.7;
  font-weight: 500;
  text-shadow: 0 2px 15px rgba(0, 0, 0, 0.3);
}

.banner-btn {
  font-size: 14px;
  padding: 0 22px;
  height: 42px;
  border-radius: 10px;
  background: #ffffff;
  color: #0f172a;
  border: 1px solid #ffffff;
  font-weight: 700;
  box-shadow: none;
  transition: all 0.2s ease;

  i {
    margin-right: 8px;
    font-size: 16px;
  }

  &:hover {
    transform: translateY(-1px);
    background: #f8fafc;
    border-color: #f8fafc;
    color: #0f172a;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.advantages-section {
  background: transparent;
  padding: 0 0 30px;
  position: relative;
}

.advantages-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  max-width: 100%;
}

.advantage-card {
  position: relative;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
  border: 1px solid #dbe2ea;
  overflow: hidden;
  isolation: isolate;
  transition: transform 0.45s cubic-bezier(0.2, 0.8, 0.2, 1),
              box-shadow 0.45s cubic-bezier(0.2, 0.8, 0.2, 1),
              border-color 0.3s ease;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 24px;
    width: 44px;
    height: 3px;
    background: var(--advantage-accent, #1d4ed8);
    border-radius: 0 0 4px 4px;
    transition: width 0.5s cubic-bezier(0.2, 0.8, 0.2, 1);
    z-index: 2;
  }

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 22px 44px rgba(15, 23, 42, 0.12);
    border-color: transparent;

    &::before {
      width: calc(100% - 48px);
    }

    .advantage-title {
      color: var(--advantage-accent, #0f172a);
    }

    .advantage-media img {
      transform: scale(1.06);
    }

    .advantage-media::after {
      opacity: 1;
    }
  }
}

.advantage-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 28px 24px 24px;
  text-align: left;
}

.advantage-index {
  display: inline-block;
  margin-bottom: 10px;
  color: var(--advantage-accent, #1d4ed8);
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 3px;
  font-variant-numeric: tabular-nums;
  opacity: 0.85;
}

.advantage-title {
  font-size: 20px;
  font-weight: 800;
  margin: 0 0 10px;
  color: #0f172a;
  letter-spacing: -0.2px;
  transition: color 0.3s ease;
}

.advantage-desc {
  color: #64748b;
  line-height: 1.75;
  font-size: 13.5px;
  font-weight: 500;
  margin: 0;
}

.advantage-media {
  width: 100%;
  height: 210px;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background: #f8fafc;
  border-top: 1px solid #eef2f7;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(180deg, transparent 62%, rgba(15, 23, 42, 0.08));
    opacity: 0.7;
    transition: opacity 0.4s ease;
    pointer-events: none;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.7s cubic-bezier(0.2, 0.8, 0.2, 1);
  }
}

.assessment-section {
  background: transparent;
  padding: 0 0 30px;
}

.assessment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 14px;
  max-width: 100%;
}

.assessment-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  border: 1px solid #dbe2ea;
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 22px rgba(15, 23, 42, 0.1);
    border-color: #c7d2e4;
  }
}

.assessment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.assessment-title {
  font-size: 17px;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
  flex: 1;
  margin-right: 15px;
  line-height: 1.4;
}

.assessment-desc {
  color: #64748b;
  line-height: 1.7;
  margin-bottom: 12px;
  font-size: 13px;
  font-weight: 500;
}

.assessment-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;

  .meta-item {
    display: flex;
    align-items: center;
    color: #64748b;
    font-size: 12px;
    font-weight: 700;

    i {
      margin-right: 4px;
      font-size: 14px;
      color: #1d4ed8;
    }

    span {
      background: #f8fafc;
      border: 1px solid #dbe2ea;
      padding: 3px 8px;
      border-radius: 999px;
      font-weight: 700;
    }
  }
}

.assessment-btn {
  width: 100%;
  border-radius: 9px;
  padding: 0 14px;
  height: 36px;
  font-size: 13px;
  font-weight: 700;
  background: #0f172a;
  border: 1px solid #0f172a;
  box-shadow: none;
  transition: all 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    background: #1e293b;
    border-color: #1e293b;
  }

  i {
    margin-right: 6px;
    font-size: 14px;
  }
}

.quick-access-section {
  background: transparent;
  padding: 0 0 30px;
}

.quick-access-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  max-width: 100%;
}

.access-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px 14px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.4s cubic-bezier(0.2, 0.8, 0.2, 1),
              box-shadow 0.4s cubic-bezier(0.2, 0.8, 0.2, 1),
              border-color 0.3s ease;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
  border: 1px solid #dbe2ea;
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 18px 36px rgba(15, 23, 42, 0.1);
    border-color: transparent;

    .access-icon {
      transform: scale(1.08);

      &::before {
        opacity: 0.22;
      }
    }

    .access-arrow {
      opacity: 1;
      transform: translate(-4px, -50%);
    }
  }
}

.access-icon {
  width: 52px;
  height: 52px;
  margin: 0 auto 12px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  position: relative;
  overflow: hidden;
  transition: transform 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    background: currentColor;
    opacity: 0.13;
    transition: opacity 0.4s ease;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    border: 1px solid currentColor;
    opacity: 0.28;
  }

  i {
    position: relative;
    z-index: 1;
    color: inherit;
  }

  &.assessment { color: #2563eb; }
  &.expert { color: #0ea5a5; }
  &.bottle { color: #d97706; }
  &.ai { color: #7c3aed; }
  &.contact { color: #ef4444; }
}

.access-card h3 {
  font-size: 16px;
  font-weight: 800;
  margin: 0 0 6px;
  color: #0f172a;
  letter-spacing: 0.1px;
}

.access-card p {
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  margin: 0;
  line-height: 1.6;
}

.access-card .access-arrow {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translate(6px, -50%);
  opacity: 0;
  transition: opacity 0.3s ease, transform 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);

  i {
    font-size: 13px;
    color: #1d4ed8;
  }
}

@media (max-width: 1200px) {
  .advantages-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-access-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .navbar-content {
    padding: 0 14px;
  }

  .navbar-logo {
    font-size: 18px;

    i {
      font-size: 20px;
    }
  }

  .navbar-menu {
    display: none;
  }

  .navbar-user {
    .user-name {
      display: none;
    }
  }

  .banner-item {
    height: 420px;
  }

  .banner-title {
    font-size: 32px;
  }

  .banner-desc {
    font-size: 15px;
  }

  .banner-btn {
    height: 38px;
    font-size: 13px;
  }

  .advantages-grid {
    grid-template-columns: 1fr;
  }

  .advantage-media {
    height: 200px;
  }

  .quick-access-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .assessment-grid {
    grid-template-columns: 1fr;
  }

  .consultation-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .top-navbar {
    .navbar-content {
      height: 60px;
      padding: 0 15px;
    }
  }

  .navbar-logo {
    font-size: 18px;

    i {
      font-size: 20px;
    }

    .logo-text {
      display: none;
    }
  }

  .banner-item {
    height: 360px;
  }

  .banner-title {
    font-size: 26px;
  }

  .banner-desc {
    font-size: 14px;
  }

  .quick-access-grid {
    grid-template-columns: 1fr;
  }

  .access-icon {
    width: 48px;
    height: 48px;

    i {
      font-size: 20px;
    }
  }
}

.footer-section {
  background: #0f172a;
  color: #f8fafc;
  padding: 48px 28px 24px;
  margin-top: 30px;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 34px;
  margin-bottom: 26px;
}

.footer-column {
  h3.footer-title {
    font-size: 18px;
    font-weight: 800;
    margin-bottom: 12px;
    color: #f8fafc;
    position: relative;
    padding-bottom: 8px;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 36px;
      height: 2px;
      background: #94a3b8;
      border-radius: 2px;
    }
  }

  .footer-desc {
    line-height: 1.7;
    font-size: 13px;
    color: #cbd5e1;
    font-weight: 500;
  }
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    margin-bottom: 15px;

    a {
      color: #cbd5e1;
      text-decoration: none;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      display: inline-block;

      &:hover {
        color: #f8fafc;
        transform: translateX(2px);
      }
    }
  }
}

.footer-contact {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 15px;
    color: #cbd5e1;
    font-size: 13px;
    font-weight: 600;

    i {
      font-size: 14px;
      color: #f8fafc;
    }
  }
}

.footer-bottom {
  text-align: center;
  padding-top: 18px;
  border-top: 1px solid rgba(148, 163, 184, 0.24);
  max-width: 1400px;
  margin: 0 auto;

  p {
    color: #94a3b8;
    font-size: 12px;
    font-weight: 600;
    margin: 0;
  }
}

@media (max-width: 768px) {
  .footer-section {
    padding: 36px 16px 18px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 20px;
    margin-bottom: 18px;
  }
}
</style>
