<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <header class="layout-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="logo">
            <i class="el-icon-ship"></i>
            星语航海
          </h1>
          <span class="slogan">专业心理测评与咨询平台</span>
        </div>
        
        <nav class="header-nav">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            class="nav-menu"
            @select="handleMenuSelect"
          >
            <!-- 首页导航 -->
            <el-menu-item index="/home">
              <i class="el-icon-house"></i>
              首页
            </el-menu-item>
            
            <!-- 普通用户与管理员可见 -->
            <template v-if="userStore.userRole !== 'psychologist'">
              <el-sub-menu index="assessment">
                <template #title>
                  <i class="el-icon-document"></i>
                  心理测评
                </template>
                <el-menu-item index="/assessment">测评中心</el-menu-item>
                <el-menu-item index="/assessment/history">历史记录</el-menu-item>
              </el-sub-menu>
              
              <el-menu-item index="/message-bottle">
                <i class="el-icon-chat-dot-round"></i>
                漂流瓶
              </el-menu-item>
              
              <el-sub-menu index="ai">
                <template #title>
                  <i class="el-icon-ai"></i>
                  AI互动
                </template>
                <el-menu-item index="/ai-interaction">AI心理助手</el-menu-item>
                <el-menu-item index="/ai-interaction/assessment">AI心理评估</el-menu-item>
              </el-sub-menu>
            </template>
            
            <!-- 心理专家导航 -->
            <template v-if="userStore.userRole === 'psychologist'">
              <el-menu-item index="/psychologist/dashboard">
                <i class="el-icon-suitcase"></i>
                工作台
              </el-menu-item>
              
              <el-sub-menu index="expert">
                <template #title>
                  <i class="el-icon-user"></i>
                  专家服务
                </template>
                <el-menu-item index="/psychologist/patients">患者管理</el-menu-item>
                <el-menu-item index="/psychologist/appointments">预约日程</el-menu-item>
                <el-menu-item index="/psychologist/consultations">咨询记录</el-menu-item>
              </el-sub-menu>
            </template>
            
            <!-- 管理员导航 -->
            <template v-if="userStore.userRole === 'admin'">
              <el-menu-item index="/admin/dashboard">
                <i class="el-icon-settings"></i>
                管理后台
              </el-menu-item>
              
              <el-sub-menu index="admin">
                <template #title>
                  <i class="el-icon-monitor"></i>
                  系统管理
                </template>
                <el-menu-item index="/admin/users">用户管理</el-menu-item>
                <el-menu-item index="/admin/experts">专家管理</el-menu-item>
                <el-menu-item index="/admin/system">系统设置</el-menu-item>
              </el-sub-menu>
            </template>
            
            <!-- 联系我们 -->
            <el-menu-item index="/contact">
              <i class="el-icon-phone"></i>
              联系我们
            </el-menu-item>
          </el-menu>
        </nav>
        
        <div class="header-right">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="safeUserAvatar" />
              <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <i class="el-icon-setting"></i>设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="layout-main">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="layout-footer">
      <div class="footer-content">
        <div class="footer-info">
          <p>© 2025 星语航海心理测评与咨询平台</p>
          <p>客服邮箱: service@xingyu.com | 客服热线: 400-123-4567</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const API_BASE = 'http://localhost:8080'
const INVALID_AVATAR_NAMES = new Set(['non.jpg', 'none.jpg', 'null.jpg', 'undefined.jpg', 'zhon.jpg'])

const createInitialAvatar = (name = '用') => {
  const text = (name || '用').charAt(0)
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="64" height="64"><rect width="100%" height="100%" fill="#1d4ed8"/><text x="50%" y="56%" text-anchor="middle" fill="#ffffff" font-size="28" font-family="Microsoft YaHei, Arial" font-weight="700">${text}</text></svg>`
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`
}

