<template>
  <div class="oauth-callback">
    <div class="oauth-callback__spinner"></div>
    <p>正在完成 GitHub 登录...</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const PROFILE_KEY = 'visitor_profile'
const PROFILE_EXPIRE_DAYS = 30

onMounted(async () => {
  const code = route.query.code

  if (!code) {
    router.replace('/')
    return
  }

  try {
    const res = await request({
      url: '/oauth/github/callback',
      method: 'get',
      params: { code },
    })

    // 存入 localStorage
    const expiresAt = Date.now() + PROFILE_EXPIRE_DAYS * 24 * 60 * 60 * 1000
    localStorage.setItem(PROFILE_KEY, JSON.stringify({
      nickname: res.nickname,
      avatarUrl: res.avatar || '',
      githubId: res.githubId || 0,
      isOwner: res.isOwner || false,
      expiresAt,
    }))

    // 跳回之前的页面
    const redirect = sessionStorage.getItem('oauth_redirect') || '/'
    sessionStorage.removeItem('oauth_redirect')
    window.location.href = redirect
  } catch (err) {
    console.error('GitHub 登录失败:', err)
    alert('GitHub 登录失败，请重试')
    router.replace('/')
  }
})
</script>

<style scoped>
.oauth-callback {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  gap: 16px;
  font-size: 15px;
  color: #666;
}

.oauth-callback__spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e0e0e0;
  border-top-color: #24292e;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
