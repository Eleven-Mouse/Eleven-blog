<template>
  <div class="moment-container">
    <el-text class="mx-1">
      <div class="about-header">
        <h3 class="title-asd">动态</h3>
      </div>
    </el-text>
    <!-- 加载中状态 -->
    <el-skeleton :rows="5" animated v-if="loading && moments.length === 0" />
    <!-- 错误提示 -->

    <el-timeline style="max-width: 800px; margin: 0 auto" v-if="moments.length > 0 && !loading">
      <el-timeline-item
        v-for="moment in moments"
        :key="moment.id"
        :timestamp="formatDate(moment.createTime)"
        placement="top"
        center
      >
        <el-card class="moment-card" shadow="hover">
          <!-- 动态内容 -->
          <p class="moment-content">{{ moment.content }}</p>

          <!-- 图片列表（如果存在） -->
          <div v-if="moment.image" class="moment-image">
            <el-image
              v-for="(img, index) in parseImages(moment.image)"
              :key="index"
              :src="img"
              :preview-src-list="parseImages(moment.image)"
              fit="cover"
              lazy
              class="moment-image"
            />
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <!-- 没有更多动态或初始空状态 -->
    <el-empty description="暂无动态" v-if="moments.length === 0 && !loading && !error" />

    <!-- 加载更多按钮或底部加载提示 -->
    <div class="load-more" v-if="moments.length > 0 && hasMore">
      <el-button type="primary" :loading="loading" @click="loadMoreMoments" plain>
        {{ loading ? '加载中...' : '加载更多' }}
      </el-button>
    </div>
    <div class="no-more-data" v-else-if="moments.length > 0 && !hasMore">
      <el-divider> <span>没有更多了</span></el-divider>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchMoments } from '@/api/moment'
import { ElMessage } from 'element-plus' // 用于错误提示
const moments = ref([])
const loading = ref(false)
const error = ref(null)
const currentPage = ref(1)
const pageSize = 10
const hasMore = ref(true)

// 格式化日期时间
const formatDate = (datetimeString) => {
  if (!datetimeString) return ''
  const date = new Date(datetimeString)
  return date.toLocaleString()
}

// 解析图片字符串（假设是逗号分隔）
const parseImages = (imagesString) => {
  if (!imagesString) return []
  // 假设 images 字段是 "url1,url2,url3"
  return imagesString
    .split(',')
    .map((url) => url.trim())
    .filter((url) => url)
}

// 获取动态数据
const getMoments = async () => {
  if (!hasMore.value && currentPage.value > 1) return
  if (loading.value) return

  loading.value = true
  error.value = null
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
    }
    const res = await fetchMoments(params)

    const newMoments = res || []
    if (currentPage.value === 1) {
      moments.value = newMoments
    } else {
      moments.value = [...moments.value, ...newMoments]
    }

    if (newMoments.length < pageSize) {
      hasMore.value = false
    } else {
      hasMore.value = true
    }
  } catch (err) {
    error.value = '获取动态数据失败，请稍后再试。'
    ElMessage.error(error.value)
    console.error('Failed to fetch moments:', err)
  } finally {
    loading.value = false
  }
}

// 加载更多动态
const loadMoreMoments = () => {
  if (hasMore.value && !loading.value) {
    currentPage.value++
    getMoments()
  }
}

// 初始化加载
onMounted(() => {
  getMoments()
})
</script>

<style scoped>
.moment-container {
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
</style>
