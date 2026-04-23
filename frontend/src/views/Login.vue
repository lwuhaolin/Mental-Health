<template>
  <div class="login-container">
    <div class="login-background">
      <div class="aurora aurora-1"></div>
      <div class="aurora aurora-2"></div>
      <div class="aurora aurora-3"></div>
      <div class="grid-overlay"></div>
      <div class="glow glow-left"></div>
      <div class="glow glow-right"></div>
      <div class="wave wave1"></div>
      <div class="wave wave2"></div>
      <div class="wave wave3"></div>
      <div class="floating-particles">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>

    <div class="login-content">
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">
            <i class="el-icon-ship"></i>
            星语航海
          </h1>
          <p class="login-subtitle">专业心理测评与咨询平台</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item>
            <el-radio-group v-model="loginForm.role" @change="handleRoleChange">
              <el-radio-button label="user">普通用户</el-radio-button>
              <el-radio-button label="psychologist">心理专家</el-radio-button>
              <el-radio-button label="admin">管理员</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              :placeholder="getUsernamePlaceholder()"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>

          <div class="login-links">
            <el-link v-if="loginForm.role === 'user'" type="primary" @click="showRegister = true">用户注册</el-link>
            <el-link v-if="loginForm.role === 'psychologist'" type="primary" @click="showPsychologistRegister = true">专家注册</el-link>
            <el-link type="info" @click="showForgotPassword = true">忘记密码</el-link>
          </div>
        </el-form>
      </div>

      <el-dialog
        v-model="showForgotPassword"
        title="找回密码"
        width="400px"
        center
      >
        <el-form
          ref="forgotPasswordFormRef"
          :model="forgotPasswordForm"
          :rules="forgotPasswordRules"
          label-width="80px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="forgotPasswordForm.username" placeholder="请输入注册时使用的用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="forgotPasswordForm.email" placeholder="请输入注册时绑定的邮箱" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="forgotPasswordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="forgotPasswordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="showForgotPassword = false">取消</el-button>
          <el-button type="primary" @click="handleForgotPassword" :loading="forgotPasswordLoading">
            重置密码
          </el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="showRegister"
        title="用户注册"
        width="400px"
        center
      >
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-width="80px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="registerForm.password" type="password" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="registerForm.realName" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="registerForm.email" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="registerForm.phone" />
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="showRegister = false">取消</el-button>
          <el-button type="primary" @click="handleRegister" :loading="registerLoading">
            注册
          </el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="showPsychologistRegister"
        title="心理专家注册"
        width="500px"
        center
      >
        <el-form
          ref="psychologistRegisterFormRef"
          :model="psychologistRegisterForm"
          :rules="psychologistRegisterRules"
          label-width="100px"
        >
          <el-form-item label="专家账号" prop="username">
            <el-input v-model="psychologistRegisterForm.username" placeholder="请输入专家登录账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="psychologistRegisterForm.password" type="password" placeholder="请输入登录密码" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="psychologistRegisterForm.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="职称" prop="title">
            <el-input v-model="psychologistRegisterForm.title" placeholder="如：国家二级心理咨询师" />
          </el-form-item>
          <el-form-item label="擅长领域" prop="specialty">
            <el-input v-model="psychologistRegisterForm.specialty" placeholder="如：抑郁焦虑、人际关系、职场压力" />
          </el-form-item>
          <el-form-item label="从业年限" prop="experienceYears">
            <el-input-number v-model="psychologistRegisterForm.experienceYears" :min="1" :max="50" placeholder="请输入从业年限" />
          </el-form-item>
          <el-form-item label="时薪(元)" prop="hourlyRate">
            <el-input-number v-model="psychologistRegisterForm.hourlyRate" :min="100" :max="1000" :step="50" placeholder="请输入时薪" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="psychologistRegisterForm.email" placeholder="请输入联系邮箱" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="psychologistRegisterForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="个人简介" prop="introduction">
            <el-input
              v-model="psychologistRegisterForm.introduction"
              type="textarea"
              :rows="3"
              placeholder="请简要介绍您的专业背景和咨询方向"
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="showPsychologistRegister = false">取消</el-button>
          <el-button type="primary" @click="handlePsychologistRegister" :loading="psychologistRegisterLoading">
            提交注册
          </el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const registerFormRef = ref()
