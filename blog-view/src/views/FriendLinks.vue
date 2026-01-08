<template>
  <div class="container">
    <div class="links">
      <el-text class="mx-1">
        <h3 class="friendslinks-title">友链</h3>

        <!-- 加载状态 -->
        <div v-if="loading">正在加载...</div>
        <div v-if="error">{{ error }}</div>

        <div v-if="frindslinksdata.length" class="friendslinks-list">
          <el-row :gutter="20">
            <el-col
              v-for="item in frindslinksdata"
              :key="item.id"
              :xs="24"
              :sm="12"
              :md="6"
              :lg="6"
              style="margin-bottom: 20px"
            >
              <el-card class="friendscard" shadow="hover" @click="openFriend(item)">
                <div class="card-content">
                  <!-- 头像部分 -->
                  <img
                    :src="getAvatarUrl(item.logo)"
                    :alt="item.name"
                    class="friendslink-avatar"
                    @error="handleImgError"
                    loading="lazy"
                  />

                  <div class="info-content">
                    <div class="friendslink-name">{{ item.name }}</div>
                    <div
                      v-if="item.description"
                      class="friendslink-description"
                      :title="item.description"
                    >
                      {{ item.description }}
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 空状态 -->
        <el-empty v-else description="暂无友链" />
      </el-text>
    </div>

    <comments-card :page="'friends'" />
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import { getFriendLinks } from '../api/friendLink'

import CommentsCard from '@/components/CommentsCard.vue'
const frindslinksdata = ref([])
const loading = ref(false)
const error = ref(null)
const BASE_URL = import.meta.env.VITE_APP_UPLOAD_URL || ''
import defaultAvatar from '../assets/(5).png'

const getAvatarUrl = (logoPath) => {
  if (!logoPath) return defaultAvatar

  let path = logoPath.split(',')[0]

  if (path.startsWith('http') || path.startsWith('https')) {
    return path
  }

  return BASE_URL + path
}
//获取友情链接列表信息
const getFrindslinksList = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await getFriendLinks()
    frindslinksdata.value = data || []
  } catch (err) {
    error.value = '获取友链数据失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}
// 图片加载失败时的处理
const handleImgError = (e) => {
  // 防止死循环：如果默认图也挂了，就不再触发 error
  e.target.onerror = null
  e.target.src = defaultAvatar
}

const openFriend = (item) => {
  if (item.url) {
    window.open(item.url, '_blank')
  }
}
onMounted(() => {
  getFrindslinksList()
})
</script>
<style scoped>
.container {
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
}
.links {
  padding: 20px 0;
  border-top: 1px solid var(--card-border-color, #3a3a3a);
  border-bottom: 1px solid var(--card-border-color, #3a3a3a);
  margin: 40px 0;
  width: 750px;
}

.el-row {
  margin-bottom: 20px;
}
.el-row:last-child {
  margin-bottom: 0;
}
.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.friendslinks-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--app-text-color, #545252);
  margin-top: 0;
  margin-bottom: 25px;
  text-align: center;
}

.friendscard {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 50px;
  margin: 10px 0;
  border-radius: 10%;
  cursor: pointer;
  box-shadow: none;
  border: 0;
}

.friendslink-avatar {
  width: 100px;
  height: 100px;
  border-radius: 10%;
  object-fit: cover;
}

.friendslink-name {
  font-size: 14px;
  color: var(--app-text-color, #545252);
  text-align: center;
}

.friendslink-description {
  font-size: 12px;
  color: #888;
  text-align: center;
  margin-top: 4px;
  line-height: 1.4;
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
</style>
