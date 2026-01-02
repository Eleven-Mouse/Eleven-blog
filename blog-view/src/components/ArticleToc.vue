<template>
  <div>
    <el-card class="toc-card">
      <el-text class="mx-1">
        <div class="card-header">
          <span
            ><el-icon><Tickets /></el-icon> 文章目录</span
          >
        </div>
        <el-scrollbar height="400px">
          <li
            v-for="item in headers"
            :key="item.id"
            :class="['toc-item', `toc-level-${item.level}`]"
          >
            <a :href="`#${item.id}`">{{ item.text }}</a>
          </li>
        </el-scrollbar>
      </el-text>
    </el-card>
  </div>
</template>

<script setup>
import { Tickets } from '@element-plus/icons-vue'

// 接收从父组件传递过来的 headers 数组
defineProps({
  headers: {
    type: Array,
    required: true,
  },
})
</script>

<style scoped>
.toc-card {
  position: sticky;
  box-shadow: 0 4px 20px rgba(129, 129, 129, 0.1);
  border-radius: 8px;
  top: 60px; /* Height of the fixed header */
  box-shadow: none;
  border: 0;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
  size: 5px;
}

.toc-item a {
  display: block;
  padding: 8px 0;
  color: var(--el-text-color-primary);
  text-decoration: none;
  border-bottom: 1px solid var(--el-border-color-light);
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.toc-item:last-child a {
  border-bottom: none;
}

.toc-item a:hover {
  background-color: var(--el-fill-color-light);
  color: var(--el-color-primary);
}

.toc-level-2 a {
  padding-left: 20px;
}

.toc-level-3 a {
  padding-left: 40px;
}
</style>
