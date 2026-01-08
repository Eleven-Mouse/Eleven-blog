<template>
  <div class="editButton">
    <el-button
      type="success"
      :icon="Edit"
      size="small"
      plain
      @click="handleEdit"
    >
      编辑
    </el-button>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
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
const handleEdit = async () => {
  router.push(`${props.targetPath}/${props.id}`);
};
</script>

<style scoped></style>
