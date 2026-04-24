<template>
  <div class="page-container">
    <el-card shadow="never" class="page-card">
      <SearchToolbar>
        <template #actions>
          <el-button type="primary" :icon="Plus" @click="router.push('/writemoment')">
            发布动态
          </el-button>
        </template>
      </SearchToolbar>

      <CommonTable
        :data="data"
        :loading="loading"
        :page="pagination.page"
        :size="pagination.size"
        :total="pagination.total"
        @page-change="handlePageChange"
        @size-change="handleSizeChange"
      >
        <el-table-column type="index" label="ID" width="70" />
        <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <template v-if="parseImages(row.image).length > 0">
              <el-image
                style="width: 60px; height: 60px; border-radius: 6px"
                :src="parseImages(row.image)[0]"
                :preview-src-list="parseImages(row.image)"
                fit="cover"
                preview-teleported
              />
              <el-tag
                v-if="parseImages(row.image).length > 1"
                size="small"
                type="info"
                style="margin-left: 6px"
              >
                +{{ parseImages(row.image).length - 1 }}
              </el-tag>
            </template>
            <span v-else class="text-placeholder">--</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <StatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <div class="table-actions">
              <DeleteButton
                :id="row.id"
                :api-func="deleteMomentById"
                :item-type="'动态'"
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
import { useRouter } from "vue-router";
import { Plus } from "@element-plus/icons-vue";
import { deleteMomentById, getMomentList } from "@/api/moment";
import { useTable } from "@/composables/useTable";
import { formatTime, parseImages } from "@/composables/useFormat";
import SearchToolbar from "@/components/common/SearchToolbar.vue";
import CommonTable from "@/components/common/CommonTable.vue";
import StatusTag from "@/components/common/StatusTag.vue";
import DeleteButton from "@/components/common/DeleteButton.vue";

const router = useRouter();

const {
  data,
  loading,
  pagination,
  handleDeleteSuccess,
  handlePageChange,
  handleSizeChange,
} = useTable(getMomentList);
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}
.text-placeholder {
  color: #c0c4cc;
}
</style>
