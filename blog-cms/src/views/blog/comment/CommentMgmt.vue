<template>
  <div class="page-container">
    <el-card shadow="never" class="page-card">
      <SearchToolbar>
        <template #filters>
          <el-input
            v-model="keyword"
            placeholder="搜索评论内容..."
            :prefix-icon="Search"
            clearable
            style="width: 220px"
          />
        </template>
      </SearchToolbar>

      <CommonTable
        :data="filteredList"
        :loading="loading"
        :show-pagination="false"
      >
        <el-table-column type="index" label="ID" width="70" />
        <el-table-column label="用户" width="200">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 8px">
              <el-avatar :size="32" :src="row.avatar || defaultAvatar" />
              <span>{{ row.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="回复" width="120">
          <template #default="{ row }">
            <span v-if="row.parentNickname" style="color: #909399">
              回复 {{ row.parentNickname }}
            </span>
            <span v-else style="color: #c0c4cc">--</span>
          </template>
        </el-table-column>
        <el-table-column label="所在页面" width="160">
          <template #default="{ row }">
            <el-tag v-if="row.blogId" type="success" effect="plain" size="small">
              {{ row.title }}
            </el-tag>
            <el-tag v-else effect="plain" size="small">
              {{ row.page }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <div class="table-actions">
              <DeleteButton
                :id="row.id"
                :api-func="deleteCommentById"
                :item-type="'评论'"
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
import { ref, computed } from "vue";
import { Search } from "@element-plus/icons-vue";
import avatarImg from "@/assets/avatar.png";
import { deleteCommentById, getComments } from "@/api/comment";
import { useSimpleList } from "@/composables/useTable";
import { formatTime } from "@/composables/useFormat";
import SearchToolbar from "@/components/common/SearchToolbar.vue";
import CommonTable from "@/components/common/CommonTable.vue";
import DeleteButton from "@/components/common/DeleteButton.vue";

const defaultAvatar = avatarImg;
const keyword = ref("");

const { data: comments, loading, refresh, handleDeleteSuccess } = useSimpleList(getComments);

const filteredList = computed(() => {
  if (!keyword.value) return comments.value;
  const k = keyword.value.toLowerCase();
  return comments.value.filter(
    (item) =>
      item.content?.toLowerCase().includes(k) ||
      item.nickname?.toLowerCase().includes(k)
  );
});
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}
</style>
