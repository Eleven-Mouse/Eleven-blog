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

    <el-card shadow="never" class="page-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span class="card-header__title">GitHub 文章同步</span>
          <span class="card-header__desc">配置仓库后，系统自动扫描 .md 文件并按分类/标签同步到博客</span>
        </div>
      </template>

      <el-form
        :model="form"
        label-width="100px"
        class="config-form"
      >
        <el-form-item label="仓库所有者">
          <el-input v-model="form.github_sync_owner" placeholder="如 Eleven-Mouse" />
        </el-form-item>

        <el-form-item label="仓库名称">
          <el-input v-model="form.github_sync_repo" placeholder="如 my-blog-posts" />
        </el-form-item>

        <el-form-item label="分支">
          <el-input v-model="form.github_sync_branch" placeholder="main" />
          <div class="form-tip">默认 main，可改为 master 或其他分支名</div>
        </el-form-item>

        <el-form-item label="目录路径">
          <el-input v-model="form.github_sync_path" placeholder="如 posts 或留空表示根目录" />
          <div class="form-tip">Markdown 文件所在目录，支持子目录递归扫描（子目录名会作为分类的兜底匹配）</div>
        </el-form-item>

        <el-form-item label="GitHub Token">
          <el-input v-model="form.github_sync_token" placeholder="ghp_xxxx（可选，提升API限流至5000次/小时）" show-password />
          <div class="form-tip">Personal Access Token，建议勾选 repo 权限。不填则使用匿名访问（60次/小时限制）</div>
        </el-form-item>

        <el-form-item label="自动发布">
          <el-switch v-model="autoPublish" active-text="自动发布" inactive-text="草稿" />
          <div class="form-tip">开启后，自动发现的文章将直接发布；关闭则为草稿状态，需手动发布</div>
        </el-form-item>

        <el-form-item label="Webhook Secret">
          <el-input v-model="form.webhook_secret" placeholder="用于验证 GitHub Webhook 推送来源（可选）" show-password />
          <div class="form-tip">在 GitHub 仓库 Settings → Webhooks 中设置相同的 Secret</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
          <el-button @click="loadConfig">重置</el-button>
          <el-button type="success" @click="handleDiscover" :loading="discovering">
            扫描仓库
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <div class="fm-help">
        <div class="fm-help__title">Markdown 文件格式说明</div>
        <p class="fm-help__desc">在 .md 文件顶部添加以下格式的元数据，系统会自动解析分类和标签：</p>
        <pre class="fm-help__code">---
title: 文章标题
category: Java
tags: Spring Boot,后端
summary: 文章摘要（可选）
---

# 正文内容从这里开始</pre>
        <div class="fm-help__rules">
          <p><strong>匹配规则：</strong></p>
          <ul>
            <li><strong>标题</strong>：Front Matter title &gt; 文件名（去掉 .md）</li>
            <li><strong>分类</strong>：Front Matter category &gt; 所在文件夹名 &gt; 不设置</li>
            <li><strong>分类自动创建</strong>：数据库中不存在对应分类时自动创建</li>
            <li><strong>标签</strong>：仅从 Front Matter tags 读取，逗号分隔</li>
            <li><strong>自动发布</strong>：可在上方开关控制新建文章是否自动发布</li>
          </ul>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getBlogConfig, updateBlogConfig } from "@/api/systemConfig";
import { discoverGithub } from "@/api/article";

const formRef = ref(null);
const loading = ref(false);
const saving = ref(false);
const discovering = ref(false);

const form = reactive({
  blog_avatar: "",
  blog_name: "",
  blog_bio: "",
  blog_github_url: "",
  blog_footer_desc: "",
  blog_copyright_name: "",
  github_sync_owner: "",
  github_sync_repo: "",
  github_sync_branch: "main",
  github_sync_path: "",
  github_sync_token: "",
  github_sync_auto_publish: "false",
  webhook_secret: "",
});

const autoPublish = ref(false);

const loadConfig = async () => {
  loading.value = true;
  try {
    const data = await getBlogConfig();
    if (data && typeof data === "object") {
      Object.keys(form).forEach((key) => {
        form[key] = data[key] || "";
      });
      autoPublish.value = form.github_sync_auto_publish === "true";
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
    form.github_sync_auto_publish = autoPublish.value ? "true" : "false";
    await updateBlogConfig({ ...form });
    ElMessage.success("配置已保存");
  } catch (err) {
    ElMessage.error("保存失败：" + (err.message || "未知错误"));
    console.error(err);
  } finally {
    saving.value = false;
  }
};

const handleDiscover = async () => {
  discovering.value = true;
  try {
    await discoverGithub();
    ElMessage.success("同步已在后台启动，请稍后刷新文章列表查看结果");
  } catch (err) {
    ElMessage.error("扫描失败：" + (err.message || "请先保存仓库配置"));
    console.error(err);
  } finally {
    discovering.value = false;
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

.fm-help {
  max-width: 600px;
}

.fm-help__title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.fm-help__desc {
  font-size: 13px;
  color: #606266;
  margin: 0 0 12px;
}

.fm-help__code {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 12px 16px;
  font-size: 13px;
  line-height: 1.6;
  color: #303133;
  overflow-x: auto;
  margin: 0 0 12px;
}

.fm-help__rules {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

.fm-help__rules ul {
  padding-left: 20px;
  margin: 4px 0 0;
}

.fm-help__rules li {
  list-style: disc;
}

@media (max-width: 768px) {
  .config-form,
  .fm-help {
    max-width: 100%;
  }

  .config-form :deep(.el-form-item__label) {
    width: 80px !important;
    font-size: 13px;
  }
}
</style>
