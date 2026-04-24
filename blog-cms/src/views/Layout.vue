<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-sidebar">
      <AppSidebar :is-collapse="isCollapse" />
    </el-aside>

    <el-container class="layout-main-container">
      <el-header class="layout-header" height="56px">
        <AppHeader
          :is-collapse="isCollapse"
          @toggle-collapse="toggleCollapse"
        />
      </el-header>

      <el-main class="layout-content custom-scrollbar">
        <router-view :key="$route.fullPath" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from "vue";
import AppSidebar from "@/components/common/AppSidebar.vue";
import AppHeader from "@/components/common/AppHeader.vue";

const isCollapse = ref(false);

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.layout-sidebar {
  transition: width 0.25s ease;
  overflow: hidden;
  border-right: 1px solid #ebeef5;
}

.layout-main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-header {
  padding: 0;
  z-index: 10;
}

.layout-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
