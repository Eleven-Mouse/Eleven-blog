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

const themeStore = useThemeStore()

const isDarkMode = computed(() => themeStore.theme === 'dark')

const toggleTheme = (checked) => {
  if ((checked && themeStore.theme === 'dark') || (!checked && themeStore.theme === 'light')) {
    return
  }
  themeStore.toggleTheme()
}
</script>
