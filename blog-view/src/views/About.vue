<template>
  <div class="about-container">
    <el-text class="mx-1">
      <div class="about-header">
        <h1>关于</h1>
        <h2>| Eleven-blog |</h2>
      </div>
      <div class="about-main">
        <my-bot-ui />
      </div>
    </el-text>
  </div>
</template>
<script setup>
import MyBotUi from '@/components/MyBotUI/MyBotUi.vue'
</script>

<style scoped>
.about-container {
  width: 900px;
  padding: 20px 0;
  border-top: 1px solid var(--card-border-color, #3a3a3a);
  border-bottom: 1px solid var(--card-border-color, #3a3a3a);
  margin: 40px auto;
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.about-header {
  text-align: center;
  margin-bottom: 40px;
}
.about-main {
  width: 900px;
  height: 1000px;
}
</style>
