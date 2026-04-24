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
        <el-input
          v-model="formData.name"
          :placeholder="`请输入${config.itemName}名称`"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, reactive } from "vue";
import { ElMessage } from "element-plus";

const emit = defineEmits(["success"]);

const visible = ref(false);
const formRef = ref(null);
const loading = ref(false);

const config = reactive({
  itemName: "项目",
  addApi: null,
  updateApi: null,
});

const formData = reactive({
  id: null,
  name: "",
});

const dialogTitle = computed(() => {
  const action = formData.id ? "修改" : "新增";
  return `${action}${config.itemName}`;
});

const rules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
};

const open = (row, options) => {
  visible.value = true;
  config.itemName = options.itemName;
  config.addApi = options.addApi;
  config.updateApi = options.updateApi;

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

const handleConfirm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    if (!config.addApi || !config.updateApi) {
      ElMessage.error("API 配置缺失");
      return;
    }

    loading.value = true;
    try {
      if (formData.id) {
        await config.updateApi(formData);
        ElMessage.success("修改成功");
      } else {
        await config.addApi(formData);
        ElMessage.success("新增成功");
      }
      visible.value = false;
      emit("success");
    } catch (err) {
      console.error(err);
      ElMessage.error("操作失败");
    } finally {
      loading.value = false;
    }
  });
};

const handleClosed = () => {
  formData.id = null;
  formData.name = "";
  if (formRef.value) formRef.value.resetFields();
};

defineExpose({ open, close });
</script>
