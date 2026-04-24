<template>
  <div class="about-page page-container">
    <div class="page-header">
      <h1 class="page-header__title">关于</h1>
      <p class="page-header__desc">| Eleven-blog |</p>
    </div>

    <div class="about-page__card modern-card">
      <MyBotUi />
    </div>
  </div>
</template>

<script setup>
import MyBotUi from '@/components/MyBotUI/MyBotUi.vue'
</script>

<style scoped>
.about-page__card {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  overflow: hidden;
  min-height: 600px;
  border-color: var(--border-light);
}

.about-page__card :deep(.bot-container) {
  margin: 16px auto;
}

@media (max-width: 768px) {
  .about-page__card {
    padding: 16px;
    min-height: 400px;
  }
}
</style>
