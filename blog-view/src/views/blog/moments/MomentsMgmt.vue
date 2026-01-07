<template>
  <div>
    <el-card shadow="never" style="border-radius: 10px">
      <el-table
        :data="moments"
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        table-layout="fixed"
      >
        <el-table-column type="index" label="id" />
        <el-table-column prop="content" label="内容" />
        <el-table-column label="图片">
          <template #default="{ row }">
            <el-image
              style="width: 80px; height: 80px; border-radius: 4px"
              :src="row.image"
              :preview-src-list="[row.image]"
              fit="cover"
              preview-teleported
            />
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <span>
              <el-button
                type="success"
                plain
                size="small"
                :icon="Edit"
                @click="onEdit(row)"
                >编辑</el-button
              >
              <DeleteButton
                :id="row.id"
                :api-func="deleteMomentById"
                @success="handleDeleteSuccess"
              />
            </span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 引入封装好的弹窗组件 -->
      <EditDialog
        ref="editDialogRef"
        title="编辑分类"
        :loading="submitLoading"
        @confirm="handleUpdate"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Edit } from "@element-plus/icons-vue";
import EditDialog from "@/components/common/EditDialog.vue";
import { ElMessage } from "element-plus";
import { deleteMomentById, getMomentList } from "@/api/moment";
import DeleteButton from "@/components/common/DeleteButton.vue";
const moments = ref([]);
const loading = ref(false);
const error = ref(null);
const keyword = ref([]);
const status = ref(1);
const editDialogRef = ref(null);
const submitLoading = ref(false);
const getMoments = async () => {
  loading.value = true;
  error.value = null;
  try {
    const params = {
      keyword: keyword.value,
      status: status.value,
    };
    const data = await getMomentList(params);
    moments.value = data || [];
  } catch (error) {
    console.error("加载动态失败:", error);
    ElMessage.error("加载动态失败");
  }
};

const formatTime = (datetime) => {
  if (!datetime) return "--";
  const date = new Date(datetime);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};
// 1. 点击编辑按钮，调用子组件的 open 方法
const onEdit = (row) => {
  editDialogRef.value.open(row);
};
const handleDeleteSuccess = (deleteId) => {
  moments.value = moments.value.filter((item) => item.id !== deleteId);
};
onMounted(async () => {
  getMoments();
});
</script>

<style scoped></style>