const resolveAvatarUrl = (path, name = '用') => {
  if (!path || typeof path !== 'string') return createInitialAvatar(name)
  const cleaned = path.trim()
  const fileName = cleaned.split('/').pop()?.toLowerCase() || ''
  if (INVALID_AVATAR_NAMES.has(fileName)) return createInitialAvatar(name)
  if (/^https?:\/\//.test(cleaned) || cleaned.startsWith('data:')) return cleaned
  if (cleaned.startsWith('/')) return `${API_BASE}${cleaned}`
  return `${API_BASE}/${cleaned}`
}

const activeMenu = computed(() => {
  return route.path
})

const safeUserAvatar = computed(() =>
  resolveAvatarUrl(userStore.userInfo?.avatar, userStore.userInfo?.realName || userStore.userInfo?.username || '用户')
)

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      // 根据不同角色进入对应个人中心
      if (userStore.userRole === 'admin') {
        router.push('/admin/profile')
      } else if (userStore.userRole === 'psychologist') {
        router.push('/psychologist/profile')
      } else {
        router.push('/user/profile')
      }
      break
    case 'settings':
      ElMessage.info('设置功能开发中')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确认要退出登录吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        userStore.logout()
        // 已确认退出登录
        router.push('/login')
      } catch {
        // 用户取消
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  background: #f6f7fb;
}

/* 背景装饰：星空与波纹 */
.layout-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url('../assets/dashboard/hero-stars.svg') no-repeat right -60px / 520px;
  pointer-events: none;
  z-index: 0;
}
.layout-container::after {
  content: '';
  position: absolute;
  inset: 0;
  background: url('../assets/dashboard/hero-waves.svg') no-repeat left bottom / 600px;
  opacity: 0.9;
  pointer-events: none;
  z-index: 0;
}

/* 内容层级在背景之上 */
.layout-header,
.layout-main,
.layout-footer {
  position: relative;
  z-index: 1;
}
.layout-header {
  background: rgba(15, 23, 42, 0.94);
  color: #e2e8f0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.26);
  box-shadow: 0 8px 24px rgba(2, 6, 23, 0.18);
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(8px);
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 66px;
}

.header-left {
  display: flex;
  align-items: center;
  
  .logo {
    font-size: 22px;
    font-weight: 800;
    margin: 0 16px 0 0;
    
    .el-icon-ship {
      margin-right: 8px;
      font-size: 24px;
    }
  }
  
  .slogan {
    font-size: 12px;
    color: #94a3b8;
    font-weight: 600;
  }
}

.header-nav {
  flex: 1;
  margin: 0 40px;
  
  .nav-menu {
    background: transparent;
    border: none;
    
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      color: #cbd5e1;
      height: 66px;
      line-height: 66px;
      font-size: 13px;
      font-weight: 700;
      
      &:hover {
        background: rgba(148, 163, 184, 0.16);
        color: #f8fafc;
      }
      
      &.is-active {
        background: rgba(148, 163, 184, 0.22);
        color: #f8fafc;
        border-bottom: 2px solid #f8fafc;
      }
    }
  }
}

.header-right {
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 999px;
    border: 1px solid rgba(148, 163, 184, 0.28);
    background: rgba(30, 41, 59, 0.8);
    transition: all 0.2s ease;
    
    &:hover {
      background: rgba(30, 41, 59, 1);
      border-color: rgba(191, 219, 254, 0.5);
    }
    
    .username {
      margin: 0 8px;
      font-size: 12px;
      font-weight: 700;
      color: #e2e8f0;
    }
    
    .el-icon-arrow-down {
      font-size: 12px;
    }
  }
}

.layout-main {
  flex: 1;
  padding: 20px;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}

.layout-footer {
  background: #ffffff;
  border-top: 1px solid #dde3ec;
  margin-top: auto;
  
  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
    color: #64748b;
    
    p {
      margin: 4px 0;
      font-size: 12px;
      font-weight: 600;
    }
  }
}

// 响应式布局
@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    height: auto;
    padding: 10px;
  }
  
  .header-nav {
    order: 3;
    margin: 10px 0 0 0;
    width: 100%;
    
    .nav-menu {
      :deep(.el-menu-item),
      :deep(.el-sub-menu__title) {
        height: 48px;
        line-height: 48px;
        font-size: 14px;
      }
    }
  }
  
  .slogan {
    display: none;
  }
}
</style>
