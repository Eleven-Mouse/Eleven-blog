<template>
  <div class="app-root" :class="{ 'is-dark': isDark }">
    <AppNavbar />
    <main class="app-main">
      <router-view v-slot="{ Component, route }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useBlogConfigStore } from '@/stores/blogConfig'
import AppNavbar from '@/components/common/Header.vue'
import AppFooter from '@/components/common/Footer.vue'

const themeStore = useThemeStore()
const blogConfigStore = useBlogConfigStore()
const isDark = computed(() => themeStore.theme === 'dark')

onMounted(() => {
  themeStore.initTheme()
  blogConfigStore.fetchConfig()
})
</script>

<style scoped>
.app-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  transition: background-color 0.3s, color 0.3s;
}

.app-main {
  flex: 1;
  width: 100%;
  padding-top: 56px;
}
</style>
