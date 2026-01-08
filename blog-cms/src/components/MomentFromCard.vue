<template>
  <el-card shadow="never">
    <el-form
      :model="momentForm"
      :rules="rules"
      ref="momentFormRef"
      label-position="top"
    >
      <el-form-item label="动态内容" prop="content">
        <el-input
          v-model="momentForm.content"
          type="textarea"
          :rows="4"
          placeholder="分享你的新鲜事..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="配图（最多9张）" prop="image">
        <!-- 

            list-type: picture-card 显示为照片墙
            on-success: 上传成功的回调
            on-remove: 删除图片的回调
            on-preview: 点击预览大图
          -->
        <el-upload
          v-model:file-list="fileList"
          list-type="picture-card"
          :limit="9"
          :http-request="handleHttpRequest"
          :on-exceed="handleExceed"
          :on-preview="handlePictureCardPreview"
          :on-success="handleUploadSuccess"
          :before-upload="beforeAvatarUpload"
          name="file"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button @click="saveDraft" :loading="loading">保存草稿</el-button>
        <el-button type="primary" @click="publishMomnet" :loading="loading"
          >发布文章</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 图片预览弹窗 -->
    <el-dialog v-model="dialogVisible">
      <img
        w-full
        :src="dialogImageUrl"
        alt="Preview Image"
        style="width: 100%"
      />
    </el-dialog>
  </el-card>
</template>
<script setup>
import { ref } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { createMoment } from "@/api/moment";
import request from "@/utils/request";
import Compressor from "compressorjs";
const momentForm = ref({
  content: "",
  image: "",
});
const momentFormRef = ref(null);
const loading = ref(false);
const BASE_URL = import.meta.env.VITE_APP_UPLOAD_URL;
// 图片预览相关
const dialogImageUrl = ref("");
const dialogVisible = ref(false);

const fileList = ref([]);

const rules = {
  content: [{ required: true, message: "请输入动态内容", trigger: "blur" }],
};
const beforeAvatarUpload = (rawFile) => {
  const isJPGorPNG =
    rawFile.type === "image/jpeg" || rawFile.type === "image/png";
  const isLt5M = rawFile.size / 1024 / 1024 < 5;

  if (!isJPGorPNG) {
    ElMessage.error("上传图片只能是 JPG/PNG 格式!");
    return false;
  }
  if (!isLt5M) {
    ElMessage.error("上传图片大小不能超过 5MB!");
    return false;
  }
  return true;
};
const handleHttpRequest = async (options) => {
  const { file } = options;

  // 返回一个 Promise，让 el-upload 等待这个过程结束
  return new Promise((resolve, reject) => {
    new Compressor(file, {
      quality: 0.6,
      success(result) {
        const formData = new FormData();
        // 使用压缩后的 result
        formData.append("file", result, result.name || file.name);

        request
          .post("/upload/images", formData)
          .then((res) => {
            // 这里只需要 resolve(res)，el-upload 会自动触发 onSuccess
            resolve(res);
          })
          .catch((err) => reject(err));
      },
      error(err) {
        // 压缩失败，上传原图
        const formData = new FormData();
        formData.append("file", file);
        request
          .post("/upload/images", formData)
          .then((res) => resolve(res))
          .catch((err) => reject(err));
      },
    });
  });
};
// 2. 上传成功回调
const handleUploadSuccess = (response, uploadFile) => {
  const relativePath = typeof response === "string" ? response : response.data;

  if (relativePath) {
    uploadFile.url = BASE_URL + relativePath;
    uploadFile.actualPath = relativePath;

    console.log("预览图地址:", uploadFile.url);
    ElMessage.success("上传成功");
  }
};

// 4. 预览图片回调
const handlePictureCardPreview = (uploadFile) => {
  const url = uploadFile.url;
  if (url.startsWith("http")) {
    dialogImageUrl.value = url;
  } else {

    dialogImageUrl.value = `${BASE_URL}${url}`;
  }
  dialogVisible.value = true;
};

// 5. 超出数量限制
const handleExceed = () => {
  ElMessage.warning("最多只能上传 9 张图片");
};

const processImages = () => {
  const rawUrls = fileList.value.map((item) => {
    // 如果有我们刚才存的自定义属性 actualPath，优先用它
    if (item.actualPath) {
      return item.actualPath;
    }

    if (item.response) {
      return typeof item.response === "string"
        ? item.response
        : item.response.data;
    }

    // 兜底：如果 url 里有相对路径特征，手动截取
    // 比如把 http://localhost:8081/images/1.png 变成 /images/1.png
    if (item.url && item.url.includes("/images/")) {
      return item.url.substring(item.url.indexOf("/images/"));
    }

    return null;
  });

  const validUrls = rawUrls.filter(
    (url) => url && typeof url === "string" && !url.startsWith("blob:")
  );

  console.log("最终提交给后端的字符串:", validUrls.join(","));
  return validUrls.join(",");
};
// 发布文章
const publishMomnet = async () => {
  if (!momentFormRef.value) return;

  await momentFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const imageUrls = processImages();

        await createMoment({
          content: momentForm.value.content,
          image: imageUrls,
          status: 1,
        });

        console.log("提交的数据:", momentForm.value.content, imageUrls);

        ElMessage.success("发布成功！");

        // 3. 重置表单
        momentFormRef.value.resetFields();
        fileList.value = [];
      } catch (e) {
        console.error(e);
        ElMessage.error("发布失败");
      } finally {
        loading.value = false;
      }
    }
  });
};

const saveDraft = async () => {
  loading.value = true;
  try {
    const imageUrls = processImages();

    await createMoment({
      content: momentForm.value.content,
      image: imageUrls,
      status: 0,
    });
    ElMessage.success("草稿保存成功");

    // 草稿保存后是否要重置？看需求，通常不重置或者跳转页面
  } catch (e) {
    console.error(e);
    ElMessage.error("保存草稿失败");
  } finally {
    loading.value = false;
  }
};
</script>
<style scoped>
:deep(.el-upload--picture-card) {
  border-style: dashed;
}
</style>
