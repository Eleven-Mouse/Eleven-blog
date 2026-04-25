<template>
  <div class="app-sidebar" :class="{ 'is-collapsed': isCollapse }">
    <div class="sidebar-logo">
      <h1 v-show="!isCollapse" class="logo-text">Eleven Admin</h1>
      <h1 v-show="isCollapse" class="logo-text">E</h1>
    </div>

    <el-scrollbar class="sidebar-scrollbar">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        active-text-color="#409eff"
        text-color="#606266"
        router
      >
        <!-- 概览 -->
        <el-menu-item index="/home">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>

        <!-- 内容管理 -->
        <el-sub-menu index="content">
          <template #title>
            <el-icon><Reading /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/articlemgmt">
            <el-icon><Document /></el-icon>
            <template #title>文章管理</template>
          </el-menu-item>
          <el-menu-item index="/writearticle">
            <el-icon><EditPen /></el-icon>
            <template #title>发布文章</template>
          </el-menu-item>
          <el-menu-item index="/momentsmgmt">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>动态管理</template>
          </el-menu-item>
          <el-menu-item index="/writemoment">
            <el-icon><Promotion /></el-icon>
            <template #title>发布动态</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 系统管理 -->
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/categorisemgmt">
            <el-icon><FolderOpened /></el-icon>
            <template #title>分类管理</template>
          </el-menu-item>
          <el-menu-item index="/tagsmgmt">
            <el-icon><PriceTag /></el-icon>
            <template #title>标签管理</template>
          </el-menu-item>
          <el-menu-item index="/commentmgmt">
            <el-icon><Comment /></el-icon>
            <template #title>评论管理</template>
          </el-menu-item>
          <el-menu-item index="/friendlinks">
            <el-icon><Link /></el-icon>
            <template #title>友链管理</template>
          </el-menu-item>
          <el-menu-item index="/blogconfig">
            <el-icon><Setting /></el-icon>
            <template #title>博客配置</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 工具 -->
        <el-sub-menu index="tools">
          <template #title>
            <el-icon><Opportunity /></el-icon>
            <span>工具</span>
          </template>
          <el-menu-item index="/upload">
            <el-icon><UploadFilled /></el-icon>
            <template #title>导入文章</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import {
  Odometer,
  Reading,
  Document,
  EditPen,
  ChatDotRound,
  Promotion,
  Setting,
  FolderOpened,
  PriceTag,
  Comment,
  Link,
  Opportunity,
  UploadFilled,
} from "@element-plus/icons-vue";

defineProps({
  isCollapse: {
    type: Boolean,
    default: false,
  },
});

const route = useRoute();

const activeMenu = computed(() => {
  // For article edit route, highlight articlemgmt
  if (route.path.startsWith("/article/edit")) return "/articlemgmt";
  return route.path;
});
</script>

<style scoped>
.app-sidebar {
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  overflow: hidden;
}

.sidebar-logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  white-space: nowrap;
}

.sidebar-scrollbar {
  flex: 1;
  overflow: hidden;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  border-radius: 6px;
  margin: 2px 8px;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #f5f7fa;
}

:deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  font-weight: 600;
}
</style>
