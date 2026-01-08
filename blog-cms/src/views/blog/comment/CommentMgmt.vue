<template>
  <el-card shadow="never" style="border-radius: 10px">
    <el-table
      :data="comments"
      :cell-style="{ textAlign: 'center' }"
      :header-cell-style="{ textAlign: 'center' }"
      table-layout="fixed"
    >
      <el-table-column type="index" label="id" />
      <el-table-column prop="avatar" label="用户头像">
        <template #default="{ row }">
          <img :src="row.avatar || defaultAvatar" alt="avatar" />
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="用户名称" />
      <el-table-column prop="parentCommentId" label="父评论">
        <template #default="{ row }">
          {{
            row.parentNickname === null ? "--" : row.parentNickname
          }}</template
        >
      </el-table-column>
      <el-table-column label="所在页面">
        <template #default="{ row }">
          <div v-if="row.blogId">
            <el-tag type="success">{{ row.title }}</el-tag>
          </div>
          <div v-else>
            <el-tag type="success">{{ row.page }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评论内容" />
      <el-table-column prop="createTime" label="创建时间">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template #default="{ row }">
          <DeleteButton
            :id="row.id"
            :api-func="deleteCommentById"
            @success="handleDeleteSuccess"
          />
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { getComments } from "@/api/comment";
import { onMounted, ref } from "vue";
import { deleteCommentById } from "@/api/comment";
import DeleteButton from "@/components/common/DeleteButton.vue";
const defaultAvatar = "src/assetsavatar.png";
const comments = ref([]);

// 加载和错误状态
const loading = ref(true);
const error = ref(null);

const getCommentsList = async () => {
  loading.value = true;
  error.value = null;
  try {
    const data = await getComments();
    comments.value = data || [];
  } catch (err) {
    error.value = "获取评论列表失败，请稍后再试。";
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const handleDeleteSuccess = (deleteId) => {
  comments.value = comments.value.filter((item) => item.id !== deleteId);
  getCommentsList();
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
onMounted(() => {
  getCommentsList();
});
</script>
<style scoped></style>
