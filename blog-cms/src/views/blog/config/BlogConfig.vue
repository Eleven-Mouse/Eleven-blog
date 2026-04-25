<template>
  <div class="page-container">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-header__title">博客配置</span>
          <span class="card-header__desc">修改后将即时生效于前台展示</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        label-width="100px"
        v-loading="loading"
        class="config-form"
      >
        <el-form-item label="博主头像">
          <el-input v-model="form.blog_avatar" placeholder="头像图片 URL 或路径" />
          <div class="form-tip">支持相对路径（如 /avatar.png）或完整 URL</div>
        </el-form-item>

        <el-form-item label="博主昵称">
          <el-input v-model="form.blog_name" placeholder="显示在侧边栏和移动端菜单" />
        </el-form-item>

        <el-form-item label="个性签名">
          <el-input v-model="form.blog_bio" placeholder="一句话介绍自己" />
        </el-form-item>

        <el-form-item label="GitHub 地址">
          <el-input v-model="form.blog_github_url" placeholder="https://github.com/your-username" />
        </el-form-item>

        <el-form-item label="页脚描述">
          <el-input
            v-model="form.blog_footer_desc"
            type="textarea"
            :rows="2"
            placeholder="显示在页面底部的描述文字"
          />
        </el-form-item>

        <el-form-item label="版权名称">
          <el-input v-model="form.blog_copyright_name" placeholder="页脚版权所有者名称" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
          <el-button @click="loadConfig">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getBlogConfig, updateBlogConfig } from "@/api/systemConfig";

const formRef = ref(null);
const loading = ref(false);
const saving = ref(false);

const form = reactive({
  blog_avatar: "",
  blog_name: "",
  blog_bio: "",
  blog_github_url: "",
  blog_footer_desc: "",
  blog_copyright_name: "",
});

const loadConfig = async () => {
  loading.value = true;
  try {
    const data = await getBlogConfig();
    if (data && typeof data === "object") {
      Object.keys(form).forEach((key) => {
        form[key] = data[key] || "";
      });
    }
  } catch (err) {
    ElMessage.error("加载配置失败");
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const handleSave = async () => {
  saving.value = true;
  try {
    await updateBlogConfig({ ...form });
    ElMessage.success("配置已保存");
  } catch (err) {
    ElMessage.error("保存失败：" + (err.message || "未知错误"));
    console.error(err);
  } finally {
    saving.value = false;
  }
};

onMounted(() => {
  loadConfig();
});
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-header__title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-header__desc {
  font-size: 13px;
  color: #909399;
}

.config-form {
  max-width: 600px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .config-form {
    max-width: 100%;
  }

  .config-form :deep(.el-form-item__label) {
    width: 80px !important;
    font-size: 13px;
  }
}
</style>
