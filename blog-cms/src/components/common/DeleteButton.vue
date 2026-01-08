<template>
  <div class="deleteButton">
    <el-popconfirm
      :title="`确定要删除该${itemType}吗？此操作不可恢复。`"
      confirm-button-text="确定删除"
      cancel-button-text="取消"
      width="220"
      icon-color="#f56c6c"
      @confirm="handleDelete"
    >
      <template #reference>
        <!-- 只有点击这个按钮，才会弹出上面的气泡 -->
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
  </div>
</template>
<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
  // 传入 API 函数
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
    console.error(error);
  } finally {
    loading.value = false;
  }
};
</script>
<style scoped></style>
