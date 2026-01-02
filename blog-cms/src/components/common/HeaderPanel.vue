<template>
  <div class="header">
    <div class="search-bar">
      <el-autocomplete
        v-model="searchInput"
        class="search-input"
        placeholder="Please Input"
        :prefix-icon="Search"
        :fetch-suggestions="querySearchArticles"
        :trigger-on-focus="false"
        value-key="title"
        clearable
        @select="handleSelectArticle"
        :popper-append-to-body="false"
      />
    </div>
  </div>
  <el-divider class="thick-divider"></el-divider>
</template>
<script setup>
import { Search } from "@element-plus/icons-vue";
</script>
<style scoped>
.header {
  height: 30px;
  display: flex;
}

/* 针对输入框的焦点状态 */
.search-bar ::v-deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #333 inset !important; /* !important确保覆盖默认样式 */
}
.search-bar {
  width: 220px;
  margin: 0 auto;
  padding-top: 5px;
}
</style>
