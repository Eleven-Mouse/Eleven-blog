<template>
  <div class="friend-wall">
    <a
      v-for="friend in friends"
      :key="friend.url"
      class="friend-card"
      :href="friend.url"
      target="_blank"
      rel="noreferrer"
    >
      <img
        v-if="activeSources[friend.url]"
        class="friend-card__avatar"
        :src="activeSources[friend.url]"
        :alt="friend.name"
        loading="lazy"
        @error="handleAvatarError(friend)"
      />
      <span
        v-else
        class="friend-card__avatar friend-card__avatar--fallback"
        aria-hidden="true"
      >
        {{ getNameInitial(friend.name) }}
      </span>
      <span class="friend-card__name">{{ friend.name }}</span>
    </a>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'

// 头像自动读取，逐级降级：显式头像 → 站点 favicon.ico → 在线 favicon 服务 → 名称首字占位。
const FALLBACK_FAVICON_API = (host) => `https://api.iowen.cn/favicon/${host}.png`

const props = defineProps({
  friends: {
    type: Array,
    default: () => [],
  },
})

const getHost = (url) => {
  try {
    return new URL(url).host
  } catch {
    return ''
  }
}

const buildAvatarCandidates = (friend) => {
  const candidates = []
  if (friend.avatar) candidates.push(friend.avatar)
  const host = getHost(friend.url)
  if (host) {
    try {
      candidates.push(new URL('/favicon.ico', friend.url).href)
    } catch {
      // 忽略无法解析的链接，走下一级降级
    }
    candidates.push(FALLBACK_FAVICON_API(host))
  }
  return candidates
}

const getNameInitial = (name) => String(name || '').trim().charAt(0).toUpperCase() || '?'

const activeSources = reactive({})
const candidateIndexes = {}

watch(
  () => props.friends,
  (friends) => {
    Object.keys(activeSources).forEach((key) => delete activeSources[key])
    friends.forEach((friend) => {
      candidateIndexes[friend.url] = 0
      activeSources[friend.url] = buildAvatarCandidates(friend)[0] || ''
    })
  },
  { immediate: true },
)

const handleAvatarError = (friend) => {
  const candidates = buildAvatarCandidates(friend)
  const nextIndex = (candidateIndexes[friend.url] || 0) + 1
  candidateIndexes[friend.url] = nextIndex
  activeSources[friend.url] = candidates[nextIndex] || ''
}
</script>

<style scoped>
.friend-wall {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 14px;
  margin: 10px 0 30px;
}

.friend-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 12px 18px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background: var(--bg-secondary);
  color: var(--text-primary);
  text-decoration: none;
  transition:
    border-color var(--transition-fast),
    box-shadow var(--transition-fast),
    transform var(--transition-fast);
}

.friend-card:hover {
  border-color: rgba(var(--accent-rgb), 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.friend-card__avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  background: var(--bg-code);
  transition: transform var(--transition-fast);
}

.friend-card:hover .friend-card__avatar {
  transform: scale(1.06);
}

.friend-card__avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent);
  font-size: 28px;
  font-weight: 700;
}

.friend-card__name {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 15px;
  font-weight: 600;
  text-align: center;
  transition: color var(--transition-fast);
}

.friend-card:hover .friend-card__name {
  color: var(--accent);
}
</style>
