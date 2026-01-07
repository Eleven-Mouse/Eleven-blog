<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="400px"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      @submit.prevent
    >
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入名称" />
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
import { ref, reactive, defineExpose ,defineEmits} from "vue";




// 定义要传给父组件的事件
const emit = defineEmits(["提交"]);

const visible = ref(false);
const formRef = ref(null);

// 内部表单状态
const form = reactive({
  id: null,
  name: "",
});



// 简单校验
const rules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
};

// --- 核心：暴露给父组件的方法 ---
const open = (row) => {
  form.id = row.id;
  form.name = row.name;
  visible.value = true;
};

const close = () => {
  visible.value = false;
};

// 点击确定的逻辑
const handleConfirm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 校验通过，把表单数据发给父组件
      emit("confirm", { ...form });
    }
  });
};

// 关闭后的清理
const handleClosed = () => {
  form.id = null;
  form.name = "";
  if (formRef.value) formRef.value.resetFields();
};

// 使用 defineExpose 允许父组件调用 open 和 close
defineExpose({
  open,
  close,
});
</script>
<style scoped></style>
