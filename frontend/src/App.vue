<template>
  <div id="app">
    <router-view v-slot="{ Component, route }">
      <transition name="route-fade-slide" mode="out-in">
        <component :is="Component" :key="route.fullPath" />
      </transition>
    </router-view>
    <button
      class="theme-toggle-fab"
      type="button"
      :aria-label="isDark ? '切换为浅色模式' : '切换为深色模式'"
      @click="toggleTheme"
    >
      <span class="theme-toggle-icon">{{ isDark ? '🌙' : '☀️' }}</span>
      <span class="theme-toggle-text">{{ isDark ? '深色' : '浅色' }}</span>
    </button>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isDark = ref(false)

const applyTheme = (dark) => {
  document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light')
}

const toggleTheme = () => {
  isDark.value = !isDark.value
}

onMounted(() => {
  // 检查本地存储的用户登录状态
  userStore.checkLoginStatus()

  const storedTheme = localStorage.getItem('app-theme')
  const systemPrefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = storedTheme ? storedTheme === 'dark' : systemPrefersDark
  applyTheme(isDark.value)
})

watch(isDark, (dark) => {
  applyTheme(dark)
  localStorage.setItem('app-theme', dark ? 'dark' : 'light')
})
</script>