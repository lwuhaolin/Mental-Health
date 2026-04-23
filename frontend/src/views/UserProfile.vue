<template>
  <div class="app-page user-profile-container">
    <div class="app-page__inner">
      <SubPageHeader
        title="个人中心"
        subtitle="查看并管理你的账户信息与心理测评记录"
      />

      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
            {{ userInfo.realName?.charAt(0) || userInfo.username?.charAt(0) }}
          </el-avatar>
          <div class="user-info">
            <h2 class="username">{{ userInfo.realName || userInfo.username }}</h2>
            <p class="user-role">
              <el-tag :type="getRoleTagType(userInfo.role)" size="small">
                {{ getRoleText(userInfo.role) }}
              </el-tag>
            </p>
            <p class="join-time">加入时间: {{ formatTime(userInfo.createTime) }}</p>
          </div>
        </div>

        <div class="statistics-section">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.assessmentCount || 0 }}</div>
            <div class="stat-label">测评次数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userStats.consultationCount || 0 }}</div>
            <div class="stat-label">咨询次数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userStats.messageCount || 0 }}</div>
            <div class="stat-label">消息条数</div>
          </div>
        </div>
      </div>

      <div class="profile-content">
        <el-row :gutter="20">
          <el-col :span="16">
            <div class="info-card">
              <div class="card-header">
                <h3><i class="el-icon-user"></i> 基本信息</h3>
                <el-button type="primary" size="small" @click="editProfile">
                  <i class="el-icon-edit"></i> 编辑信息
                </el-button>
              </div>
              <div class="info-content">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
                  <el-descriptions-item label="真实姓名">{{ userInfo.realName || '未设置' }}</el-descriptions-item>
                  <el-descriptions-item label="性别">{{ userInfo.gender || '未设置' }}</el-descriptions-item>
                  <el-descriptions-item label="年龄">{{ userInfo.age || '未设置' }}</el-descriptions-item>
                  <el-descriptions-item label="邮箱">{{ userInfo.email || '未设置' }}</el-descriptions-item>
                  <el-descriptions-item label="手机号">{{ userInfo.phone || '未设置' }}</el-descriptions-item>
                  <el-descriptions-item label="最后登录">{{ formatTime(userInfo.lastLoginTime) }}</el-descriptions-item>
                  <el-descriptions-item label="账户状态">
                    <el-tag :type="userInfo.status === '正常' || userInfo.status === 'active' ? 'success' : 'danger'">
                      {{ userInfo.status === '正常' || userInfo.status === 'active' ? '正常' : '异常' }}
                    </el-tag>
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </div>

            <div class="assessment-card">
              <div class="card-header">
                <h3><i class="el-icon-document"></i> 最近测评记录</h3>
                <el-button type="text" @click="$router.push('/assessment')">
                  查看全部 <i class="el-icon-arrow-right"></i>
                </el-button>
              </div>
              <div class="assessment-list">
                <el-table :data="recentAssessments" style="width: 100%">
                  <el-table-column prop="assessmentTitle" label="测评标题" min-width="150"></el-table-column>
                  <el-table-column prop="completionTime" label="完成时间" width="120">
                    <template #default="scope">
                      {{ formatShortTime(scope.row.completionTime) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="totalScore" label="得分" width="80"></el-table-column>
                  <el-table-column prop="assessmentLevel" label="等级" width="80">
                    <template #default="scope">
                      <el-tag :type="getLevelTagType(scope.row.assessmentLevel)" size="small">
                        {{ scope.row.assessmentLevel }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="120">
                    <template #default="scope">
                      <el-button type="text" @click="viewAssessmentResult(scope.row.id)">
                        查看报告
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-col>

          <el-col :span="8">
            <div class="quick-actions-card">
              <div class="card-header">
                <h3><i class="el-icon-s-operation"></i> 快捷操作</h3>
              </div>
              <div class="actions-list">
                <div class="action-item" @click="$router.push('/assessment')">
                  <div class="action-icon" style="background: #409EFF;">
                    <FileTextOutlined />
                  </div>
                  <div class="action-info">
                    <h4>开始测评</h4>
                    <p>进行心理测评并生成结果报告</p>
                  </div>
                </div>

                <div class="action-item" @click="$router.push('/ai-interaction')">
                  <div class="action-icon" style="background: #67C23A;">
                    <RobotOutlined />
                  </div>
                  <div class="action-info">
                    <h4>AI互动</h4>
                    <p>与AI助手进行心理对话</p>
                  </div>
                </div>

                <div class="action-item" @click="$router.push('/message-bottle')">
                  <div class="action-icon" style="background: #E6A23C;">
                    <NotificationOutlined />
                  </div>
                  <div class="action-info">
                    <h4>漂流瓶</h4>
                    <p>分享心情，收获陪伴</p>
                  </div>
                </div>

                <div class="action-item" @click="consultExpert">
                  <div class="action-icon" style="background: #F56C6C;">
                    <TeamOutlined />
                  </div>
                  <div class="action-info">
                    <h4>专家咨询</h4>
                    <p>预约专业心理咨询师</p>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-dialog
        v-model="showEditDialog"
        title="编辑个人信息"
        width="600px"
      >
        <el-form :model="editForm" label-width="80px">
          <el-form-item label="真实姓名">
            <el-input v-model="editForm.realName" placeholder="请输入真实姓名"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="editForm.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
              <el-radio label="保密">保密</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input-number v-model="editForm.age" :min="1" :max="120"></el-input-number>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" placeholder="请输入邮箱"></el-input>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" placeholder="请输入手机号"></el-input>
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import SubPageHeader from '@/components/SubPageHeader.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { updateUser } from '@/api/user'
import {
  FileTextOutlined,
  RobotOutlined,
  NotificationOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const showEditDialog = ref(false)

const userInfo = ref({
  id: 1,
  username: 'testuser',
  realName: '测试用户',
  gender: '男',
  age: 25,
  email: 'test@example.com',
  phone: '13800138000',
  avatar: '',
  role: 'user',
  status: 'active',
  createTime: new Date('2024-01-01'),
  lastLoginTime: new Date()
})

const userStats = ref({
  assessmentCount: 3,
  consultationCount: 2,
  messageCount: 15
})

const recentAssessments = ref([])
const editForm = ref({})

const getRoleTagType = (role) => {
  const typeMap = {
    user: 'primary',
    psychologist: 'success',
    admin: 'warning'
  }
  return typeMap[role] || 'info'
}

const getRoleText = (role) => {
  const textMap = {
    user: '普通用户',
    psychologist: '心理专家',
    admin: '管理员'
  }
  return textMap[role] || '未知角色'
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

const formatTime = (time) => {
  if (!time) return '未知'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatShortTime = (time) => {
  if (!time) return '未知'
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}

const editProfile = () => {
  editForm.value = { ...userInfo.value }
  showEditDialog.value = true
}

const saveProfile = async () => {
  try {
    const response = await updateUser(editForm.value)

    if (response.success) {
      Object.assign(userInfo.value, editForm.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      showEditDialog.value = false
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新失败，请稍后重试')
  }
}

const viewAssessmentResult = (assessmentId) => {
  router.push({
    path: `/assessment/result/${assessmentId}`,
    query: { from: 'profile' }
  })
}

const consultExpert = () => {
  router.push('/expert-consultation')
}

const loadAssessmentHistory = () => {
  try {
    const assessmentHistory = JSON.parse(localStorage.getItem('assessmentHistory') || '[]')
    const currentUserId = userInfo.value.id

    const userHistory = assessmentHistory
      .filter(item => item.userId === currentUserId)
      .slice(0, 5)
      .map(item => ({
        id: item.assessmentId,
        assessmentTitle: item.assessmentTitle,
        completionTime: new Date(item.completionTime),
        totalScore: item.totalScore,
        assessmentLevel: item.assessmentLevel
      }))

    recentAssessments.value = userHistory
    userStats.value.assessmentCount = assessmentHistory.filter(item => item.userId === currentUserId).length
  } catch (e) {
    console.error('加载测评历史失败:', e)
  }
}

onMounted(() => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      const parsedInfo = JSON.parse(storedUserInfo)
      userInfo.value = { ...userInfo.value, ...parsedInfo }
      loadAssessmentHistory()
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
})
</script>

<style lang="scss" scoped>
.user-profile-container {
  background: transparent;
  min-height: auto;
  padding: 0;
  position: relative;

  &::before {
    content: none;
  }
}

.profile-header {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  align-items: center;

  .user-avatar {
    margin-right: 20px;
    background: #409eff;
    color: white;
    font-size: 32px;
    font-weight: bold;
  }

  .user-info {
    .username {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      margin: 0 0 8px 0;
    }

    .user-role {
      margin: 0 0 8px 0;
    }

    .join-time {
      font-size: 14px;
      color: #999;
      margin: 0;
    }
  }
}

.statistics-section {
  display: flex;
  gap: 40px;

  .stat-item {
    text-align: center;

    .stat-value {
      font-size: 32px;
      font-weight: bold;
      color: #409eff;
      line-height: 1;
    }

    .stat-label {
      font-size: 14px;
      color: #666;
      margin-top: 8px;
    }
  }
}

.profile-content {
  .info-card,
  .assessment-card,
  .quick-actions-card,
  .notifications-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin: 0;
    display: flex;
    align-items: center;

    i {
      margin-right: 8px;
      font-size: 20px;
    }
  }
}

.actions-list {
  .action-item {
    display: flex;
    align-items: center;
    padding: 16px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    margin-bottom: 12px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .action-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;

      :deep(svg) {
        width: 20px;
        height: 20px;
        color: white;
      }
    }

    .action-info {
      flex: 1;

      h4 {
        margin: 0 0 4px 0;
        font-size: 16px;
        color: #333;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: #666;
      }
    }
  }
}

.notifications-list {
  .notification-item {
    display: flex;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .notification-icon {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;

      &.info {
        background: #409eff;
      }

      &.success {
        background: #67c23a;
      }

      &.warning {
        background: #e6a23c;
      }

      i {
        font-size: 16px;
        color: white;
      }
    }

    .notification-content {
      flex: 1;

      .notification-title {
        margin: 0 0 4px 0;
        font-size: 14px;
        color: #333;
        line-height: 1.4;
      }

      .notification-time {
        margin: 0;
        font-size: 12px;
        color: #999;
      }
    }
  }
}

@media (max-width: 768px) {
  .user-profile-container {
    padding: 10px;
  }

  .profile-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;

    .user-avatar {
      margin-right: 0;
      margin-bottom: 12px;
    }
  }

  .statistics-section {
    gap: 20px;

    .stat-item .stat-value {
      font-size: 24px;
    }
  }

  .profile-content .el-col {
    width: 100%;
  }
}
</style>
