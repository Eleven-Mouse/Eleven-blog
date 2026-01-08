<template>
  <el-container style="height: 97vh; overflow: hidden">
    <el-header style="width: 100%"> <HeaderPanel /></el-header>

    <el-container class="main-container" style="width: 100%; height: 100%">
      <el-aside style="width: 240px">
        <DashboardPanel />
      </el-aside>
      <el-main class="scroll-main" style="overflow-y: auto; height: 95%"
        ><router-view :key="$route.fullPath"
      /></el-main>
    </el-container>
  </el-container>
</template>
<script>
export default {
  name: "MyLayout",
};
</script>
<script setup>
import DashboardPanel from "@/components/common/DashboardPanel.vue";
import HeaderPanel from "@/components/common/HeaderPanel.vue";
</script>
<style scoped>
.scroll-main {
  background-color: #fafafa;

  flex: 1;
  overflow-y: auto;
  padding: 20px;
  scrollbar-width: 0;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    width: 0;
    height: 0;
  }
}
</style>
