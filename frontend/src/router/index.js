import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入页面组件
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Assessment from '../views/Assessment.vue'
import AssessmentTaking from '../views/AssessmentTaking.vue'
import AssessmentResult from '../views/AssessmentResult.vue'
import MessageBottle from '../views/MessageBottle.vue'
import AIInteraction from '../views/AIInteraction.vue'
import Contact from '../views/Contact.vue'
import UserProfile from '../views/UserProfile.vue'
import PsychologistDashboard from '../views/PsychologistDashboard.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import ExpertConsultation from '../views/ExpertConsultation.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录 - 星语航海' }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { 
      title: '首页 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/assessment',
    name: 'Assessment',
    component: Assessment,
    meta: { 
      title: '心理测评 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/assessment/taking/:id',
    name: 'AssessmentTaking',
    component: AssessmentTaking,
    meta: { 
      title: '正在测评 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/assessment/result/:id',
    name: 'AssessmentResult',
    component: AssessmentResult,
    meta: { 
      title: '测评结果 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/message-bottle',
    name: 'MessageBottle',
    component: MessageBottle,
    meta: { 
      title: '漂流瓶 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/ai-interaction',
    name: 'AIInteraction',
    component: AIInteraction,
    meta: { 
      title: 'AI互动 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/contact',
    name: 'Contact',
    component: Contact,
    meta: { 
      title: '联系我们 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/expert-consultation',
    name: 'ExpertConsultation',
    component: ExpertConsultation,
    meta: { 
      title: '专家咨询 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: UserProfile,
    meta: { 
      title: '个人中心 - 星语航海',
      requiresAuth: true 
    }
  },
  {
    path: '/psychologist/dashboard',
    name: 'PsychologistDashboard',
    component: PsychologistDashboard,
    meta: { 
      title: '专家工作台 - 星语航海',
      requiresAuth: true,
      requiresRole: 'psychologist'
    }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { 
      title: '管理后台 - 星语航海',
      requiresAuth: true,
      requiresRole: 'admin'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 权限控制
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    // 如果用户未登录，跳转到登录页
    if (!userInfo.id) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }

    // 检查角色权限
    if (to.meta.requiresRole && userInfo.role !== to.meta.requiresRole) {
      ElMessage.error('权限不足')
      next(from.path || '/home')
      return
    }
  }

  next()
})

// 路由后置钩子 - 可以在这里添加一些全局逻辑
router.afterEach((to, from) => {
  // 可以在这里添加页面访问统计等逻辑
})

export default router