<template>
  <el-popconfirm
    :title="`确定要删除该${itemType}吗？此操作不可恢复。`"
    confirm-button-text="确定删除"
    cancel-button-text="取消"
    width="220"
    icon-color="#f56c6c"
    @confirm="handleDelete"
  >
    <template #reference>
      <el-button
        type="danger"
        :icon="Delete"
        :loading="loading"
        size="small"
        plain
      >
        删除
      </el-button>
    </template>
  </el-popconfirm>
</template>

<script setup>
import { ref } from "vue";
import { Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
  apiFunc: {
    type: Function,
    required: true,
  },
  itemType: {
    type: String,
    default: "数据",
  },
});

const emit = defineEmits(["success"]);

const loading = ref(false);

const handleDelete = async () => {
  loading.value = true;
  try {
    await props.apiFunc(props.id);
    ElMessage.success("删除成功");
    emit("success", props.id);
  } catch (err) {
    console.error(err);
    ElMessage.error(`删除${props.itemType}失败`);
  } finally {
    loading.value = false;
  }
};
</script>
