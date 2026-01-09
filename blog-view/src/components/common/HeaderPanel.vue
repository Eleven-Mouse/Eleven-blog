<template>
  <div class="header">
    <el-text class="text"><h1>Eleven-blog</h1></el-text>

    <div class="search-bar">
      <el-autocomplete
        v-model="searchInput"
        class="search-input"
        placeholder="输入文章标题"
        :prefix-icon="Search"
        :fetch-suggestions="querySearchAsync"
        :trigger-on-focus="false"
        :loading="searchLoading"
        value-key="title"
        clearable
        @select="handleSelectArticle"
        :popper-append-to-body="false"
      />
    </div>

    <el-button style="width: 60px; height: 60px" @click="showClick" type="text">
      <el-dropdown
        ref="dropdown1"
        trigger="contextmenu"
        size="large"
        placement="bottom-end"
        @command="handelCommand"
      >
        <el-avatar shape="square" :src="avatarUrl" />
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item><a href="">github</a></el-dropdown-item>
            <el-dropdown-item command="logout">logout</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-button>
  </div>
</template>
<script setup>
import avatarImg from "../../../public/avatar.png";
import { Search } from "@element-plus/icons-vue";
import { ref, nextTick } from "vue";
import { useRouter } from "vue-router";

import { getArticlesList } from "@/api/article";
import { debounce } from "lodash-es";

const router = useRouter();
const searchInput = ref("");
const searchLoading = ref(false);
const avatarUrl = ref(avatarImg);
const dropdown1 = ref([]);

function showClick() {
  if (!dropdown1.value) return;
  dropdown1.value.handleOpen();
}
function handelCommand() {
  localStorage.removeItem("token");
  router.push("/login");
}
// 自动补全：根据输入关键字异步获取文章标题列表
const querySearchArticles = async (queryString, cb) => {
  const keyword = queryString.trim();
  if (!keyword) {
    cb([]);
    return;
  }

  searchLoading.value = true;
  try {
    const res = await getArticlesList({
      page: 1,
      size: 5,
      keyword,
    });
    const list = res?.data || [];
    cb(list);
  } catch (e) {
    console.error("搜索文章失败", e);
    cb([]);
  } finally {
    searchLoading.value = false;
  }
};

const debouncedSearch = debounce((queryString, cb) => {
  querySearchArticles(queryString, cb);
}, 300);

const querySearchAsync = (queryString, cb) => {
  const keyword = queryString.trim();
  if (!keyword) {
    cb([]);
    return;
  }
  searchLoading.value = true;
  // 调用防抖后的函数
  debouncedSearch(keyword, cb);
};
// 选择某个搜索建议后，跳转到文章详情，并清空搜索框
const handleSelectArticle = (item) => {
  if (!item || !item.id) return;
  router.push(`/article/${item.id}`);

  nextTick(() => {
    searchInput.value = "";
  });
};
</script>
<style scoped>
.header {
  height: 20px;
  display: flex;
}
.company-name {
  text-align: center;
}
/* 针对输入框的焦点状态 */
.search-bar ::v-deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #333 inset !important; /* !important确保覆盖默认样式 */
}
.search-bar {
  width: 220px;
  margin: 0 auto;
  padding-top: 10px;
}
.text {
  padding-top: 20px;
}
.el-button:hover {
  background-color: #e0e0e0; /* 悬停时加浅灰色背景 */
}
</style>
