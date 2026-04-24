<template>
  <div class="comment-node">
    <!-- Comment body -->
    <div class="comment-main">
      <div class="comment-avatar">
        <img :src="comment.avatar || defaultAvatar" alt="avatar" />
      </div>
      <div class="comment-body">
        <div class="comment-header">
          <span class="comment-nickname">{{ comment.nickname }}</span>
          <!-- 博主标签 -->
          <span v-if="isOwner" class="comment-owner-tag">博主</span>
          <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
        </div>
        <div class="comment-text">
          <span v-if="comment.parentCommentNickname" class="comment-reply-to">@{{ comment.parentCommentNickname }}</span>
          <!-- Markdown 渲染内容 -->
          <div class="comment-md-content" v-html="renderedContent"></div>
        </div>
        <!-- IP归属地 -->
        <div v-if="comment.location" class="comment-location">
          来自：{{ comment.location }}
        </div>
        <div class="comment-actions">
          <button v-if="hasProfile" @click="$emit('show-reply', comment)" class="comment-action-btn">
            回复
          </button>
          <button @click="$emit('like-comment', comment.id)" class="comment-action-btn comment-like-btn">
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/>
            </svg>
            {{ comment.likeCount || 0 }}
          </button>
        </div>
      </div>
    </div>

    <!-- Reply form -->
    <div v-if="replyingTo === comment.id" class="reply-form">
      <div class="reply-form__header">
        <img :src="visitorAvatar" alt="" class="reply-form__avatar" />
        <span class="reply-form__name">{{ visitorNickname }}</span>
      </div>
      <el-input
        v-model="commentForm.content"
        :placeholder="`回复 @${comment.nickname}`"
        type="textarea"
        :rows="3"
      />
      <div class="reply-form__actions">
        <el-button size="small" @click="submitReply">提交</el-button>
        <el-button size="small" @click="$emit('cancel-reply')">取消</el-button>
      </div>
    </div>

    <!-- Children -->
    <div v-if="comment.children?.length" class="comment-replies">
      <CommentNode
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :floor="null"
        :replyingTo="replyingTo"
        :isOwner="child.isOwner"
        :hasProfile="hasProfile"
        :visitorNickname="visitorNickname"
        :visitorAvatar="visitorAvatar"
        :commentForm="commentForm"
        @show-reply="(c) => $emit('show-reply', c)"
        @submit-reply="(p) => $emit('submit-reply', p)"
        @cancel-reply="$emit('cancel-reply')"
        @like-comment="(id) => $emit('like-comment', id)"
      />
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import defaultAvatarUrl from '../assets/(5).png'

defineOptions({ name: 'CommentNode' })

const props = defineProps({
  comment: { type: Object, required: true },
  floor: { type: [Number, String], default: null },
  replyingTo: { type: [Number, String], default: null },
  isOwner: { type: Boolean, default: false },
  hasProfile: { type: Boolean, default: false },
  visitorNickname: { type: String, default: '' },
  visitorAvatar: { type: String, default: '' },
  commentForm: { type: Object, required: true },
  defaultAvatar: { type: String, required: false, default: '' },
})

const emit = defineEmits(['show-reply', 'submit-reply', 'cancel-reply', 'like-comment'])

/** Markdown 渲染 — 安全配置 */
const renderer = new marked.Renderer()
marked.setOptions({
  renderer,
  breaks: true,
  gfm: true,
  sanitize: false,
})

const renderedContent = computed(() => {
  if (!props.comment.content) return ''
  // 简单 XSS 过滤: 移除 script 标签
  const sanitized = props.comment.content
    .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
  try {
    return marked.parse(sanitized)
  } catch {
    return sanitized
  }
})

const submitReply = () => {
  if (!props.commentForm.content.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }
  emit('submit-reply', props.comment)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}
</script>

<style scoped>
.comment-main {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border-light);
}

.comment-node:last-child > .comment-main {
  border-bottom: none;
}

.comment-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.comment-nickname {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.comment-owner-tag {
  font-size: 9px;
  padding: 0 4px;
  border-radius: 8px;
  border: 1px solid var(--accent);
  background: transparent;
  color: var(--accent);
  font-weight: 500;
  line-height: 15px;
}

.comment-floor {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.comment-date {
  font-size: 12px;
  color: var(--text-muted);
}

.comment-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.comment-reply-to {
  color: var(--accent);
  font-weight: 500;
  margin-right: 4px;
}

/* Markdown 渲染内容样式 */
.comment-md-content :deep(p) {
  margin: 0 0 4px;
}

.comment-md-content :deep(p:last-child) {
  margin-bottom: 0;
}

.comment-md-content :deep(code) {
  padding: 1px 5px;
  font-size: 13px;
  background: var(--bg-inline-code);
  color: var(--accent);
  border-radius: 4px;
  font-family: 'SF Mono', 'Fira Code', Consolas, monospace;
}

.comment-md-content :deep(pre) {
  margin: 8px 0;
  padding: 10px 14px;
  background: var(--bg-code);
  border-radius: 6px;
  overflow-x: auto;
}

.comment-md-content :deep(pre code) {
  padding: 0;
  background: none;
  color: inherit;
}

.comment-md-content :deep(strong) {
  font-weight: 600;
  color: var(--text-primary);
}

.comment-md-content :deep(blockquote) {
  margin: 6px 0;
  padding: 4px 12px;
  border-left: 3px solid var(--accent);
  color: var(--text-muted);
}

.comment-location {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 6px;
}

.comment-action-btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 13px;
  padding: 0;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.15s;
}

.comment-action-btn:hover {
  color: var(--accent);
}

.comment-like-btn {
  gap: 3px;
}

/* Replies */
.comment-replies {
  margin-left: 48px;
  padding-left: 14px;
  border-left: 2px solid var(--border-light);
}

.comment-replies .comment-replies {
  margin-left: 0;
  border-left: none;
  padding-left: 0;
}

/* Reply form */
.reply-form {
  margin-top: 12px;
  margin-left: 48px;
}

.reply-form__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.reply-form__avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.reply-form__name {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.reply-form__actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .comment-replies {
    margin-left: 24px;
    padding-left: 10px;
  }

  .reply-form {
    margin-left: 24px;
  }

  .comment-avatar img {
    width: 30px;
    height: 30px;
  }
}
</style>