const forgotPasswordFormRef = ref()
const psychologistRegisterFormRef = ref()
const loading = ref(false)
const registerLoading = ref(false)
const forgotPasswordLoading = ref(false)
const psychologistRegisterLoading = ref(false)
const showRegister = ref(false)
const showForgotPassword = ref(false)
const showPsychologistRegister = ref(false)

const loginForm = reactive({
  role: 'user',
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: ''
})

const forgotPasswordForm = reactive({
  username: '',
  email: '',
  newPassword: '',
  confirmPassword: ''
})

const psychologistRegisterForm = reactive({
  username: '',
  password: '',
  realName: '',
  title: '',
  specialty: '',
  experienceYears: 5,
  hourlyRate: 300,
  email: '',
  phone: '',
  introduction: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== forgotPasswordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const forgotPasswordRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const psychologistRegisterRules = {
  username: [
    { required: true, message: '请输入专家账号', trigger: 'blur' },
    { min: 3, message: '账号长度不能少于3位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入擅长领域', trigger: 'blur' }],
  experienceYears: [{ required: true, message: '请输入从业年限', trigger: 'blur' }],
  hourlyRate: [{ required: true, message: '请输入时薪', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  introduction: [
    { required: true, message: '请输入个人简介', trigger: 'blur' },
    { min: 10, message: '个人简介长度不能少于10个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    const result = await userStore.login(loginForm)
    if (result.success) {
      if (loginForm.role === 'admin') {
        router.push('/admin/dashboard')
      } else if (loginForm.role === 'psychologist') {
        router.push('/psychologist/dashboard')
      } else {
        router.push('/home')
      }
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查输入信息后重试')
  } finally {
    loading.value = false
  }
}

const handleRoleChange = () => {
  loginForm.username = ''
  loginForm.password = ''
}

const getUsernamePlaceholder = () => {
  switch (loginForm.role) {
    case 'admin':
      return '请输入管理员账号'
    case 'psychologist':
      return '请输入专家账号'
    default:
      return '请输入用户名'
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    registerLoading.value = true

    const result = await userStore.register(registerForm)
    if (result.success) {
      showRegister.value = false
      Object.keys(registerForm).forEach((key) => {
        registerForm[key] = ''
      })
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    registerLoading.value = false
  }
}

const handleForgotPassword = async () => {
  if (!forgotPasswordFormRef.value) return

  try {
    await forgotPasswordFormRef.value.validate()
    forgotPasswordLoading.value = true

    let result
    if (loginForm.role === 'psychologist') {
      result = await userStore.forgotPasswordPsychologist(forgotPasswordForm)
    } else {
      result = await userStore.forgotPassword(forgotPasswordForm)
    }

    if (result.success) {
      showForgotPassword.value = false
      Object.keys(forgotPasswordForm).forEach((key) => {
        forgotPasswordForm[key] = ''
      })
    } else {
      ElMessage.error(result.message || '密码重置失败')
    }
  } catch (error) {
    console.error('密码重置失败:', error)
    ElMessage.error('密码重置失败，请稍后重试')
  } finally {
    forgotPasswordLoading.value = false
  }
}

const handlePsychologistRegister = async () => {
  if (!psychologistRegisterFormRef.value) return

  try {
    await psychologistRegisterFormRef.value.validate()
    psychologistRegisterLoading.value = true

    const result = await userStore.registerPsychologist(psychologistRegisterForm)
    if (result.success) {
      showPsychologistRegister.value = false
      Object.keys(psychologistRegisterForm).forEach((key) => {
        psychologistRegisterForm[key] = ''
      })
      psychologistRegisterForm.experienceYears = 5
      psychologistRegisterForm.hourlyRate = 300
    } else {
      ElMessage.error(result.message || '专家注册失败')
    }
  } catch (error) {
    console.error('专家注册失败:', error)
    ElMessage.error('专家注册失败，请检查信息后重试')
  } finally {
    psychologistRegisterLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 20% 15%, #1e3a8a 0%, transparent 38%),
    radial-gradient(circle at 80% 82%, #0f766e 0%, transparent 30%),
    linear-gradient(120deg, #020617 0%, #0b1b46 48%, #0a1634 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at 12% 18%, rgba(148, 163, 184, 0.12) 0, transparent 45%);
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at 88% 84%, rgba(37, 99, 235, 0.14) 0, transparent 48%);
    pointer-events: none;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.login-background {
  position: absolute;
  inset: -12%;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.aurora {
  position: absolute;
  border-radius: 50%;
  filter: blur(70px);
  opacity: 0.35;
  mix-blend-mode: screen;
}

.aurora-1 {
  width: 600px;
  height: 600px;
  top: -120px;
  left: -140px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.85) 0%, rgba(30, 64, 175, 0.16) 65%, transparent 100%);
  animation: driftSlow 18s ease-in-out infinite alternate;
}

.aurora-2 {
  width: 520px;
  height: 520px;
  bottom: -140px;
  right: -100px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.75) 0%, rgba(15, 118, 110, 0.14) 65%, transparent 100%);
  animation: driftSlow 23s ease-in-out infinite alternate-reverse;
}

.aurora-3 {
  width: 420px;
  height: 420px;
  top: 28%;
  left: 48%;
  background: radial-gradient(circle, rgba(129, 140, 248, 0.55) 0%, rgba(37, 99, 235, 0.1) 70%, transparent 100%);
  animation: floatSoft 16s ease-in-out infinite;
}

.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.08) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(circle at center, black 36%, transparent 92%);
  animation: gridMove 24s linear infinite;
}

.glow {
  position: absolute;
  width: 46vw;
  max-width: 620px;
  height: 46vw;
  max-height: 620px;
  border-radius: 50%;
  filter: blur(50px);
  opacity: 0.24;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.9) 0%, rgba(15, 23, 42, 0) 70%);
}

.glow-left {
  left: -14%;
  top: 10%;
  animation: pulseGlow 8s ease-in-out infinite;
}

.glow-right {
  right: -12%;
  bottom: -8%;
  animation: pulseGlow 10s ease-in-out infinite reverse;
}

@keyframes wave {
  0% {
    transform: translateX(0) rotate(0deg);
  }
  100% {
    transform: translateX(-50%) rotate(360deg);
  }
}

.wave {
  position: absolute;
  width: 160%;
  height: 300px;
  left: -30%;
  border-radius: 42%;
  opacity: 0.2;
  filter: blur(1px);
}

.wave1 {
  bottom: -180px;
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.33), rgba(30, 64, 175, 0.12));
  animation: wave 24s linear infinite;
}

.wave2 {
  bottom: -200px;
  background: linear-gradient(90deg, rgba(16, 185, 129, 0.22), rgba(59, 130, 246, 0.14));
  animation: wave 32s linear infinite reverse;
}

.wave3 {
  bottom: -220px;
  background: linear-gradient(90deg, rgba(129, 140, 248, 0.26), rgba(15, 23, 42, 0.1));
  animation: wave 40s linear infinite;
}

.floating-particles {
  position: absolute;
  inset: 0;
}

.floating-particles span {
  position: absolute;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(226, 232, 240, 0.6);
  box-shadow: 0 0 16px rgba(148, 163, 184, 0.85);
  animation: particleFloat 12s linear infinite;
}

.floating-particles span:nth-child(1) { left: 12%; bottom: -10%; animation-delay: 0s; animation-duration: 11s; }
.floating-particles span:nth-child(2) { left: 28%; bottom: -6%; animation-delay: 2s; animation-duration: 14s; }
.floating-particles span:nth-child(3) { left: 46%; bottom: -12%; animation-delay: 1s; animation-duration: 13s; }
.floating-particles span:nth-child(4) { left: 62%; bottom: -7%; animation-delay: 3s; animation-duration: 15s; }
.floating-particles span:nth-child(5) { left: 78%; bottom: -14%; animation-delay: 4s; animation-duration: 12s; }
.floating-particles span:nth-child(6) { left: 90%; bottom: -9%; animation-delay: 5s; animation-duration: 16s; }

@keyframes driftSlow {
  0% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  100% {
    transform: translate3d(40px, -26px, 0) scale(1.08);
  }
}

@keyframes floatSoft {
  0%, 100% {
    transform: translate3d(0, 0, 0);
  }
  50% {
    transform: translate3d(20px, -16px, 0);
  }
}

@keyframes gridMove {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(60px);
  }
}

