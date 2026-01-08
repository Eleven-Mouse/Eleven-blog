<template>
  <div class="comments-card">
    <h3 class="title">评论</h3>

    <!-- 主评论表单 -->
    <div class="comment-form-wrapper">
      <div class="user-info-inputs">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-input v-model="commentForm.nickname" placeholder="昵称 (必填)" />
          </el-col>
          <el-col :span="12">
            <el-input v-model="commentForm.email" type="email" placeholder="邮箱 (必填，不公开)" />
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-input v-model="commentForm.website" type="url" placeholder="网站 (可选)" />
          </el-col>
          <el-col :span="12">
            <el-input v-model="commentForm.avatar" type="url" placeholder="头像URL (可选)" />
          </el-col>
        </el-row>
        <el-input
          v-model="commentForm.content"
          placeholder="请输入评论..."
          :rows="5"
          type="textarea"
        ></el-input>
      </div>

      <div class="form-footer">
        <el-button @click="submitRootComment"> 发表评论</el-button>
      </div>
    </div>

    <el-divider />
    <h3>共有{{ totalComments }}条评论</h3>
    <div v-if="loading && comments.length === 0" class="loading-tip">评论加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <!-- 评论列表 -->
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
    <div v-else-if="!loading" class="no-comments-tip">暂无评论，快来抢沙发吧！</div>

    <div v-if="noMore && comments.length > 0" class="no-more-tip">已加载所有评论</div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps, reactive, onBeforeMount } from 'vue'
import { fetchComments, createComment } from '@/api/comment.js'
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
const pagination = reactive({ pageNum: 1, pageSize: 1000 }) // Load all comments at once for tree building

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
    const flatList = res || [] // The interceptor returns the data directly
    totalComments.value = flatList.length // 在这里更新总数
    comments.value = buildTree(flatList)
    if (flatList.length < pagination.pageSize) noMore.value = true
  } catch (err) {
    error.value = '评论加载失败，请稍后重试。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const submitComment = async (commentData) => {
  if (!commentForm.nickname.trim()) return alert('昵称不能为空！')
  if (!commentForm.content.trim()) return alert('内容不能为空！')

  try {
    await createComment(commentData)

    localStorage.setItem('comment_nickname', commentForm.nickname)
    localStorage.setItem('comment_email', commentForm.email)
    localStorage.setItem('comment_website', commentForm.website)
    localStorage.setItem('comment_avatar', commentForm.avatar)
    commentForm.page = props.page
    clearForm.blogId = props.blogId

    alert('评论成功！')
    resetAndReload()
    clearForm()
  } catch (err) {
    alert(err.message || '评论失败，请稍后重试。')
    console.error(err)
  }
}
const submitRootComment = () => {
  if (!commentForm.content.trim()) return alert('评论内容不能为空！')
  const data = { ...commentForm, blogId: props.blogId, page: props.page, parentCommentId: null }
  submitComment(data)
}

const handleShowReply = (comment) => {
  replyingToId.value = comment.id
}

const handleSubmitReply = (parentComment) => {
  const data = {
    ...commentForm,
    blogId: props.blogId,
    page: props.page,
    parentCommentId: parentComment.id,
  }
  submitComment(data)
}

const handleCancelReply = () => {
  replyingToId.value = null
}

const clearForm = () => {
  commentForm.content = ''
  commentForm.nickname = ''
  commentForm.email = ''
  commentForm.website = ''
  commentForm.avatar = ''
}

const resetAndReload = () => {
  replyingToId.value = null
  clearForm() // 调用清空函数
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
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}
.title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
}
.comment-form-wrapper {
  margin-bottom: 30px;
}

.input-field {
  flex: 1 1 200px;
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}
.comment-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  resize: vertical;
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
.form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
.submit-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  background-color: #333;
  color: white;
}
.loading-tip,
.error-tip,
.no-comments-tip,
.no-more-tip {
  text-align: center;
  color: #888;
  padding: 20px 0;
}
.error-tip {
  color: #f56c6c;
}
.comments-list {
  margin-top: 20px;
}
.no-more-tip {
  color: #999;
}
</style>
