<template>
  <div class="moment-container">
    <el-text class="mx-1">
      <div class="about-header">
        <h1>动态</h1>
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
              style="height: 220px; border-radius: 6px"
            />
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <!-- 没有更多动态或初始空状态 -->
    <el-empty description="暂无动态" v-if="moments.length === 0 && !loading && !error" />

    <div style="margin-top: 20px; display: flex; justify-content: center">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="prev, pager, next"
        @size-change="getMomentsList"
        @current-change="getMomentsList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMoments } from '@/api/moment'
import { ElMessage } from 'element-plus'
const moments = ref([])
const loading = ref(false)
const error = ref(null)
const BASE_URL = import.meta.env.VITE_APP_UPLOAD_URL
const pagination = ref({
  page: 1,
  size: 10,
  total: 0,
})
// 格式化日期时间
const formatDate = (datetimeString) => {
  if (!datetimeString) return ''
  const date = new Date(datetimeString)
  return date.toLocaleString()
}

const parseImages = (imageStr) => {
  if (!imageStr) return []

  const paths = imageStr.split(',')

  return paths.map((path) => {
    if (path.startsWith('http')) {
      return path
    }
    return BASE_URL + path
  })
}

// 获取动态数据
const getMomentsList = async () => {
  loading.value = true
  error.value = null
  try {
    const params = {
      page: pagination.value.page,
      size: pagination.value.size,
    }
    const res = await getMoments(params)
    moments.value = res.list || []
    pagination.value.total = res.total || 0
  } catch (err) {
    error.value = '获取动态数据失败，请稍后再试。'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 初始化加载
onMounted(() => {
  getMomentsList()
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
