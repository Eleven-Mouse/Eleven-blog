<template>
  <div class="moment-page page-container">
    <div class="page-header">
      <h1 class="page-header__title">动态</h1>
      <p class="page-header__desc">记录生活中的点滴</p>
    </div>

    <div class="moment-list">
      <el-skeleton :rows="5" animated v-if="loading && moments.length === 0" />

      <div v-for="moment in moments" :key="moment.id" class="moment-card modern-card stagger-item">
        <div class="moment-card__time">{{ formatDate(moment.createTime) }}</div>
        <p class="moment-card__content">{{ moment.content }}</p>
        <div v-if="moment.image" class="moment-card__images">
          <el-image
            v-for="(img, index) in parseImages(moment.image)"
            :key="index"
            :src="img"
            :preview-src-list="parseImages(moment.image)"
            fit="cover"
            lazy
            class="moment-card__img"
          />
        </div>
      </div>

      <el-empty v-if="moments.length === 0 && !loading && !error" description="暂无动态" />
    </div>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="prev, pager, next"
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
const pagination = ref({ page: 1, size: 10, total: 0 })

const formatDate = (datetimeString) => {
  if (!datetimeString) return ''
  return new Date(datetimeString).toLocaleString()
}

const parseImages = (imageStr) => {
  if (!imageStr) return []
  return imageStr.split(',').map(path =>
    path.startsWith('http') ? path : BASE_URL + path
  )
}

const getMomentsList = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await getMoments({ page: pagination.value.page, size: pagination.value.size })
    moments.value = res.list || []
    pagination.value.total = res.total || 0
  } catch (err) {
    error.value = '获取动态数据失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

onMounted(() => { getMomentsList() })
</script>

<style scoped>
.moment-list {
  max-width: 700px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.moment-card {
  padding: 22px 26px;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
}

.moment-card:hover {
  transform: translateY(-2px);
}

.moment-card__time {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 12px;
  font-weight: 500;
}

.moment-card__content {
  font-size: 15.5px;
  color: var(--text-primary);
  line-height: 1.8;
  margin: 0;
}

.moment-card__images {
  display: flex;
  gap: 8px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.moment-card__img {
  height: 180px;
  border-radius: var(--radius-md);
  flex: 1;
  min-width: 120px;
  overflow: hidden;
}

@media (max-width: 768px) {
  .moment-card {
    padding: 16px;
  }

  .moment-card__img {
    height: 140px;
  }
}
</style>
