<template>
  <div class="comments-card">
    <h3 class="comments-card__title">评论</h3>

    <!-- 未登录: 显示 GitHub 登录按钮 -->
    <div v-if="!hasProfile" class="comment-form">
      <div class="comment-form__login">
        <a :href="githubLoginUrl" class="comment-form__github-btn" @click="handleGitHubLogin" title="GitHub 登录">
          <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor">
            <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/>
          </svg>
        </a>
        <span class="comment-form__login-tip">GitHub 登录后发表评论</span>
      </div>
    </div>

    <!-- 已登录: 显示评论输入框 -->
    <div v-else class="comment-form">
      <div class="comment-form__fields">
        <div class="comment-form__user">
          <img :src="visitorAvatar || defaultAvatar" alt="avatar" class="comment-form__user-avatar" />
          <span class="comment-form__user-name">{{ visitorNickname }}</span>
          <el-button text size="small" @click="handleLogout" class="comment-form__logout-btn">退出</el-button>
        </div>
        <el-input
          v-model="commentForm.content"
          placeholder="支持 Markdown 语法..."
          :rows="4"
          type="textarea"
        />
        <div class="comment-form__actions">
          <el-button type="primary" @click="submitRootComment">发表评论</el-button>
        </div>
      </div>
    </div>

    <el-divider />

    <h3 class="comments-card__subtitle">共有 {{ totalComments }} 条评论</h3>

    <div v-if="loading && comments.length === 0" class="loading-tip">评论加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div class="comments-list" v-if="comments.length > 0">
      <CommentNode
        v-for="(comment, index) in comments"
        :key="comment.id"
        :comment="comment"
        :floor="comment.floor || (index + 1)"
        :replyingTo="replyingToId"
        :isOwner="comment.isOwner"
        :hasProfile="hasProfile"
        :visitorNickname="visitorNickname"
        :visitorAvatar="visitorAvatar"
        @show-reply="handleShowReply"
        :commentForm="commentForm"
        @submit-reply="handleSubmitReply"
        @cancel-reply="handleCancelReply"
        @like-comment="handleLikeComment"
      />
    </div>
    <div v-else-if="!loading" class="empty-tip">暂无评论，快来抢沙发吧！</div>

    <div v-if="noMore && comments.length > 0" class="comments-card__nomore">已加载所有评论</div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps, reactive, onBeforeMount, computed } from 'vue'
import { fetchComments, createComment, likeComment } from '@/api/comment.js'
import { ElMessage } from 'element-plus'
import CommentNode from './CommentNode.vue'
import defaultAvatar from '../assets/(5).png'

const PROFILE_KEY = 'visitor_profile'
const PROFILE_EXPIRE_DAYS = 30

const props = defineProps({
  blogId: { type: [Number, String], default: null },
  page: { type: String, default: null },
})

// ---- 访客资料状态 ----
const visitorNickname = ref('')
const visitorAvatar = ref('')
const visitorGithubId = ref(0)
const visitorIsOwner = ref(false)
const profileExpiresAt = ref(0)
const hasProfile = computed(() => {
  return visitorNickname.value && Date.now() < profileExpiresAt.value
})

const totalComments = ref(0)
const comments = ref([])
const commentForm = reactive({ content: '' })
const loading = ref(false)
const error = ref(null)
const noMore = ref(false)
const pagination = reactive({ pageNum: 1, pageSize: 1000 })
const replyingToId = ref(null)

// ---- 从 localStorage 恢复访客资料 ----
const loadProfile = () => {
  try {
    const raw = localStorage.getItem(PROFILE_KEY)
    if (!raw) return
    const data = JSON.parse(raw)
    visitorNickname.value = data.nickname || ''
    visitorAvatar.value = data.avatarUrl || ''
    visitorGithubId.value = data.githubId || 0
    visitorIsOwner.value = data.isOwner || false
    profileExpiresAt.value = data.expiresAt || 0
    if (!hasProfile.value) {
      clearProfile()
    }
  } catch {
    clearProfile()
  }
}

const clearProfile = () => {
  visitorNickname.value = ''
  visitorAvatar.value = ''
  visitorGithubId.value = 0
  visitorIsOwner.value = false
  profileExpiresAt.value = 0
  localStorage.removeItem(PROFILE_KEY)
}

const handleLogout = () => {
  clearProfile()
  ElMessage.success('已退出登录')
}

