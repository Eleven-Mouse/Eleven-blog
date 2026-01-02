<template>
  <div class="app-wrapper">
    <el-container>
      <el-aside width="240px">
        <DashboardPanel :is-collapse="isCollapse" />
      </el-aside>

      <el-container>
        <el-header> <HeaderPanel /></el-header>
        <el-main class="scroll-main"><router-view /></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import DashboardPanel from "./components/common/DashboardPanel.vue";
import HeaderPanel from "./components/common/HeaderPanel.vue";
</script>

<style scoped>
.app-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.el-aside {
  border-right: 1px, solid, #333;
  transition: width 0.3s ease-in-out;
  overflow-y: auto;
  overflow-x: hidden;
}
.el-header {
  border-bottom: 1px solid #dcdfe6;
  flex-shrink: 0; /* 关键：防止被 Main 挤压 */

  /* Flex 布局让内部元素对齐 */
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.scroll-main {
  overflow: hidden;
  flex: 1;

  /* 关键：只有这里允许滚动 */
  overflow-y: auto;

  /* 美化：背景色和内边距 */
  background-color: #f4f4f5;
  padding: 20px;
}
</style>
