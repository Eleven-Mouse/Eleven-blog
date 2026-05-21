<template>
  <el-switch
    v-model="isDarkMode"
    @change="toggleTheme"
    inline-prompt
    :active-icon="Moon"
    :inactive-icon="Sunny"
    style="--el-switch-on-color: #2a2d3a; --el-switch-off-color: #d1d5db;"
  />
</template>

<script setup>
import { computed } from 'vue';
import { useThemeStore } from '@/stores/theme';
import { Sunny, Moon } from '@element-plus/icons-vue';
import { triggerSilentGithubSync } from '@/api/sync';

const themeStore = useThemeStore();

const isDarkMode = computed(() => themeStore.theme === 'dark');

const toggleTheme = () => {
  themeStore.toggleTheme();
  // 静默触发文章自动同步，不阻塞主题切换，也不提示用户
  triggerSilentGithubSync().catch(() => {});
};
</script>