@keyframes pulseGlow {
  0%, 100% {
    transform: scale(1);
    opacity: 0.2;
  }
  50% {
    transform: scale(1.08);
    opacity: 0.3;
  }
}

@keyframes particleFloat {
  0% {
    transform: translate3d(0, 0, 0) scale(0.8);
    opacity: 0;
  }
  15% {
    opacity: 0.8;
  }
  85% {
    opacity: 0.4;
  }
  100% {
    transform: translate3d(-24px, -115vh, 0) scale(1.2);
    opacity: 0;
  }
}

.login-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 460px;
  padding: 20px;
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-card {
  background: linear-gradient(165deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.92) 100%);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 22px;
  padding: 34px 30px;
  box-shadow:
    0 24px 56px rgba(2, 6, 23, 0.34),
    0 10px 24px rgba(15, 23, 42, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(203, 213, 225, 0.8);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 5px;
    background: linear-gradient(90deg, #0f172a 0%, #1d4ed8 52%, #0f172a 100%);
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 12px;
    border-radius: 14px;
    border: 1px solid rgba(255, 255, 255, 0.4);
    pointer-events: none;
  }
}

.login-header {
  text-align: center;
  margin-bottom: 26px;
  position: relative;
}

.login-title {
  color: #0b1736;
  font-size: 34px;
  font-weight: 900;
  margin: 0 0 8px 0;
  letter-spacing: 0.4px;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.5);

  .el-icon-ship {
    margin-right: 10px;
    font-size: 34px;
    color: #0f172a;
  }
}

