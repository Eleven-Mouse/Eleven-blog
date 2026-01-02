<template>
  <div class="common-layout">
    <el-container>
      <el-header style="position: fixed; width: 100%; z-index: 1000">
        <Header></Header>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
      <Footer></Footer>
    </el-container>
  </div>
</template>

<script setup>
import Footer from './components/common/Footer.vue'
import Header from './components/common/Header.vue'
import { onMounted } from 'vue'
import { useThemeStore } from './stores/theme'

const themeStore = useThemeStore()

onMounted(() => {
  themeStore.initTheme()
})
</script>

<style>
/* Dark mode using a semi-transparent overlay */
html[data-theme='dark']::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(16, 16, 27, 0.38); /* Adjust the opacity here */
  pointer-events: none; /* Make sure the overlay doesn't block interactions */
  z-index: 9999;
  transition: background-color 0.3s;
}

.el-header {
  background-color: var(--header-bg-color);
  backdrop-filter: blur(10px);
  color: var(--app-text-color);
  line-height: 40px;
  border-bottom: 1px solid var(--header-border-color);
  transition:
    background-color 0.3s,
    border-color 0.3s;
}

.el-main {
  min-height: calc(100vh - 60px);

}
</style>
