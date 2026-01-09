<template>
  <div>
    <el-text class="mx-1">
      <el-card class="info-card">
        <div class="avatar-section">
          <el-avatar shape="square" :size="100" :src="squareUrl" />
        </div>
        <div class="info-section">
          <h3>Eleven-Mouse</h3>
          <p>will yow?</p>
        </div>
        <div class="social-links">
          <a href="#" target="_blank"
            ><el-icon><ChatLineRound /></el-icon
          ></a>
          <a href="#"
            ><el-icon><Message /></el-icon
          ></a>
          <a href="#"
            ><el-icon><Share /></el-icon
          ></a>
        </div> </el-card
    ></el-text>
  </div>
</template>

<script setup>
import { ChatLineRound, Message, Share } from '@element-plus/icons-vue'
import { reactive, toRefs } from 'vue'
const state = reactive({
  squareUrl: '/avatar.png',
})

const { squareUrl } = toRefs(state)
</script>

<style scoped>
.info-card {
  text-align: center;
  background-color: var(--card-bg-color);
  border: 1px solid var(--card-border-color);
  box-shadow: var(--card-box-shadow);
  transition:
    background-color 0.3s,
    border-color 0.3s;
  box-shadow: none;
  border: 0;
}

.avatar-section {
  margin-bottom: 15px;
}

.info-section h3 {
  margin: 10px 0;
  font-size: 1rem;
  color: var(--app-text-color);
  transition: color 0.3s;
}

.info-section p {
  font-size: 0.9rem;
  color: var(--app-text-color);
  margin-bottom: 20px;
  transition: color 0.3s;
}

.social-links a {
  margin: 0 15px;
  font-size: 24px;
  color: var(--app-text-color);
  text-decoration: none;
  transition: color 0.3s;
}

.social-links a:hover {
  color: var(--link-hover-color);
}
</style>
