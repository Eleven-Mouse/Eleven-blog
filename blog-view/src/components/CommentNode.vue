<template>
  <div class="comment-node">
    <!-- 评论主体 -->
    <div class="comment-main">
      <div class="comment-avatar">
        <img :src="comment.avatar || defaultAvatar" alt="avatar" />
      </div>
      <div class="comment-content-wrapper">
        <div class="comment-header">
          <span class="nickname">{{ comment.nickname }}</span>
          <span class="create-date">{{ formatDate(comment.createTime) }}</span>
        </div>
        <div class="comment-body">
          <p>
            <span v-if="comment.parentCommentNickname" class="reply-to">@{{ comment.parentCommentNickname}}</span>
            {{ comment.content }}
          </p>
        </div>
        <div class="comment-footer">
          <button @click="$emit('show-reply', comment)" class="reply-btn">回复</button>
        </div>
      </div>
    </div>

    <!-- 回复表单 -->
    <div v-if="replyingTo === comment.id" class="reply-form-wrapper">
      <div class="user-info-inputs">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-input v-model="commentForm.nickname" type="text" placeholder="昵称 (必填)"
          /></el-col>
          <el-col :span="12">
            <el-input v-model="commentForm.avatar" type="url" placeholder="头像URL (可选)" />
          </el-col>
        </el-row>
        <el-input
          v-model="commentForm.content"
          :placeholder="`回复 @${comment.nickname}`"
          type="textarea"
        ></el-input>
      </div>

      <div class="form-footer">
        <el-button @click="submitReply">提交回复</el-button>
        <el-button @click="$emit('cancel-reply')">取消</el-button>
      </div>
    </div>

    <!-- 子评论 -->
    <div v-if="comment.children && comment.children.length > 0" class="replies-list">
      <CommentNode
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :replyingTo="replyingTo"
        :commentForm="commentForm"
        :defaultAvatar="defaultAvatar"
        @show-reply="(c) => $emit('show-reply', c)"
        @submit-reply="(parent) => $emit('submit-reply', parent)"
        @cancel-reply="$emit('cancel-reply')"
      />
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

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
    alert('回复内容不能为空！')
    return
  }
  emit('submit-reply', props.comment)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}
</script>

<style scoped>
/* 第一层 replies 继续缩进 55px 留立柱 */
.replies-list {
  margin-left: 55px;
  border-left: 2px solid #f0f0f0;
  padding-left: 15px;
}

/* 从第二层开始去掉额外缩进和立柱 */
:deep(.replies-list .replies-list) {
  margin-left: 0; /* 取消进一步缩进 */
  border-left: none; /* 去掉立柱（竖线） */
  padding-left: 0; /* 去掉额外内边距 */
}
.comment-node {
  width: 100%;
}
.comment-main {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}
.comment-node:last-child .comment-main {
  border-bottom: none;
}

.comment-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 20%;
  margin-right: 15px;
}
.comment-content-wrapper {
  flex: 1;
}
.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.nickname {
  font-weight: 600;
  color: #333;
  margin-right: 10px;
}
.create-date {
  font-size: 12px;
  color: #999;
}
.comment-body {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}
.reply-to {
  color: #252627;
  font-weight: 500;
  margin-right: 5px;
}
.comment-footer {
  margin-top: 10px;
}
.reply-btn {
  background: none;
  border: none;
  color: #888;
  cursor: pointer;
  font-size: 13px;
}
.reply-form-wrapper {
  margin-top: 15px;
  margin-left: 55px; /* Indent reply form */
}
.user-info-inputs {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 10px;
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
.form-footer {
  display: flex;
  justify-content: flex-end;
}
.submit-btn,
.cancel-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
.submit-btn {
  background-color: #007bff;
  color: white;
}
.cancel-btn {
  background-color: #f1f1f1;
  color: #333;
  margin-left: 10px;
}
.replies-list {
  margin-left: 55px; /* Indent replies */
  border-left: 2px solid #f0f0f0;
  padding-left: 15px;
}
</style>
