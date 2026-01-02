<template>
  <div class="container">
    <div class="links">
      <el-text class="mx-1">
        <h3 class="friendslinks-title">友链</h3>
        <div v-if="loading">正在加载...</div>
        <div v-if="error">{{ error }}</div>
        <ul v-if="frindslinksdata.length" class="friendslinks-list">
          <el-row :gutter="20">
            <el-col :span="6"
              ><div class="grid-content ep-bg-purple">
                <el-card
                  class="friendscard"
                  shadow="hover"
                  v-for="friendslink in frindslinksdata.slice(
                    0,
                    Math.ceil(frindslinksdata.length / 4),
                  )"
                  :key="friendslink.id"
                  @click="openFriend(friendslink)"
                >
                  <img
                    v-if="friendslink.logo"
                    :src="friendslink.logo"
                    :alt="friendslink.name"
                    class="friendslink-avatar"
                    @error="(e) => (e.target.src = 'src/assets/ (11).png')"
                    loading="lazy"
                  />
                  <br />
                  <span class="friendslink-name">{{ friendslink.name }}</span>
                  <br />
                  <span v-if="friendslink.description" class="friendslink-description">
                    {{ friendslink.description }}
                  </span>
                </el-card>
              </div></el-col
            >

            <el-col :span="6"
              ><div class="grid-content ep-bg-purple">
                <el-card
                  class="friendscard"
                  shadow="hover"
                  v-for="friendslink in frindslinksdata.slice(
                    Math.ceil(frindslinksdata.length / 4),
                    Math.ceil(frindslinksdata.length / 2),
                  )"
                  :key="friendslink.id"
                  @click="openFriend(friendslink)"
                >
                  <img
                    v-if="friendslink.logo"
                    :src="friendslink.logo"
                    :alt="friendslink.name"
                    class="friendslink-avatar"
                    @error="(e) => (e.target.src = 'src/assets/ (11).png')"
                    loading="lazy"
                  />
                  <br />
                  <span class="friendslink-name">{{ friendslink.name }}</span>
                  <br />
                  <span v-if="friendslink.description" class="friendslink-description">
                    {{ friendslink.description }}
                  </span>
                </el-card>
              </div>
            </el-col>
            <el-col :span="6"
              ><div class="grid-content ep-bg-purple">
                <el-card
                  class="friendscard"
                  shadow="hover"
                  v-for="friendslink in frindslinksdata.slice(
                    Math.ceil(frindslinksdata.length / 2),
                    Math.ceil((frindslinksdata.length * 3) / 4),
                  )"
                  :key="friendslink.id"
                  @click="openFriend(friendslink)"
                >
                  <img
                    v-if="friendslink.logo"
                    :src="friendslink.logo"
                    :alt="friendslink.name"
                    class="friendslink-avatar"
                    @error="(e) => (e.target.src = 'src/assets/ (11).png')"
                    loading="lazy"
                  />
                  <br />
                  <span class="friendslink-name">{{ friendslink.name }}</span>
                  <br />
                  <span v-if="friendslink.description" class="friendslink-description">
                    {{ friendslink.description }}
                  </span>
                </el-card>
              </div></el-col
            >

            <el-col :span="6"
              ><div class="grid-content ep-bg-purple">
                <el-card
                  class="friendscard"
                  shadow="hover"
                  v-for="friendslink in frindslinksdata.slice(
                    Math.ceil((frindslinksdata.length * 3) / 4),
                  )"
                  :key="friendslink.id"
                  @click="openFriend(friendslink)"
                >
                  <img
                    v-if="friendslink.logo"
                    :src="friendslink.logo"
                    :alt="friendslink.name"
                    class="friendslink-avatar"
                    @error="(e) => (e.target.src = 'src/assets/ (11).png')"
                    loading="lazy"
                  />
                  <br />
                  <span class="friendslink-name">{{ friendslink.name }}</span>
                  <br />
                  <span v-if="friendslink.description" class="friendslink-description">
                    {{ friendslink.description }}
                  </span>
                </el-card>
              </div></el-col
            >
          </el-row>
        </ul>
      </el-text>
    </div>

    <comments-card :page="'friends'" />
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import { fetchFriends } from '@/api/friend.js'

import CommentsCard from '@/components/CommentsCard.vue'
const frindslinksdata = ref([])
const loading = ref(false)
const error = ref(null)

// 点击友链卡片，跳转到对应站点
const openFriend = (friendslink) => {
  if (!friendslink || !friendslink.url) return

  let targetUrl = friendslink.url.trim()
  // 如果后端只给了域名或不带协议，默认补上 https://
  if (!/^https?:\/\//i.test(targetUrl)) {
    targetUrl = `https://${targetUrl}`
  }

  window.open(targetUrl, '_blank')
}

//获取友情链接列表信息
const getFrindslinks = async () => {
  loading.value = true
  error.value = null

  try {
    const data = await fetchFriends()
    frindslinksdata.value = data || []
  } catch (err) {
    error.value = '获取友链数据失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getFrindslinks()
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
