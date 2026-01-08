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
              :src="parseImages(row.image)[0]"
              :preview-src-list="parseImages(row.image)"
              fit="cover"
              preview-teleported
            />
            <el-badge
              v-if="parseImages(row.image).length > 1"
              :value="parseImages(row.image).length - 1"
              class="item"
              type="primary"
              style="position: absolute"
            >
            </el-badge>
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
              <DeleteButton
                :id="row.id"
                :api-func="deleteMomentById"
                @success="handleDeleteSuccess"
              />
            </span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; display: flex; justify-content: center">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, prev, pager, next, jumper"
          @size-change="getMoments"
          @current-change="getMoments"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { deleteMomentById, getMomentList } from "@/api/moment";
import DeleteButton from "@/components/common/DeleteButton.vue";
const moments = ref([]);
const loading = ref(false);
const error = ref(null);
const BASE_URL = import.meta.env.VITE_APP_UPLOAD_URL;

const pagination = ref({
  page: 1,
  size: 10,
  total: 0,
});
const getMoments = async () => {
  loading.value = true;
  error.value = null;

  try {
    const params = {
      page: pagination.value.page,
      size: pagination.value.size,
    };
    const data = await getMomentList(params);
    moments.value = data.list || [];

    pagination.value.total = data.total || 0;
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
const handleDeleteSuccess = (deleteId) => {
  moments.value = moments.value.filter((item) => item.id !== deleteId);
};

const parseImages = (imageStr) => {
  if (!imageStr) return [];

  const paths = imageStr.split(",");

  return paths.map((path) => {
    if (path.startsWith("http")) {
      return path;
    }
    return BASE_URL + path;
  });
};
onMounted(async () => {
  getMoments();
});
</script>

<style scoped></style>
