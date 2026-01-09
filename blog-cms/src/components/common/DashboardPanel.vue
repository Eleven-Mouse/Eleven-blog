<template>
  <div class="sidebar-header">
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
          <h4>Home</h4>
        </el-menu-item>
        <el-menu-item index="/articlemgmt">
          <el-icon><HomeFilled /></el-icon>
          <h4>文章管理</h4>
        </el-menu-item>
        <el-menu-item index="/writearticle">
          <el-icon><WalletFilled /></el-icon>
          <h4>编辑文章</h4>
        </el-menu-item>
        <el-menu-item index="/momentsmgmt">
          <el-icon><House /></el-icon>
          <h4>动态管理</h4>
        </el-menu-item>
        <el-menu-item index="/writemoment">
          <el-icon><ChatSquare /></el-icon>
          <h4>编辑动态</h4>
        </el-menu-item>
        <el-menu-item index="/categorisemgmt">
          <el-icon><UserFilled /></el-icon>
          <h4>分类管理</h4>
        </el-menu-item>
        <el-menu-item index="/commentmgmt">
          <el-icon><Comment /></el-icon>
          <h4>评论管理</h4>
        </el-menu-item>
        <el-menu-item index="/tagsmgmt">
          <el-icon><Discount /></el-icon>
          <h4>标签管理</h4>
        </el-menu-item>
        <el-menu-item index="/friendlinks">
          <el-icon><Share /></el-icon>
          <h4>友链管理</h4>
        </el-menu-item>
        <el-menu-item index="/upload">
          <el-icon><UploadFilled /></el-icon>
          <h4>上传文章</h4>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import {
  HomeFilled,
  UserFilled,
  WalletFilled,
  Platform,
  ChatSquare,
  House,
  Comment,
  Discount,
  UploadFilled,
  Share
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
.menu-scrollbar {
  border: 1px, #27272a;
  height: 600px;
}
.el-menu-vertical {
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px;
}
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  border-radius: 8px;
}
</style>
