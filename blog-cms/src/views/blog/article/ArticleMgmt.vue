<template>
  <el-card class="article-container" shadow="never" style="border-radius: 10px">
    <SelectArticlesByCategory
      v-model="pagination.categoryId"
      placeholder="请选择分类"
      @change="handleFilterChange"
    />
    <div class="article-list">
      <div class="article-card-container">
        <el-table
          :data="articles"
          row-class-name="article-row"
          :cell-style="{ textAlign: 'center' }"
          :header-cell-style="{ textAlign: 'center' }"
          table-layout="fixed"
        >
          <el-table-column prop="title" label="标题">
            <template #default="{ row }"> {{ row.title }} </template>
          </el-table-column>

          <el-table-column prop="categoryName" label="分类">
            <template #default="{ row }">
              <el-tag type="info"> {{ row.categoryName }} </el-tag></template
            ></el-table-column
          >

          <el-table-column label="标签">
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
          <el-table-column prop="viewCount" label="浏览量" />
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
          <el-table-column label="创建时间">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="发布时间">
            <template #default="{ row }">
              {{ formatTime(row.publishTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作">
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
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, prev, pager, next, jumper"
          @size-change="getArticles"
          @current-change="getArticles"
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
  getArticlesList,
  updateArticle,
} from "@/api/article";
import DeleteButton from "@/components/common/DeleteButton.vue";
import EditButton from "@/components/common/EditButton.vue";
import { ElMessage } from "element-plus";
import SelectArticlesByCategory from "@/components/SelectArticlesByCategory.vue";
const route = useRoute();
// 文章列表
const articles = ref([]);
// 分页信息
const pagination = ref({
  page: 1,
  total: 0,
  size: 10,
  categoryId: null,
});

// 加载和错误状态
const loading = ref(true);
const error = ref(null);

// 获取文章列表
const getArticles = async () => {
  loading.value = true;
  error.value = null;
  try {
    const params = {
      page: pagination.value.page,
      size: pagination.value.size,
      categoryId: pagination.value.categoryId,
    };
    const data = await getArticlesList(params);
    articles.value = data.data || [];
    pagination.value.total = data.pagination.total;
  } catch (err) {
    error.value = "获取文章列表失败，请稍后再试。";
    console.error(err);
  } finally {
    loading.value = false;
  }
};
const handleFilterChange = () => {
  pagination.value.page = 1;
  getArticles();
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
    pagination.value.page = 1;
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
  if (articles.value.length === 0 && pagination.value.page > 1) {
    pagination.value.page--;
    getArticles();
  }
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
