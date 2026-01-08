<template>
  <div>
    <el-card shadow="never" style="border-radius: 10px">
      <el-button type="success" plain @click="handleAdd">新增分类</el-button>
      <el-table
        :data="categories"
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        table-layout="fixed"
      >
        <el-table-column type="index" label="id" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="articleCount" label="关联文章数量" />
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
                :api-func="deleteCategoryById"
                @success="handleDeleteSuccess"
              />
            </span>
          </template>
        </el-table-column>
      </el-table>

      <CommonDialog
        ref="dialogRef"
        :loading="submitLoading"
        @success="getAllCategoriesList"
      />
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { createCategory, getAllCategories } from "@/api/category";
import DeleteButton from "@/components/common/DeleteButton.vue";
import { deleteCategoryById } from "@/api/category";
import { updateCategory } from "@/api/category";
import { ElMessage } from "element-plus";
import { Edit } from "@element-plus/icons-vue";
import CommonDialog from "@/components/common/CommonDialog.vue";

const categories = ref([]);
const dialogRef = ref(null);
const submitLoading = ref(false);

const getAllCategoriesList = async () => {
  try {
    categories.value = await getAllCategories();
  } catch (error) {
    console.error("加载分类失败:", error);
    ElMessage.error("加载分类失败");
  }
};

onMounted(async () => {
  getAllCategoriesList();
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
  categories.value = categories.value.filter((item) => item.id !== deleteId);
};

const handleAdd = () => {
  dialogRef.value.open(null, {
    itemName: "分类",
    addApi: createCategory,
    updateApi: updateCategory,
  });
};

const handleEdit = (row) => {
  dialogRef.value.open(row, {
    itemName: "分类",
    addApi: createCategory,
    updateApi: updateCategory,
  });
};
</script>
<style scoped></style>
