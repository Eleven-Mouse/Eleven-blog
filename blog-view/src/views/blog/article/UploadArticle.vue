<template>
  <el-card
    shadow="never"
    style="border-radius: 10px"
    header="导入 Markdown 博客"
  >
    <el-upload
      drag
      accept=".md"
      :auto-upload="false"
      :show-file-list="false"
      :on-change="handleFileChange"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">拖拽文件到此处 或 <em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip">仅支持 .md 文件</div>
      </template>
    </el-upload>

    <!-- 预览区域 -->
    <div v-if="parsedArticleData.content" class="preview-area">
      <h3 style="margin-bottom: 15px">解析预览 & 编辑：</h3>
      <ArticleFormCard :initData="parsedArticleData" />
    </div>
  </el-card>
</template>

<script setup>
import { UploadFilled } from "@element-plus/icons-vue";
import { ref } from "vue";
import { ElMessage } from "element-plus";
import fm from "front-matter";
import ArticleFormCard from "@/components/ArticleFormCard.vue";

const parsedArticleData = ref({
  title: "",
  content: "",
  summary: "",
});

const handleFileChange = async (uploadFile) => {
  const rawFile = uploadFile.raw;

  // 1. 基础校验
  if (!rawFile) return;
  if (rawFile.type !== "text/markdown" && !rawFile.name.endsWith(".md")) {
    ElMessage.error("请上传 Markdown 文件！");
    return;
  }

  // 2. 使用 FileReader 读取文件内容
  try {
    const text = await readFileContent(rawFile);
    const result = fm(text);

    // 处理正文
    const content = result.body;

    // 处理标题
    const fileNameTitle = rawFile.name.replace(/\.md$/i, ""); // "我的文章.md" -> "我的文章"
    const title = result.attributes?.title || fileNameTitle;

    const summary =
      result.attributes?.summary ||
      content.substring(0, 150).replace(/[#*`]/g, "") + ".....";

    // 5. 赋值给传递对象
    parsedArticleData.value = {
      title,
      content,
      summary,
    };
    ElMessage.success("导入成功！");
  } catch (error) {
    console.error(error);
    ElMessage.error("文件读取失败");
  }
};

// 封装 Promise 化的 FileReader
const readFileContent = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    // 指定编码，防止中文乱码
    reader.readAsText(file, "UTF-8");

    reader.onload = (e) => {
      resolve(e.target.result);
    };
    reader.onerror = (e) => {
      reject(e);
    };
  });
};
</script>

<style scoped>
.preview-area {
  margin-top: 20px;
}
</style>