// ---- 评论树构建 ----
const buildTree = (commentsList) => {
  const commentMap = {}
  const rootComments = []

  commentsList.forEach((comment) => {
    comment.children = []
    commentMap[comment.id] = comment
  })

  commentsList.forEach((comment) => {
    if (comment.parentCommentId && commentMap[comment.parentCommentId]) {
      const parent = commentMap[comment.parentCommentId]
      comment.parentCommentNickname = parent.nickname
      commentMap[comment.parentCommentId].children.push(comment)
    } else {
      rootComments.push(comment)
    }
  })

  Object.values(commentMap).forEach((comment) => {
    if (comment.children.length > 1) {
      comment.children.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
    }
  })

  rootComments.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
  return rootComments
}

// ---- 评论加载 ----
const getComments = async () => {
  if (loading.value) return
  loading.value = true
  error.value = null
  try {
    const params = { blogId: props.blogId, page: props.page, ...pagination }
    const res = await fetchComments(params)
    const flatList = res || []
    totalComments.value = flatList.length
    comments.value = buildTree(flatList)
    if (flatList.length < pagination.pageSize) noMore.value = true
  } catch (err) {
    error.value = '评论加载失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// ---- 提交评论 ----
const submitComment = async (commentData) => {
  if (!hasProfile.value) {
    ElMessage.warning('请先登录')
    return
  }
  if (!commentForm.content.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  try {
    commentData.nickname = visitorNickname.value
    commentData.avatar = visitorAvatar.value
    commentData.githubId = visitorGithubId.value
    commentData.email = ''
    commentData.website = ''

    await createComment(commentData)
    ElMessage.success('评论成功！')
    resetAndReload()
  } catch (err) {
    ElMessage.error(err.message || '评论失败，请稍后重试。')
    console.error(err)
  }
}

const submitRootComment = () => {
  if (!commentForm.content.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }
  const data = { content: commentForm.content, blogId: props.blogId, page: props.page, parentCommentId: null }
  submitComment(data)
}

const handleShowReply = (comment) => { replyingToId.value = comment.id }

const handleSubmitReply = (parentComment) => {
  const data = { content: commentForm.content, blogId: props.blogId, page: props.page, parentCommentId: parentComment.id }
  submitComment(data)
}

const handleCancelReply = () => { replyingToId.value = null }

const handleLikeComment = async (commentId) => {
  try {
    await likeComment(commentId)
    const addLike = (list) => {
      for (const c of list) {
        if (c.id === commentId) {
          c.likeCount = (c.likeCount || 0) + 1
          return true
        }
        if (c.children?.length && addLike(c.children)) return true
      }
      return false
    }
    addLike(comments.value)
  } catch {
    ElMessage.error('点赞失败')
  }
}

// ---- GitHub OAuth 登录 ----
const GITHUB_CLIENT_ID = 'Ov23liNWajET8KEhPIXL'
const GITHUB_REDIRECT_URI = `${window.location.origin}/oauth/callback`

const githubLoginUrl = computed(() => {
  const params = new URLSearchParams({
    client_id: GITHUB_CLIENT_ID,
    redirect_uri: GITHUB_REDIRECT_URI,
    scope: 'read:user,user:email',
    state: Math.random().toString(36).slice(2),
  })
  return `https://github.com/login/oauth/authorize?${params}`
})

const handleGitHubLogin = () => {
  sessionStorage.setItem('oauth_redirect', window.location.pathname + window.location.search)
}

const resetAndReload = () => {
  replyingToId.value = null
  commentForm.content = ''
  comments.value = []
  pagination.pageNum = 1
  noMore.value = false
  getComments()
}

onBeforeMount(() => { commentForm.content = '' })
onMounted(() => {
  loadProfile()
  getComments()
})
</script>

<style scoped>
.comments-card {
  padding: 24px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.comments-card__title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.comments-card__subtitle {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

/* ---- 未登录区域 ---- */
.comment-form__login {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 0;
}

.comment-form__github-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  background: var(--bg-card, #fff);
  color: var(--text-secondary, #555);
  border: 1.5px solid var(--border-color, #ddd);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}

.comment-form__github-btn:hover {
  color: #24292e;
  border-color: #24292e;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.comment-form__login-tip {
  color: var(--text-muted);
  font-size: 13px;
}

/* ---- 已登录用户栏 ---- */
.comment-form__user {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.comment-form__user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-form__user-name {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.comment-form__logout-btn {
  color: var(--text-muted);
}

/* ---- 评论表单 ---- */
.comment-form__fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-form__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 4px;
}

.comments-list {
  margin-top: 16px;
}

.comments-card__nomore {
  text-align: center;
  color: var(--text-muted);
  font-size: 13px;
  padding: 16px 0;
}

/* ---- 响应式 ---- */
@media (max-width: 768px) {
  .comments-card {
    padding: 16px;
    border-radius: var(--radius-md);
  }
}
</style>
