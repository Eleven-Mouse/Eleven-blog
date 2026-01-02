<template>
  <el-card class="article-container">
    <div class="article-list">
      <div class="article-card-container">
        <el-table
          :data="articles"
          row-class-name="article-row"
          :cell-style="{ textAlign: 'center' }"
          :header-cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="title" label="标题" width="150">
            <template #default="{ row }"> {{ row.title }} </template>
          </el-table-column>

          <el-table-column prop="categoryName" label="分类" width="150">
            <template #default="{ row }">
              <el-tag type="info"> {{ row.categoryName }} </el-tag></template
            ></el-table-column
          >

          <el-table-column width="200" label="标签">
            <template #default="{ row }">
              <el-tag
                size="small"
                v-for="(tag, index) in formatTags(row.tags)"
                :key="index"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览量" width="90" />
          <el-table-column prop="status" width="110" label="公开性">
            <template #default="{ row }">
              <el-switch
                v-model="row.status"
                :active-value="1"
                :inactive-value="0"
                :loading="row.statusLoading"
                @change="handleStatusChange(row)"
                style="
                  --el-switch-on-color: #13ce66;
                  --el-switch-off-color: #ffb6c1;
                "
              />
            </template>
          </el-table-column>
          <el-table-column width="150" label="创建时间">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column width="160" label="发布时间">
            <template #default="{ row }">
              {{ formatTime(row.publishTime) }}
            </template>
          </el-table-column>
          <el-table-column width="150" label="操作">
            <template #default="{ row }">
              <span>
                <EditButton :id="row.id" target-path="/article/edit" />
                <DeleteButton
                  :id="row.id"
                  :api-func="deleteArticleById"
                  @success="handleDeleteSuccess"
                />
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div
        v-if="loading"
        :data="getArticles"
        style="width: 100%"
        class="loading-tip"
      >
        正在加载文章...
      </div>
      <div v-if="error" class="error-tip">{{ error }}</div>

      <div
        class="pagination-container"
        v-if="!loading && !error && articles.length > 0"
      >
        <el-pagination
          layout="prev, pager, next"
          :total="pagination.total"
          :page-size="pagination.size"
          :current-page="pagination.currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </el-card>
</template>
<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import {
  deleteArticleById,
  getAllArticles,
  updateArticle,
} from "@/api/article";
import DeleteButton from "@/components/common/DeleteButton.vue";
import EditButton from "@/components/common/EditButton.vue";
import { ElMessage } from "element-plus";
const route = useRoute();
// 文章列表
const articles = ref([]);
// 分页信息
const pagination = ref({
  currentPage: 1,
  total: 0,
  size: 10,
});

// 加载和错误状态
const loading = ref(true);
const error = ref(null);

// 获取文章列表
const getArticles = async (page = 1) => {
  loading.value = true;
  error.value = null;
  try {
    const response = await getAllArticles();
    articles.value = response || [];
    pagination.value.total = response.length;
    pagination.value.currentPage = page;
  } catch (err) {
    error.value = "获取文章列表失败，请稍后再试。";
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 分页
const handlePageChange = (page) => {
  getArticles(page);
  // 手动滚动到页面顶部
  window.scrollTo({ top: 0, behavior: "smooth" });
};
// 组件挂载时获取第一页数据
onMounted(() => {
  getArticles(1);
});

// 监听路由上的 keyword 变化，实时更新搜索结果
watch(
  () => route.query.keyword,
  () => {
    // 每次搜索关键字变化时，从第一页重新请求
    getArticles(1);
  }
);

const formatTime = (datetime) => {
  if (!datetime) return "--";
  const date = new Date(datetime);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

const formatTags = (tags) => {
  if (!tags) return [];
  // 如果已经是数组，直接返回
  if (Array.isArray(tags)) return tags;
  // 如果是字符串，按逗号拆分，并去掉可能存在的空格
  if (typeof tags === "string") {
    return tags
      .split(",")
      .map((t) => t.trim())
      .filter((t) => t);
  }
  return [];
};

const handleDeleteSuccess = (deleteId) => {
  articles.value = articles.value.filter((item) => item.id !== deleteId);
};

// 处理状态变更
const handleStatusChange = async (row) => {
  // 1. 开启 Loading 状态（需要在获取列表时给每个 row 加这个属性，或者临时加）
  row.statusLoading = true;

  try {
    const id = row.id;
    const data = {
      status: row.status,
    };

    console.log("正在更新状态 ID:", id, "数据:", data);

    await updateArticle(id, data);

    ElMessage.success("文章状态更新成功");
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1;

    ElMessage.error("状态更新失败");
    console.error(error);
  } finally {
    row.statusLoading = false;
  }
};
</script>
<style scoped>
.article-container {
  width: 100%;
}
.el-col {
  text-align: center;
}
.pagination-container {
  display: flex;
  justify-content: center;
}
.titles {
  text-align: center;
}

</style>
