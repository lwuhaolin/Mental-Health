<template>
  <div class="app-page contact-container">
    <div class="app-page__inner">
      <SubPageHeader
        title="联系我们"
        subtitle="如有任何问题或建议，欢迎随时与我们沟通"
      />

      <div class="contact-content">
        <el-row :gutter="30">
          <el-col :span="12">
            <el-card class="contact-info-card">
              <template #header>
                <div class="card-header">
                  <i class="el-icon-info"></i>
                  <span>联系方式</span>
                </div>
              </template>

              <div class="contact-info">
                <div class="info-item">
                  <i class="el-icon-phone"></i>
                  <div class="info-content">
                    <h4>客服热线</h4>
                    <p>400-123-4567</p>
                    <span class="info-desc">周一至周日 9:00-21:00</span>
                  </div>
                </div>

                <div class="info-item">
                  <i class="el-icon-message"></i>
                  <div class="info-content">
                    <h4>客服邮箱</h4>
                    <p>service@xingyu.com</p>
                    <span class="info-desc">24小时内回复</span>
                  </div>
                </div>

                <div class="info-item">
                  <i class="el-icon-location-information"></i>
                  <div class="info-content">
                    <h4>公司地址</h4>
                    <p>北京市朝阳区心理科技大厦A座5层</p>
                    <span class="info-desc">欢迎预约来访</span>
                  </div>
                </div>

                <div class="info-item">
                  <i class="el-icon-time"></i>
                  <div class="info-content">
                    <h4>服务时间</h4>
                    <p>周一至周五 9:00-18:00</p>
                    <span class="info-desc">节假日除外</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card class="contact-form-card">
              <template #header>
                <div class="card-header">
                  <i class="el-icon-edit"></i>
                  <span>在线留言</span>
                </div>
              </template>

              <el-form
                ref="contactFormRef"
                :model="contactForm"
                :rules="contactRules"
                label-width="80px"
                class="contact-form"
              >
                <el-form-item label="姓名" prop="name">
                  <el-input
                    v-model="contactForm.name"
                    placeholder="请输入您的姓名"
                  />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="contactForm.email"
                    placeholder="请输入您的邮箱"
                  />
                </el-form-item>

                <el-form-item label="电话" prop="phone">
                  <el-input
                    v-model="contactForm.phone"
                    placeholder="请输入您的电话"
                  />
                </el-form-item>

                <el-form-item label="主题" prop="subject">
                  <el-select
                    v-model="contactForm.subject"
                    placeholder="请选择咨询主题"
                    style="width: 100%"
                  >
                    <el-option label="产品建议" value="suggestion" />
                    <el-option label="问题反馈" value="problem" />
                    <el-option label="合作咨询" value="cooperation" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>

                <el-form-item label="内容" prop="content">
                  <el-input
                    v-model="contactForm.content"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入您的问题或建议..."
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    @click="submitContactForm"
                    :loading="submitting"
                    style="width: 100%"
                  >
                    {{ submitting ? '提交中...' : '提交留言' }}
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>

        <div class="faq-section">
          <el-card>
            <template #header>
              <div class="faq-header">
                <i class="el-icon-question"></i>
                <span>常见问题</span>
              </div>
            </template>

            <el-collapse v-model="activeFaq">
              <el-collapse-item
                v-for="faq in faqs"
                :key="faq.id"
                :name="faq.id"
                :title="faq.question"
              >
                <p>{{ faq.answer }}</p>
              </el-collapse-item>
            </el-collapse>
          </el-card>
        </div>

        <div class="team-section">
          <el-card>
            <template #header>
              <div class="team-header">
                <i class="el-icon-user"></i>
                <span>团队介绍</span>
              </div>
            </template>

            <div class="team-members">
              <el-row :gutter="20">
                <el-col
                  v-for="member in teamMembers"
                  :key="member.id"
                  :span="6"
                >
                  <div class="team-member">
                    <el-avatar :size="80" :src="member.avatar" />
                    <h4>{{ member.name }}</h4>
                    <p class="position">{{ member.position }}</p>
                    <p class="description">{{ member.description }}</p>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import SubPageHeader from '@/components/SubPageHeader.vue'
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { submitContactMessage, getTeamPsychologists } from '@/api/contact'

const contactFormRef = ref()
const submitting = ref(false)
const activeFaq = ref(['1'])
const loadingTeam = ref(false)

const contactForm = ref({
  name: '',
  email: '',
  phone: '',
  subject: '',
  content: ''
})

const contactRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, message: '姓名长度不能少于2个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请选择咨询主题', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 10, message: '内容长度不能少于10个字符', trigger: 'blur' }
  ]
}

