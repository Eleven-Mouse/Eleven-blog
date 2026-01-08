<template>
  <el-card shadow="never" style="border-radius: 10px">
    <el-button type="success" plain @click="handleAdd">新增标签</el-button>
    <el-table
      :data="tags"
      :cell-style="{ textAlign: 'center' }"
      :header-cell-style="{ textAlign: 'center' }"
      table-layout="fixed"
    >
      <el-table-column type="index" label="id" />
      <el-table-column prop="name" label="标签名称">
        <template #default="{ row }">
          <el-tag> {{ row.name }} </el-tag></template
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
              @click="handleEdit(row)"
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

    <CommonDialog
      ref="dialogRef"
      :loading="submitLoading"
      @success="getTagsList"
    />
  </el-card>
</template>
<script setup>
import { createTag, deleteTagById, updateTag } from "@/api/tags";
import { ref, onMounted } from "vue";
import { getAllTags } from "@/api/tags";
import { Edit } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import DeleteButton from "@/components/common/DeleteButton.vue";
import CommonDialog from "@/components/common/CommonDialog.vue";
const tags = ref([]);
const dialogRef = ref(null);
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

const handleDeleteSuccess = (deleteId) => {
  tags.value = tags.value.filter((item) => item.id !== deleteId);
};

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
<style scoped></style>
