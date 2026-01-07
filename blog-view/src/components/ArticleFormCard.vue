<template>
  <div>
    <el-card shadow="never">
      <el-form
        :model="articleForm"
        :rules="rules"
        ref="articleFormRef"
        label-position="top"
      >
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="文章内容" prop="content">
          <!-- 
          MdEditor 配置说明：
          1. v-model: 双向绑定内容
          2. @onUploadImg: 监听图片上传（包括点击上传按钮 和 截图粘贴）
          3. preview-theme: 预览主题 (推荐 'github' 或 'smart-blue')
          4. code-theme: 代码高亮主题 (推荐 'atom' 或 'github')
          5. toolbars:以此配置显示的工具栏
        -->
          <MdEditor
            v-model="articleForm.content"
            placeholder="支持 Markdown 语法，可直接截图粘贴图片..."
            preview-theme="default"
            code-theme="atom"
            @onUploadImg="handleUploadImage"
            style="height: calc(100vh - 100px)"
          />
        </el-form-item>

        <!-- Sidebar Area -->

        <template #header>
          <span>发布设置</span>
        </template>

        <el-form-item label="文章摘要" prop="summary">
          <el-input
            type="textarea"
            v-model="articleForm.summary"
            :autosize="{ minRows: 3, maxRows: 10 }"
            placeholder="请输入文章摘要"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select
                v-model="articleForm.categoryId"
                placeholder="请选择分类"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签" prop="tags">
              <el-select
                v-model="articleForm.tags"
                multiple
                placeholder="请选择标签"
                tag="success"
                :popper-append-to-body="false"
              >
                <el-option
                  v-for="tag in tagsList"
                  :key="tag.id"
                  :label="tag.name"
                  :value="tag.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="允许评论">
          <el-switch
            v-model="articleForm.isComment"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="saveDraft" :loading="loading">保存草稿</el-button>
          <el-button type="primary" @click="publishArticle" :loading="loading"
            >发布文章</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, defineProps } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getAllCategories } from "@/api/category";
import { getAllTags } from "@/api/tags";
import { createArticle, getArticleById, updateArticle } from "@/api/article";
import { useRoute } from "vue-router";
import { MdEditor } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import { uploadImage } from "@/api/upload";

const router = useRouter();
const articleFormRef = ref(null);
const loading = ref(false);
const route = useRoute();

const articleForm = ref({
  id: null,
  title: "",
  content: "",
  summary: "",
  categoryId: null,
  tags: [],
  coverImage: "",
  isComment: 1,
  status: 0,
  publishTime: null,
});

const categories = ref([]);
const tagsList = ref([]);

const props = defineProps({
  initData: {
    type: Object,
    default: () => null,
  },
});

watch(
  () => props.initData,
  (newData) => {
    if (newData) {
      articleForm.value.title = newData.title;
      articleForm.value.summary = newData.summary;
      articleForm.value.content = newData.content;
    }
  },

  {
    immediate: true, //立即执行
    deep: true,
  }
);

const rules = ref({
  title: [{ required: true, message: "请输入文章标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入文章内容", trigger: "blur" }],
  categoryId: [{ required: true, message: "请选择分类", trigger: "change" }],
});

const submitArticle = async (status) => {
  loading.value = true;
  const articleData = {
    ...articleForm.value,
    tags: articleForm.value.tags.join(","),
    status: status,
  };

  try {
    if (articleForm.value.id) {
      console.log("正在更新文章", articleForm.value);

      await updateArticle(articleData.id, articleData);
    } else {
      await createArticle(articleData);
      ElMessage.success(status === 1 ? "文章发布成功" : "草稿保存成功");
    }
    router.push("/articlemgmt");
  } catch (error) {
    console.error("Failed to submit article:", error);
    ElMessage.error("操作失败，请重试");
  } finally {
    loading.value = false;
  }
};
onMounted(async () => {
  try {
    categories.value = await getAllCategories();
    tagsList.value = await getAllTags();
  } catch (error) {
    console.error("Failed to load categories or tags:", error);
    ElMessage.error("加载分类或标签失败");
  }

  const articleId = route.params.id;
  if (articleId) {
    await loadArticleDetail(articleId);
  }
});
const saveDraft = () => {
  submitArticle(0);
};

const publishArticle = () => {
  articleFormRef.value.validate((valid) => {
    if (valid) {
      submitArticle(1);
    } else {
      ElMessage.error("请填写所有必填项");
      return false;
    }
  });
};

const loadArticleDetail = async (id) => {
  try {
    const res = await getArticleById(id);
    const data = res;

    articleForm.value = {
      id: data.id,
      title: data.title,
      content: data.content,
      summary: data.summary,
      coverImage: data.coverImage,
      categoryId: data.categoryId, // 确保类型一致(Number/String)
      isComment: data.isComment,
      status: data.status,
      tags: data.tags,
    };
  } catch (error) {
    console.error("获取详情失败", error);
  }
};

/**
 * @param {Array<File>} files
 * @param {Function} callback
 */
const handleUploadImage = async (files, callback) => {
  try {
    const res = await Promise.all(
      files.map((file) => {
        // 关键点 1: 添加 reject 参数
        return new Promise((resolve, reject) => {
          const formData = new FormData();
          formData.append("file", file);

          uploadImage(formData)
            .then((response) => {
              console.log("后端返回的原始数据:", response);
              // 关键点 2: 假设后端返回的数据结构是 { code: 200, data: { url: '...' } }
              // 这里 resolve 只要返回那个 URL 字符串即可
              // 直接判断 url 是否存在
              if (response && response.url) {
                // 这里的 response 就是你打印出来的那个包含 url 的对象
                resolve("http://localhost:8081/upload" + response.url);
              } else {
                reject(new Error("返回结果中没有URL"));
              }
            })
            .catch((err) => {
              console.error("单个图片上传失败", err);
              reject(err);
            });
        });
      })
    );

    // 关键点 3: res 现在是一个 URL 字符串数组，例如 ["/2025/12/31/xxx.png"]
    // 这正是编辑器需要的格式
    callback(res);
  } catch (error) {
    console.error("图片上传过程中出现错误:", error);
    // 这里可以加一个全局提示，比如 el-message
  }
};
</script>

<style scoped>
.markdown-editor {
  width: 100%;
  height: 100%;
}
/* 修复全屏编辑时的层级问题 */
.md-editor {
  z-index: 999 !important;
}
</style>
