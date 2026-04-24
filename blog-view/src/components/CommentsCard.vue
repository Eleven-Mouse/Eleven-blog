<template>
  <div class="comments-card">
    <h3 class="comments-card__title">评论</h3>

    <!-- Comment Form -->
    <div class="comment-form">
      <div class="comment-form__fields">
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-input v-model="commentForm.nickname" placeholder="昵称 (必填)" />
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-input v-model="commentForm.email" type="email" placeholder="邮箱 (必填，不公开)" />
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-input v-model="commentForm.website" type="url" placeholder="网站 (可选)" />
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-input v-model="commentForm.avatar" type="url" placeholder="头像URL (可选)" />
          </el-col>
        </el-row>
        <el-input
          v-model="commentForm.content"
          placeholder="请输入评论..."
          :rows="4"
          type="textarea"
        />
      </div>
      <div class="comment-form__actions">
        <el-button type="primary" @click="submitRootComment">发表评论</el-button>
      </div>
    </div>

    <el-divider />

    <h3 class="comments-card__subtitle">共有 {{ totalComments }} 条评论</h3>

    <div v-if="loading && comments.length === 0" class="loading-tip">评论加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div class="comments-list" v-if="comments.length > 0">
      <CommentNode
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :replyingTo="replyingToId"
        :defaultAvatar="defaultAvatar"
        @show-reply="handleShowReply"
        :commentForm="commentForm"
        @submit-reply="handleSubmitReply"
        @cancel-reply="handleCancelReply"
      />
    </div>
    <div v-else-if="!loading" class="empty-tip">暂无评论，快来抢沙发吧！</div>

    <div v-if="noMore && comments.length > 0" class="comments-card__nomore">已加载所有评论</div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps, reactive, onBeforeMount } from 'vue'
import { fetchComments, createComment } from '@/api/comment.js'
import { ElMessage } from 'element-plus'
import CommentNode from './CommentNode.vue'
import defaultAvatar from '../assets/(5).png'

const props = defineProps({
  blogId: { type: [Number, String], default: null },
  page: { type: String, default: null },
})

const totalComments = ref(0)
const comments = ref([])
const commentForm = reactive({
  content: '',
  nickname: localStorage.getItem('comment_nickname') || '',
  email: localStorage.getItem('comment_email') || '',
  website: localStorage.getItem('comment_website') || '',
  avatar: localStorage.getItem('comment_avatar') || '',
  page: '',
  blogId: 0,
})

const loading = ref(false)
const error = ref(null)
const noMore = ref(false)
const pagination = reactive({ pageNum: 1, pageSize: 1000 })
const replyingToId = ref(null)

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

const submitComment = async (commentData) => {
  if (!commentForm.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  if (!commentForm.content.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  try {
    await createComment(commentData)
    localStorage.setItem('comment_nickname', commentForm.nickname)
    localStorage.setItem('comment_email', commentForm.email)
    localStorage.setItem('comment_website', commentForm.website)
    localStorage.setItem('comment_avatar', commentForm.avatar)

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
  const data = { ...commentForm, blogId: props.blogId, page: props.page, parentCommentId: null }
  submitComment(data)
}

const handleShowReply = (comment) => { replyingToId.value = comment.id }

const handleSubmitReply = (parentComment) => {
  const data = { ...commentForm, blogId: props.blogId, page: props.page, parentCommentId: parentComment.id }
  submitComment(data)
}

const handleCancelReply = () => { replyingToId.value = null }

const clearForm = () => {
  commentForm.content = ''
  commentForm.nickname = ''
  commentForm.email = ''
  commentForm.website = ''
  commentForm.avatar = ''
}

const resetAndReload = () => {
  replyingToId.value = null
  clearForm()
  comments.value = []
  pagination.pageNum = 1
  noMore.value = false
  getComments()
}

onBeforeMount(clearForm)
onMounted(getComments)
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

.comment-form {
  margin-bottom: 8px;
}

.comment-form__fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-form__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
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

@media (max-width: 768px) {
  .comments-card {
    padding: 16px;
    border-radius: var(--radius-md);
  }

  .comments-card :deep(.el-col) {
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }
}
</style>