.login-subtitle {
  color: #5b6d86;
  font-size: 13px;
  margin: 0;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.login-form {
  position: relative;

  .el-form-item {
    margin-bottom: 16px;
  }

  :deep(.el-radio-group) {
    width: 100%;
    display: flex;
    gap: 10px;

    .el-radio-button__inner {
      border-radius: 12px;
      padding: 9px 14px;
      font-size: 12px;
      font-weight: 700;
      transition: all 0.2s ease;
      border: 1px solid #d3ddec;
      background: rgba(255, 255, 255, 0.72);
      color: #1e293b;
    }

    .el-radio-button:first-child .el-radio-button__inner {
      border-left: 2px solid #e4e7ed;
    }

    .el-radio-button__original-radio:checked + .el-radio-button__inner {
      background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
      border-color: #0f172a;
      box-shadow: none;
      transform: none;
    }
  }

  .el-input {
    :deep(.el-input__wrapper) {
      border-radius: 12px;
      box-shadow: none;
      min-height: 46px;
      transition: all 0.2s ease;
      border: 1px solid #d3ddec;
      background: rgba(255, 255, 255, 0.8);

      &:hover {
        border-color: #b7c9e6;
        box-shadow: 0 6px 14px rgba(148, 163, 184, 0.18);
      }

      &.is-focus {
        box-shadow:
          0 0 0 4px rgba(37, 99, 235, 0.14),
          0 8px 18px rgba(30, 64, 175, 0.12);
        border-color: #1d4ed8;
        transform: translateY(-1px);
      }
    }

    :deep(.el-input__inner) {
      font-size: 13px;
      color: #0f172a;
    }
  }
}

.login-btn {
  width: 100%;
  border-radius: 12px;
  height: 46px;
  font-size: 14px;
  font-weight: 800;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
  border: 1px solid #0f172a;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.3);
  transition: all 0.2s ease;
  letter-spacing: 0.6px;

  &:hover {
    transform: translateY(-1px);
    background: linear-gradient(135deg, #111c3e 0%, #1d4ed8 100%);
    border-color: #1e3a8a;
    box-shadow: 0 14px 24px rgba(15, 23, 42, 0.35);
  }

  &:active {
    transform: translateY(0);
  }
}

.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid #dbe4ef;

  .el-link {
    font-size: 12px;
    font-weight: 700;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }
}

@media (max-width: 768px) {
  .grid-overlay {
    background-size: 44px 44px;
  }

  .floating-particles span {
    width: 4px;
    height: 4px;
  }

  .login-content {
    max-width: 100%;
    padding: 15px;
  }

  .login-card {
    padding: 35px 25px;
  }

  .login-title {
    font-size: 32px;
  }
}
</style>
