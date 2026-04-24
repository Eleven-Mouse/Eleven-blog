<template>
  <div class="category-page page-container">
    <div class="page-header">
      <h1 class="page-header__title">分类</h1>
      <p class="page-header__desc">按分类浏览文章</p>
    </div>

    <CategoriesDirectoryCard />
  </div>
</template>

<script setup>
import CategoriesDirectoryCard from '@/components/CategoriesDirectoryCard.vue'
</script>

<style scoped>
</style>
