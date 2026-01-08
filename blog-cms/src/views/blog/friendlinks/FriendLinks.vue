<template>
  <el-card shadow="never" style="border-radius: 10px">
    <el-button type="success" plain @click="handleAdd">新增友链</el-button>
    <el-table
      :data="friendlinks"
      :cell-style="{ textAlign: 'center' }"
      :header-cell-style="{ textAlign: 'center' }"
      table-layout="fixed"
    >
      <el-table-column type="index" label="id" />
      <el-table-column prop="logo" label="头像">
        <template #default="{ row }">
          <el-image
            style="width: 80px; height: 80px; border-radius: 4px"
            :src="parseImages(row.logo)[0]"
            :preview-src-list="parseImages(row.logo)"
            fit="cover"
            preview-teleported
          >
            <template #error>
              <div class="image-error-slot">
                <el-icon :size="50" style="padding-top: 15px"
                  ><Picture></Picture
                ></el-icon>
              </div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="友链名称">
        <template #default="{ row }">
          <el-tag> {{ row.name }} </el-tag></template
        >
      </el-table-column>
      <el-table-column prop="description" label="描述">
        <template #default="{ row }"> {{ row.description }} </template>
      </el-table-column>
      <el-table-column prop="url" label="站点" />

      <el-table-column prop="createTime" label="创建时间">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间">
        <template #default="{ row }">
          {{ formatTime(row.updateTime) }}
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
              :api-func="deleteFriendLinkById"
              @success="handleDeleteSuccess"
            />
          </span>
        </template>
      </el-table-column>
    </el-table>
    <FriendLinksDialog
      ref="dialogRef"
      :loading="submitLoading"
      @success="getFriendLinksList"
    />
  </el-card>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { Edit, Picture } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import {
  createFriendLink,
  updateFriendLink,
  getFriendLinks,
  deleteFriendLinkById,
} from "@/api/friendLink";
import DeleteButton from "@/components/common/DeleteButton.vue";
import FriendLinksDialog from "@/components/FriendLinksDialog.vue";
const friendlinks = ref([]);
const dialogRef = ref(null);
const loading = ref(false);
const error = ref(null);
const submitLoading = ref(false);
const BASE_URL = import.meta.env.VITE_APP_UPLOAD_URL;
const getFriendLinksList = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await getFriendLinks();
    friendlinks.value = res || [];
  } catch (error) {
    console.error("加载标签失败:", error);
    ElMessage.error("加载标签失败");
  } finally {
    loading.value = false;
  }
};
onMounted(async () => {
  getFriendLinksList();
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
  friendlinks.value = friendlinks.value.filter((item) => item.id !== deleteId);
};

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
const parseImages = (imageStr) => {
  if (!imageStr) return [];

  const paths = imageStr.split(",");

  return paths.map((path) => {
    if (path.startsWith("http") || path.startsWith("https")) {
      return path;
    }
    return BASE_URL + path;
  });
};
</script>
<style scoped></style>
