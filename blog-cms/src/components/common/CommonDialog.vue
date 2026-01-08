<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="400px"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      @submit.prevent
    >
      <el-form-item :label="config.itemName + '名称'" prop="name">
        <el-input v-model="formData.name" placeholder="请输入名称" />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleConfirm">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script setup>
import { computed } from "vue";
import { ref, reactive, defineExpose, defineEmits } from "vue";
import { ElMessage } from "element-plus";
const emit = defineEmits(["success"]);

const visible = ref(false);
const formRef = ref(null);
const loading = ref(false);
let config = reactive({
  itemName: "项目",
  addApi: null,
  updateApi: null,
});
const formData = reactive({
  id: null,
  name: "",
});
// 计算标题：根据是否有 ID 判断是新增还是修改
const dialogTitle = computed(() => {
  const action = formData.id ? "修改" : "新增";
  return `${action}${config.itemName}`;
});

// 简单校验
const rules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
};

const open = (row, options) => {
  visible.value = true;

  // 1. 接收配置（父组件传过来的 API 和 名称）
  config.itemName = options.itemName;
  config.addApi = options.addApi;
  config.updateApi = options.updateApi;

  // 2. 初始化表单数据
  if (row) {
    formData.id = row.id;
    formData.name = row.name;
  } else {
    formData.id = null;
    formData.name = "";
  }
};

const close = () => {
  visible.value = false;
};

// 点击确定的逻辑
const handleConfirm = async () => {
  if (!formData.name) {
    ElMessage.warning("请输入${config.itemName}名称");
    return;
  }
  loading.value = true;

  if (!config.addApi || !config.updateApi) {
    ElMessage.error("API 配置缺失");
    return;
  }

  loading.value = true;
  try {
    if (formData.id) {
      // 调用父组件传进来的【修改】接口
      await config.updateApi(formData);
      ElMessage.success("修改成功");
    } else {
      // 调用父组件传进来的【新增】接口
      await config.addApi(formData);
      ElMessage.success("新增成功");
    }

    visible.value = false;
    emit("success");
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 关闭后的清理
const handleClosed = () => {
  formData.id = null;
  formData.name = "";
  if (formRef.value) formRef.value.resetFields();
};

// 使用 defineExpose 允许父组件调用 open 和 close
defineExpose({
  open,
  close,
});
</script>
<style scoped></style>
