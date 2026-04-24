<template>
  <div class="sidebar-cats modern-card">
    <div class="section-title">分类</div>
    <div v-if="loading" class="sidebar-cats__loading">加载中...</div>
    <div v-if="error" class="sidebar-cats__error">{{ error }}</div>
    <div v-if="categories.length" class="sidebar-cats__list">
      <router-link
        v-for="cat in categories"
        :key="cat.id"
        :to="`/category/${cat.id}`"
        class="sidebar-cats__item"
      >
        <span class="sidebar-cats__name">{{ cat.name }}</span>
        <span class="sidebar-cats__arrow">&rarr;</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchCategories } from '@/api/categories'

const categories = ref([])
const loading = ref(false)
const error = ref(null)

const getCategories = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await fetchCategories()
    categories.value = data || []
  } catch (err) {
    error.value = '获取分类失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => { getCategories() })
</script>

<style scoped>
.sidebar-cats {
  padding: 20px;
}

.sidebar-cats__list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.sidebar-cats__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: all 0.15s;
}

.sidebar-cats__item:hover {
  color: var(--accent);
  background: var(--accent-light);
}

.sidebar-cats__name {
  font-weight: 500;
}

.sidebar-cats__arrow {
  font-size: 12px;
  opacity: 0;
  transform: translateX(-4px);
  transition: all 0.15s;
}

.sidebar-cats__item:hover .sidebar-cats__arrow {
  opacity: 1;
  transform: translateX(0);
}

.sidebar-cats__loading,
.sidebar-cats__error {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  padding: 8px 0;
}
</style>
