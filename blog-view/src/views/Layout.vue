<template>
  <div class="layout">
    <router-view :key="route.fullPath" />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
.layout {
  width: 100%;
  min-height: calc(100vh - 60px);
}
</style>
