<template>
  <el-card>
    <el-table
      :data="tags"
      :cell-style="{ textAlign: 'center' }"
      :header-cell-style="{ textAlign: 'center' }"
      table-layout="fixed"
    >
      <el-table-column type="index" label="id" />
      <el-table-column prop="name" label="标签名称">
        <template #default="{ row }">
          <el-tag size="normal"> {{ row.name }} </el-tag></template
        >
      </el-table-column>
      <el-table-column prop="articleCount" label="关联文章数量">
        <template #default="{ row }">
          <el-tag type="warning">{{ row.articleCount }}</el-tag></template
        >
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
              :api-func="deleteTagById"
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
</template>
<script setup>
import { deleteTagById, updateTag } from "@/api/tags";
import { ref, onMounted } from "vue";
import { getAllTags } from "@/api/tags";
import { Edit } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import EditDialog from "@/components/common/EditDialog.vue";
import DeleteButton from "@/components/common/DeleteButton.vue";
const tags = ref([]);
const editDialogRef = ref(null);
const submitLoading = ref(false);

const getTagsList = async () => {
  try {
    tags.value = await getAllTags();
  } catch (error) {
    console.error("加载标签失败:", error);
    ElMessage.error("加载标签失败");
  }
};
onMounted(async () => {
  getTagsList();
});
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

// 2. 接收子组件传回的数据，调用 API
const handleUpdate = async (formData) => {
  submitLoading.value = true;
  try {
    console.log("正在调用后端接口，更新数据：", formData);

    // 调用真实的后端接口
    await updateTag(formData);
    ElMessage.success("修改成功");

    // 修改成功后，通知子组件关闭
    editDialogRef.value.close();

    getTagsList();
  } catch (error) {
    console.error("更新失败", error);
  } finally {
    submitLoading.value = false;
  }
};

const handleDeleteSuccess = (deleteId) => {
  tags.value = tags.value.filter((item) => item.id !== deleteId);
};
</script>
<style scoped></style>
