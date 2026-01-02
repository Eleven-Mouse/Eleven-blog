<template>
  <el-card >
    <div class="sidebar-header">
      <div class="logo-box">
        <el-avatar shape="square" :size="40" class="logo-avatar">
          Eleven-blog</el-avatar
        >
        <div
          v-show="!isCollapse"
          class="logo-text"
          :class="{ hidden: isCollapse }"
        >
          <span class="company-name">Eleven-blog</span>
        </div>
      </div>

      <!-- 菜单区域 -->
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          active-text-color="#333"
          class="el-menu-vertical"
          default-active="/home"
          text-color="#444"
          router
        >
          <el-menu-item index="/home">
            <el-icon><Platform /></el-icon>
            Home
          </el-menu-item>
          <el-menu-item index="/articlemgmt">
            <el-icon><HomeFilled /></el-icon>
            文章管理
          </el-menu-item>
          <el-menu-item index="/writearticle">
            <el-icon><WalletFilled /></el-icon>
            编辑文章
          </el-menu-item>
          <el-menu-item index="/categorisemgmt">
            <el-icon><UserFilled /></el-icon>
            分类管理
          </el-menu-item>
          <el-menu-item index="/commentmgmt">
            <el-icon><Link /></el-icon>
            评论管理
          </el-menu-item>
          <el-menu-item index="/tagsmgmt">
            <el-icon><Link /></el-icon>
            标签管理
          </el-menu-item>
          <el-menu-item index="/upload">
            <el-icon><Link /></el-icon>
            上传文章
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
      <!-- 底部用户信息 -->
      <div class="sidebar-footer">
        <div class="user-box">
          <el-avatar
            :size="40"
            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
          />
          <div v-show="!isCollapse" class="user-info">
            <span class="username">shadcn</span>
            <span class="user-email">m@example.com</span>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import {
  Link,
  HomeFilled,
  UserFilled,
  WalletFilled,
  Platform,
} from "@element-plus/icons-vue";
import { defineProps } from "vue";
defineProps({
  isCollapse: {
    type: Boolean,
    default: false,
  },
});
</script>

<style scoped>
.main {
  height: 100vh;
  width: 100%;
}
.sidebar-footer {
  border-top: 1px solid #27272a;
  color: #9c9c9c;
  height: 50px;
}

.logo-box {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 100px;
  width: 100%;
}
.user-box {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 50px;
  width: 100%;
}

.logo-text,
.user-info {
  display: flex;
  flex-direction: column;

  width: auto;
}

.user-email {
  font-size: 12px;
}

.menu-scrollbar {
  flex: 1;
  height: 70vh;
  border: 1px, #27272a;
}
.el-menu-vertical {
  border-right: none;
}

:deep(.el-menu-item.is-active) {
  background-color: #e3e3e3;
  color: #333;
  border-radius: 8px;
}
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  display: flex;
  align-items: center;
  height: 50px;
}
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #dbdbdb;
  border-radius: 8px;
}
</style>
