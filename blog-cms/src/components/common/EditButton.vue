<template>
  <el-button
    type="primary"
    :icon="Edit"
    size="small"
    plain
    @click="handleEdit"
  >
    编辑
  </el-button>
</template>

<script setup>
import { Edit } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
  targetPath: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const handleEdit = () => {
  router.push(`${props.targetPath}/${props.id}`);
};
</script>
