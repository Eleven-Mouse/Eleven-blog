<script setup>
import CommentsCard from '@/components/CommentsCard.vue'
</script>

<template>
  <div class="about-container">
    <el-text class="mx-1">
      <div class="about-header">
        <h3 class="title-asd">关于</h3>
      </div>
      <div class="about-main">
        
      </div>
    </el-text>
  </div>
</template>

<style scoped>

.about-container {
  width: 650px;
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
  width: 100px;
  height: 100px;
}
</style>
