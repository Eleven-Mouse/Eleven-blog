<template>
  <el-breadcrumb separator="/" class="app-breadcrumb">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item
      v-for="item in breadcrumbs"
      :key="item.path"
      :to="item.path ? { path: item.path } : undefined"
    >
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

const breadcrumbs = computed(() => {
  return route.matched
    .filter((r) => r.meta?.title && r.name !== "layout")
    .map((r) => ({
      path: r.path,
      title: r.meta.title,
    }));
});
</script>

<style scoped>
.app-breadcrumb {
  line-height: 56px;
}
</style>
