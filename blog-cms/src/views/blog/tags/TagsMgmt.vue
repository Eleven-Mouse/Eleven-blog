<template>
  <div class="page-container">
    <el-card shadow="never" class="page-card">
      <SearchToolbar>
        <template #filters>
          <el-input
            v-model="keyword"
            placeholder="搜索标签..."
            :prefix-icon="Search"
            clearable
            style="width: 220px"
          />
        </template>
        <template #actions>
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增标签
          </el-button>
        </template>
      </SearchToolbar>

      <CommonTable
        :data="filteredList"
        :loading="loading"
        :show-pagination="false"
      >
        <el-table-column type="index" label="ID" width="80" />
        <el-table-column prop="name" label="标签名称">
          <template #default="{ row }">
            <el-tag effect="plain">{{ row.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="articleCount" label="关联文章数" width="140">
          <template #default="{ row }">
            <el-tag type="warning" effect="plain">{{ row.articleCount }}</el-tag>
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
              <el-button
                type="primary"
                :icon="Edit"
                size="small"
                plain
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <DeleteButton
                :id="row.id"
                :api-func="deleteTagById"
                :item-type="'标签'"
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
import { Plus, Search, Edit } from "@element-plus/icons-vue";
import { createTag, deleteTagById, getAllTags, updateTag } from "@/api/tags";
import { useSimpleList } from "@/composables/useTable";
import { formatTime } from "@/composables/useFormat";
import SearchToolbar from "@/components/common/SearchToolbar.vue";
import CommonTable from "@/components/common/CommonTable.vue";
import CommonDialog from "@/components/common/CommonDialog.vue";
import DeleteButton from "@/components/common/DeleteButton.vue";

const { data: tags, loading, refresh, handleDeleteSuccess } = useSimpleList(getAllTags);
const dialogRef = ref(null);
const keyword = ref("");

const filteredList = computed(() => {
  if (!keyword.value) return tags.value;
  const k = keyword.value.toLowerCase();
  return tags.value.filter((item) => item.name?.toLowerCase().includes(k));
});

const handleAdd = () => {
  dialogRef.value.open(null, {
    itemName: "标签",
    addApi: createTag,
    updateApi: updateTag,
  });
};

const handleEdit = (row) => {
  dialogRef.value.open(row, {
    itemName: "标签",
    addApi: createTag,
    updateApi: updateTag,
  });
};
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}
</style>
