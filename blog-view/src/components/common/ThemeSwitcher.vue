<template>
  <el-switch
    :model-value="isDarkMode"
    @change="toggleTheme"
    inline-prompt
    :active-icon="Moon"
    :inactive-icon="Sunny"
    style="--el-switch-on-color: #2a2d3a; --el-switch-off-color: #d1d5db;"
  />
</template>

<script setup>
import { computed } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { triggerSilentGithubSync } from '@/api/sync'

const themeStore = useThemeStore()

const isDarkMode = computed(() => themeStore.theme === 'dark')

const emitTopicsRefresh = () => {
  window.dispatchEvent(new Event('blog:topics-refresh'))
}

const toggleTheme = (checked) => {
  if ((checked && themeStore.theme === 'dark') || (!checked && themeStore.theme === 'light')) {
    return
  }
  themeStore.toggleTheme()
  // 非阻塞触发同步和分类刷新，不阻塞主题切换 UI
  triggerSilentGithubSync()
    .then(() => emitTopicsRefresh())
    .catch(() => emitTopicsRefresh())
}
</script>
