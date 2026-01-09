<template>
  <div>
    <el-card class="article-card">
      <div class="card-content">
        <h6 class="article-title">
          <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
        </h6>
        <p class="article-summary">{{ article.summary }}</p>
        <div class="article-meta">
          <span v-if="article.tags"
            ><el-icon><PriceTag /></el-icon> {{ article.tags }}</span
          >
          <span v-if="formattedDate"
            ><el-icon><Clock /></el-icon> {{ formattedDate }}</span
          >
          <span v-if="article.categoryName"
            ><el-icon><FolderOpened /></el-icon> {{ article.categoryName }}</span
          >
          <span v-if="article.viewCount"
          ><el-icon><View /></el-icon>{{ article.viewCount }}</span
          >
        </div>

        <div class="more">
          <router-link :to="`/article/${article.id}`">more</router-link>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Clock, FolderOpened, PriceTag,View  } from '@element-plus/icons-vue'
import { defineProps, computed } from 'vue'

const props = defineProps({
  article: {
    type: Object,
    required: true,
  },
})

// 格式化日期
const formattedDate = computed(() => {
  if (!props.article.publishTime) return ''
  const date = new Date(props.article.publishTime)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
})
</script>

<style scoped>
.article-card {
  margin-bottom: 10px;
  box-shadow: none;
  border: 0;
}

.article-title a {
  position: relative;
  color: #5b5b5b;
  text-decoration: none;
  font-size: 1.4rem;
  transition: color 0.3s;
}

/* 下划线动画 */
.article-title a::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -2px; /* 可以根据需求调整位置 */
  width: 0%;
  height: 2px;
  background-color: currentColor; /* 使用当前文字颜色 */
  transition: width 0.3s ease-in-out;
}

.article-title a:hover {
  color: #333;
}

.article-title a:hover::after {
  width: 100%;
}

.article-summary {
  color: #7d7d7d;
  line-height: 1.6;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 0.9em;
  color: #999999;
  margin-top: 15px;
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}
.more a {
  color: #7c7c7c;
  transition: color 0.3s;
  position: relative;
  float: inline-end;
  text-decoration: none;
  font-size: 1rem;
}

.more a:hover {
  color: #333;
}

/* 下划线动画 */
.more a::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -2px;
  width: 0%;
  height: 2px;
  background-color: currentColor;
  transition: width 0.3s ease-in-out;
}

.more a:hover::after {
  width: 100%;
}
</style>
