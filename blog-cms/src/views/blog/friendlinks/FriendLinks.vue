<template>
  <div class="page-container">
    <el-card shadow="never" class="page-card">
      <SearchToolbar>
        <template #filters>
          <el-input
            v-model="keyword"
            placeholder="搜索友链..."
            :prefix-icon="Search"
            clearable
            style="width: 220px"
          />
        </template>
        <template #actions>
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增友链
          </el-button>
        </template>
      </SearchToolbar>

      <CommonTable
        :data="filteredList"
        :loading="loading"
        :show-pagination="false"
      >
        <el-table-column type="index" label="ID" width="70" />
        <el-table-column label="Logo" width="90">
          <template #default="{ row }">
            <el-image
              v-if="parseImages(row.logo).length > 0"
              style="width: 48px; height: 48px; border-radius: 6px"
              :src="parseImages(row.logo)[0]"
              fit="cover"
            >
              <template #error>
                <div class="image-fallback">
                  <el-icon :size="20"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="image-fallback">
              <el-icon :size="20"><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="140">
          <template #default="{ row }">
            <el-tag effect="plain">{{ row.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="url" label="站点" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <a
              v-if="row.url"
              :href="row.url"
              target="_blank"
              class="link-text"
            >
              {{ row.url }}
            </a>
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
                :api-func="deleteFriendLinkById"
                :item-type="'友链'"
                @success="handleDeleteSuccess"
              />
            </div>
          </template>
        </el-table-column>
      </CommonTable>

      <FriendLinksDialog
        ref="dialogRef"
        @success="refresh"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { Plus, Search, Edit, Picture } from "@element-plus/icons-vue";
import {
  createFriendLink,
  updateFriendLink,
  getFriendLinks,
  deleteFriendLinkById,
} from "@/api/friendLink";
import { useSimpleList } from "@/composables/useTable";
import { formatTime, parseImages } from "@/composables/useFormat";
import SearchToolbar from "@/components/common/SearchToolbar.vue";
import CommonTable from "@/components/common/CommonTable.vue";
import DeleteButton from "@/components/common/DeleteButton.vue";
import FriendLinksDialog from "@/components/business/FriendLinksDialog.vue";

const { data: friendlinks, loading, refresh, handleDeleteSuccess } = useSimpleList(getFriendLinks);
const dialogRef = ref(null);
const keyword = ref("");

const filteredList = computed(() => {
  if (!keyword.value) return friendlinks.value;
  const k = keyword.value.toLowerCase();
  return friendlinks.value.filter(
    (item) =>
      item.name?.toLowerCase().includes(k) ||
      item.description?.toLowerCase().includes(k) ||
      item.url?.toLowerCase().includes(k)
  );
});

const handleAdd = () => {
  dialogRef.value.open(null, {
    itemName: "友链",
    addApi: createFriendLink,
    updateApi: updateFriendLink,
  });
};

const handleEdit = (row) => {
  dialogRef.value.open(row, {
    itemName: "友链",
    addApi: createFriendLink,
    updateApi: updateFriendLink,
  });
};
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}

.image-fallback {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.link-text {
  color: #409eff;
  font-size: 13px;
}

.link-text:hover {
  text-decoration: underline;
}
</style>