const faqs = ref([
  {
    id: '1',
    question: '如何注册成为平台用户？',
    answer: '点击首页右上角“注册”按钮，填写基本信息即可完成注册。注册后即可使用完整的心理测评与咨询服务。'
  },
  {
    id: '2',
    question: '心理测评结果准确吗？',
    answer: '测评量表基于心理学专业理论，具有较高信效度。结果仅供参考，不能替代专业诊断。'
  },
  {
    id: '3',
    question: '如何预约心理咨询？',
    answer: '登录后进入“专家咨询”页面，选择咨询师并查看可预约时间，提交预约即可。'
  },
  {
    id: '4',
    question: '平台如何保护用户隐私？',
    answer: '平台采用严格的数据加密与权限控制机制，咨询内容与个人信息均受到保护。'
  },
  {
    id: '5',
    question: 'AI 心理助手是否收费？',
    answer: '基础 AI 对话功能免费，部分高级功能可能收费，具体以会员服务说明为准。'
  }
])

const teamMembers = ref([])

const loadTeamPsychologists = async () => {
  loadingTeam.value = true
  try {
    const response = await getTeamPsychologists()
    if (response.code === 200) {
      teamMembers.value = response.psychologists.map(psychologist => ({
        id: psychologist.id,
        name: psychologist.realName,
        position: psychologist.title,
        description: `${psychologist.specialty} | ${psychologist.experienceYears}年经验 | ${psychologist.introduction}`,
        avatar: psychologist.avatar || `/api/placeholder/80x80?text=${psychologist.realName.charAt(0)}`
      }))
    } else {
      ElMessage.error('加载团队专家信息失败')
    }
  } catch {
    ElMessage.error('网络错误，无法加载团队信息')
    teamMembers.value = [
      {
        id: 1,
        name: '张心语',
        position: '首席心理咨询师',
        description: '国家二级心理咨询师，10年咨询经验。',
        avatar: '/api/placeholder/80x80?text=张'
      },
      {
        id: 2,
        name: '李技术',
        position: '技术总监',
        description: '资深全栈工程师，专注心理健康科技产品。',
        avatar: '/api/placeholder/80x80?text=李'
      },
      {
        id: 3,
        name: '王运营',
        position: '运营总监',
        description: '互联网产品运营专家，持续提升用户体验。',
        avatar: '/api/placeholder/80x80?text=王'
      },
      {
        id: 4,
        name: '赵设计',
        position: 'UI/UX设计师',
        description: '注重温暖、易用的人机交互设计。',
        avatar: '/api/placeholder/80x80?text=赵'
      }
    ]
  } finally {
    loadingTeam.value = false
  }
}

const submitContactForm = async () => {
  if (!contactFormRef.value) return

  try {
    await contactFormRef.value.validate()
    submitting.value = true

    const response = await submitContactMessage(contactForm.value)
    if (response.code === 200) {
      ElMessage.success('提交成功，我们会尽快与你联系')
      contactFormRef.value.resetFields()
    } else {
      ElMessage.error(response.message || '提交失败，请稍后重试')
    }
  } catch {
    ElMessage.error('表单校验失败，请检查后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadTeamPsychologists()
})
</script>

<style lang="scss" scoped>
.contact-container {
  padding: 0;
  min-height: auto;
  background: transparent;
  position: relative;
}

.contact-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(11, 18, 32, 0.55);
  z-index: -1;
  pointer-events: none;
}

.contact-header {
  display: none;
}

.contact-content {
  .el-row {
    margin-bottom: 30px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;

  i {
    margin-right: 8px;
    font-size: 18px;
    color: #1e90ff;
  }
}

.contact-info {
  .info-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }

    i {
      font-size: 24px;
      color: #1e90ff;
      margin-right: 16px;
      margin-top: 4px;
    }
  }

  .info-content {
    flex: 1;

    h4 {
      font-size: 16px;
      font-weight: bold;
      margin: 0 0 4px 0;
      color: #333;
    }

    p {
      font-size: 18px;
      font-weight: bold;
      margin: 0 0 4px 0;
      color: #1e90ff;
    }

    .info-desc {
      font-size: 12px;
      color: #999;
    }
  }
}

.contact-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.faq-section,
.team-section {
  margin-bottom: 30px;
}

.faq-header,
.team-header {
  display: flex;
  align-items: center;
  font-weight: bold;

  i {
    margin-right: 8px;
    font-size: 18px;
    color: #1e90ff;
  }
}

.team-members {
  .team-member {
    text-align: center;
    padding: 20px 10px;

    h4 {
      font-size: 16px;
      font-weight: bold;
      margin: 12px 0 4px 0;
      color: #333;
    }

    .position {
      font-size: 14px;
      color: #1e90ff;
      margin-bottom: 8px;
      font-weight: bold;
    }

    .description {
      font-size: 12px;
      color: #666;
      line-height: 1.4;
      margin: 0;
    }
  }
}

@media (max-width: 768px) {
  .contact-content .el-col {
    margin-bottom: 20px;
  }

  .team-members .el-col {
    margin-bottom: 20px;
  }

  .contact-info .info-item {
    flex-direction: column;
    text-align: center;

    i {
      margin-right: 0;
      margin-bottom: 8px;
    }
  }
}
</style>
