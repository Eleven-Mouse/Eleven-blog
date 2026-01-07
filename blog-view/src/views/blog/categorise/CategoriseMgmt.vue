<template>
  <div>
    <el-card shadow="never" style="border-radius: 10px">
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
                @click="onEdit(row)"
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
import { getAllCategories } from "@/api/category";
import DeleteButton from "@/components/common/DeleteButton.vue";
import { deleteCategoryById } from "@/api/category";
import EditDialog from "@/components/common/EditDialog.vue";
import { updateCategory } from "@/api/category";
import { ElMessage } from "element-plus";
import { Edit } from "@element-plus/icons-vue";

const categories = ref([]);
const editDialogRef = ref(null);
const submitLoading = ref(false);

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
    await updateCategory(formData);
    ElMessage.success("修改成功");

    // 修改成功后，通知子组件关闭
    editDialogRef.value.close();

    // 刷新页面列表
    getAllArticles();
  } catch (error) {
    console.error("更新失败", error);
  } finally {
    submitLoading.value = false;
  }
};

const getAllArticles = async () => {
  try {
    categories.value = await getAllCategories();
  } catch (error) {
    console.error("加载分类失败:", error);
    ElMessage.error("加载分类失败");
  }
};

onMounted(async () => {
  getAllArticles();
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
</script>
<style scoped></style>
