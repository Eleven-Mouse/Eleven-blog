<template>
  <div class="page-container">
    <el-card shadow="never" class="page-card">
      <SearchToolbar>
        <template #filters>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索文章标题..."
            :prefix-icon="Search"
            clearable
            style="width: 200px"
            @clear="resetFilters"
            @keyup.enter="resetFilters"
          />
          <SelectArticlesByCategory
            v-model="filters.categoryId"
            placeholder="全部分类"
            @change="resetFilters"
          />
          <el-select
            v-model="filters.status"
            placeholder="全部状态"
            clearable
            style="width: 130px"
            @change="resetFilters"
          >
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
          </el-select>
        </template>
        <template #actions>
          <el-button
            v-if="selectedIds.length > 0"
            type="danger"
            plain
            :icon="Delete"
            @click="handleBatchDelete"
          >
            批量删除 ({{ selectedIds.length }})
          </el-button>
          <el-button type="primary" :icon="Plus" @click="router.push('/writearticle')">
            发布文章
          </el-button>
        </template>
      </SearchToolbar>

      <CommonTable
        :data="articles"
        :loading="loading"
        :selectable="true"
        :page="pagination.page"
        :size="pagination.size"
        :total="pagination.total"
        @page-change="handlePageChange"
        @size-change="handleSizeChange"
        @selection-change="handleSelectionChange"
      >
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="200">
          <template #default="{ row }">
            <div class="tag-group">
              <el-tag
                v-for="(tag, index) in formatTags(row.tags)"
                :key="index"
                size="small"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :loading="row.statusLoading"
              active-color="#67c23a"
              inactive-color="#dcdfe6"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="140">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <div class="table-actions">
              <EditButton :id="row.id" target-path="/article/edit" />
              <DeleteButton
                :id="row.id"
                :api-func="deleteArticleById"
                :item-type="'文章'"
                @success="handleDeleteSuccess"
              />
            </div>
          </template>
        </el-table-column>
      </CommonTable>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Plus, Search, Delete } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { deleteArticleById, getArticlesList, updateArticle } from "@/api/article";
import { useTable } from "@/composables/useTable";
import { formatTime, formatTags } from "@/composables/useFormat";
import SearchToolbar from "@/components/common/SearchToolbar.vue";
import CommonTable from "@/components/common/CommonTable.vue";
import EditButton from "@/components/common/EditButton.vue";
import DeleteButton from "@/components/common/DeleteButton.vue";
import SelectArticlesByCategory from "@/components/business/SelectArticlesByCategory.vue";

const route = useRoute();
const router = useRouter();

const filters = reactive({
  keyword: null,
  categoryId: null,
  status: null,
});

const selectedIds = ref([]);

const {
  data: articles,
  loading,
  pagination,
  fetchData,
  handleDeleteSuccess,
  handlePageChange,
  handleSizeChange,
  resetFilters,
} = useTable(getArticlesList, {
  defaultFilters: filters,
});

watch(
  () => route.query.keyword,
  (keyword) => {
    if (keyword) {
      filters.keyword = keyword;
      resetFilters();
    }
  },
  { immediate: true }
);

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map((item) => item.id);
};

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 篇文章吗？此操作不可恢复。`,
      "批量删除",
      { confirmButtonText: "确定删除", cancelButtonText: "取消", type: "warning" }
    );
    await Promise.all(selectedIds.value.map((id) => deleteArticleById(id)));
    ElMessage.success(`成功删除 ${selectedIds.value.length} 篇文章`);
    selectedIds.value = [];
    fetchData();
  } catch {
    // cancelled
  }
};

const handleStatusChange = async (row) => {
  row.statusLoading = true;
  try {
    await updateArticle(row.id, { status: row.status });
    ElMessage.success(row.status === 1 ? "文章已发布" : "文章已转为草稿");
  } catch (err) {
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.error("状态更新失败");
  } finally {
    row.statusLoading = false;
  }
};
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}
</style>
