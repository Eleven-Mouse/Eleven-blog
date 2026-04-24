<template>
  <div class="page-container">
    <el-card shadow="never" class="page-card">
      <SearchToolbar>
        <template #filters>
          <el-input
            v-model="keyword"
            placeholder="搜索分类..."
            :prefix-icon="Search"
            clearable
            style="width: 220px"
            @input="handleSearch"
          />
        </template>
        <template #actions>
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增分类
          </el-button>
        </template>
      </SearchToolbar>

      <CommonTable
        :data="filteredList"
        :loading="loading"
        :show-pagination="false"
      >
        <el-table-column type="index" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="articleCount" label="关联文章数" width="140">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.articleCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
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
                :api-func="deleteCategoryById"
                :item-type="'分类'"
                @success="handleDeleteSuccess"
              />
            </div>
          </template>
        </el-table-column>
      </CommonTable>

      <CommonDialog
        ref="dialogRef"
        @success="refresh"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { Plus, Search } from "@element-plus/icons-vue";
import { createCategory, getAllCategories, deleteCategoryById, updateCategory } from "@/api/category";
import { useSimpleList } from "@/composables/useTable";
import { formatTime } from "@/composables/useFormat";
import SearchToolbar from "@/components/common/SearchToolbar.vue";
import CommonTable from "@/components/common/CommonTable.vue";
import CommonDialog from "@/components/common/CommonDialog.vue";
import EditButton from "@/components/common/EditButton.vue";
import DeleteButton from "@/components/common/DeleteButton.vue";

const { data: categories, loading, refresh, handleDeleteSuccess } = useSimpleList(getAllCategories);
const dialogRef = ref(null);
const keyword = ref("");

const filteredList = computed(() => {
  if (!keyword.value) return categories.value;
  const k = keyword.value.toLowerCase();
  return categories.value.filter((item) => item.name?.toLowerCase().includes(k));
});

const handleSearch = () => {
  // filteredList is computed, updates reactively
};

const handleAdd = () => {
  dialogRef.value.open(null, {
    itemName: "分类",
    addApi: createCategory,
    updateApi: updateCategory,
  });
};
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}
</style>
