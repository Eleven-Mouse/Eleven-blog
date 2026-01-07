<template>
  <div class="common-layout">
    <el-main>
      <router-view :key="route.fullPath" />
    </el-main>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.el-main {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  font-family: Georgia, 'Times New Roman', Times, serif;
}
</style>
