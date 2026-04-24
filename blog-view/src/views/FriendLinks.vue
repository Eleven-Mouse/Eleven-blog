<template>
  <div class="friends-page page-container">
    <div class="page-header">
      <h1 class="page-header__title">友链</h1>
      <p class="page-header__desc">海内存知己，天涯若比邻</p>
    </div>

    <div v-if="loading" class="loading-tip">正在加载...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div v-if="friends.length" class="friends-grid">
      <a
        v-for="(item, index) in friends"
        :key="item.id"
        :href="item.url"
        target="_blank"
        rel="noopener noreferrer"
        class="friend-card modern-card stagger-item"
        :style="{ animationDelay: `${index * 0.06}s` }"
      >
        <div class="friend-card__avatar">
          <img :src="getAvatarUrl(item.logo)" :alt="item.name" loading="lazy" @error="handleImgError" />
        </div>
        <div class="friend-card__info">
          <div class="friend-card__name">{{ item.name }}</div>
          <div v-if="item.description" class="friend-card__desc">{{ item.description }}</div>
        </div>
        <svg class="friend-card__arrow" viewBox="0 0 16 16" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M4 12L12 4M12 4H6M12 4v6"/></svg>
      </a>
    </div>

    <el-empty v-else-if="!loading" description="暂无友链" />

    <div class="friends-comments">
      <CommentsCard :page="'friends'" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getFriendLinks } from '../api/friendLink'
import CommentsCard from '@/components/CommentsCard.vue'
import defaultAvatar from '../assets/(5).png'

const friends = ref([])
const loading = ref(false)
const error = ref(null)
const BASE_URL = import.meta.env.VITE_APP_UPLOAD_URL || ''

const getAvatarUrl = (logoPath) => {
  if (!logoPath) return defaultAvatar
  const path = logoPath.split(',')[0]
  if (path.startsWith('http')) return path
  return BASE_URL + path
}

const handleImgError = (e) => {
  e.target.onerror = null
  e.target.src = defaultAvatar
}

onMounted(async () => {
  loading.value = true
  try {
    const data = await getFriendLinks()
    friends.value = data || []
  } catch (err) {
    error.value = '获取友链数据失败'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 18px;
}

.friend-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 22px;
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal),
    border-color var(--transition-normal);
}

.friend-card:hover {
  transform: translateY(-3px) scale(1.01);
  border-color: rgba(var(--accent-rgb), 0.15);
}

.friend-card__avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  transition: box-shadow var(--transition-fast);
}

.friend-card:hover .friend-card__avatar img {
  box-shadow: 0 0 0 2px var(--accent);
}

.friend-card__info {
  flex: 1;
  min-width: 0;
}

.friend-card__name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  transition: color var(--transition-fast);
}

.friend-card:hover .friend-card__name {
  color: var(--accent);
}

.friend-card__desc {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend-card__arrow {
  color: var(--text-muted);
  flex-shrink: 0;
  opacity: 0;
  transform: translate(-4px, 4px);
  transition: all var(--transition-fast);
}

.friend-card:hover .friend-card__arrow {
  opacity: 1;
  transform: translate(0, 0);
  color: var(--accent);
}

.friends-comments {
  margin-top: 48px;
}

@media (max-width: 768px) {
  .friends-grid {
    grid-template-columns: 1fr;
  }
}
</style>
