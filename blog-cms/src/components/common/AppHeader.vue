<template>
  <div class="app-header">
    <div class="header-left">
      <el-icon
        class="collapse-btn"
        :size="20"
        @click="toggleCollapse"
      >
        <Expand v-if="isCollapse" />
        <Fold v-else />
      </el-icon>
      <AppBreadcrumb />
    </div>

    <div class="header-center">
      <el-autocomplete
        v-model="searchInput"
        class="header-search"
        placeholder="搜索文章..."
        :prefix-icon="Search"
        :fetch-suggestions="querySearchAsync"
        :trigger-on-focus="false"
        :loading="searchLoading"
        value-key="title"
        clearable
        @select="handleSelectArticle"
      />
    </div>

    <div class="header-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="avatar-wrapper">
          <el-avatar :size="32" :src="avatarUrl" />
          <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <a
                href="https://github.com/Eleven-Mouse/Eleven-blog"
                target="_blank"
                class="github-link"
              >
                <el-icon><Link /></el-icon>
                GitHub
              </a>
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import avatarImg from "@/assets/avatar.png";
import {
  Search,
  Expand,
  Fold,
  ArrowDown,
  Link,
  SwitchButton,
} from "@element-plus/icons-vue";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { getArticlesList } from "@/api/article";
import { debounce } from "lodash-es";

defineProps({
  isCollapse: { type: Boolean, default: false },
});

const emit = defineEmits(["toggle-collapse"]);

const router = useRouter();
const authStore = useAuthStore();
const searchInput = ref("");
const searchLoading = ref(false);
const avatarUrl = ref(avatarImg);

const toggleCollapse = () => emit("toggle-collapse");

const querySearchArticles = async (queryString, cb) => {
  const keyword = queryString.trim();
  if (!keyword) {
    cb([]);
    return;
  }
  searchLoading.value = true;
  try {
    const res = await getArticlesList({ page: 1, size: 5, keyword });
    cb(res?.data || []);
  } catch (e) {
    console.error("搜索文章失败", e);
    cb([]);
  } finally {
    searchLoading.value = false;
  }
};

const debouncedSearch = debounce(querySearchArticles, 300);

const querySearchAsync = (queryString, cb) => {
  const keyword = queryString.trim();
  if (!keyword) {
    cb([]);
    return;
  }
  debouncedSearch(keyword, cb);
};

const handleSelectArticle = (item) => {
  if (!item || !item.id) return;
  router.push(`/article/edit/${item.id}`);
  searchInput.value = "";
};

const handleCommand = (command) => {
  if (command === "logout") {
    authStore.logout();
    router.push("/login");
  }
};
</script>

<style scoped>
.app-header {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 24px;
}

.header-search {
  width: 320px;
}

.header-right {
  display: flex;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.avatar-wrapper:hover {
  background-color: #f5f7fa;
}

.dropdown-arrow {
  font-size: 12px;
  color: #909399;
}

.github-link {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: inherit;
}
</style>
