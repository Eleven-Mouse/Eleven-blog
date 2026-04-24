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
          <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
        </div>
        <div class="comment-text">
          <span v-if="comment.parentCommentNickname" class="comment-reply-to">@{{ comment.parentCommentNickname }}</span>
          {{ comment.content }}
        </div>
        <div class="comment-actions">
          <button @click="$emit('show-reply', comment)" class="comment-reply-btn">回复</button>
        </div>
      </div>
    </div>

    <!-- Reply form -->
    <div v-if="replyingTo === comment.id" class="reply-form">
      <div class="reply-form__fields">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-input v-model="commentForm.nickname" placeholder="昵称 (必填)" />
          </el-col>
          <el-col :span="12">
            <el-input v-model="commentForm.avatar" placeholder="头像URL (可选)" />
          </el-col>
        </el-row>
        <el-input
          v-model="commentForm.content"
          :placeholder="`回复 @${comment.nickname}`"
          type="textarea"
        />
      </div>
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
        :replyingTo="replyingTo"
        :commentForm="commentForm"
        :defaultAvatar="defaultAvatar"
        @show-reply="(c) => $emit('show-reply', c)"
        @submit-reply="(p) => $emit('submit-reply', p)"
        @cancel-reply="$emit('cancel-reply')"
      />
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'CommentNode' })

const props = defineProps({
  comment: { type: Object, required: true },
  replyingTo: { type: [Number, String], default: null },
  commentForm: { type: Object, required: true },
  defaultAvatar: { type: String, required: true },
})

const emit = defineEmits(['show-reply', 'submit-reply', 'cancel-reply'])

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
  gap: 10px;
  margin-bottom: 6px;
}

.comment-nickname {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
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

.comment-actions {
  margin-top: 6px;
}

.comment-reply-btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 13px;
  padding: 0;
  transition: color 0.15s;
}

.comment-reply-btn:hover {
  color: var(--accent);
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

.reply-form__fields {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 10px;
}

.reply-form__actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
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
