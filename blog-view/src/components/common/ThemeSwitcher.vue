<template>
  <el-switch
    v-model="isDarkMode"
    @change="toggleTheme"
    inline-prompt
    :active-icon="Moon"
    :inactive-icon="Sunny"
    style="--el-switch-on-color: #4C4D4F; --el-switch-off-color: #C0C4CC;"
  />
</template>

<script setup>
import { computed } from 'vue';
import { useThemeStore } from '@/stores/theme';
import { Sunny, Moon } from '@element-plus/icons-vue';

const themeStore = useThemeStore();

const isDarkMode = computed(() => themeStore.theme === 'dark');

const toggleTheme = () => {
  themeStore.toggleTheme();
};
</script>

