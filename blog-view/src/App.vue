<template>
  <el-container style="height: 97vh">
    <el-header style="height: 60px"> <Header></Header> </el-header>

    <el-main
      style="overflow-y: auto; display: block; justify-content: center"
      class="main-scroll-container"
    >
      <router-view /> <Footer></Footer>
    </el-main>
  </el-container>
</template>

<script setup>
import Header from './components/common/Header.vue'
import Footer from '@/components/common/Footer.vue'
</script>

<style>
html[data-theme='dark']::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(16, 16, 27, 0.38);
  pointer-events: none;
  z-index: 9999;
  transition: background-color 0.3s;
}
.el-header {
  top: 0;
  z-index: 2000;
  background-color: white;
}
.el-main {
  scrollbar-width: 0;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    width: 0;
    height: 0;
  }
}
</style>
