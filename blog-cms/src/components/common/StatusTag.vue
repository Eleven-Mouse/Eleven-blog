<template>
  <el-tag :type="currentOption.type" :effect="effect" :size="size">
    {{ currentOption.label }}
  </el-tag>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  status: { required: true },
  options: {
    type: Array,
    default: () => [
      { value: 0, label: "草稿", type: "info" },
      { value: 1, label: "已发布", type: "success" },
    ],
  },
  effect: { type: String, default: "light" },
  size: { type: String, default: "default" },
});

const currentOption = computed(() => {
  return (
    props.options.find((o) => o.value === props.status) || {
      label: "未知",
      type: "info",
    }
  );
});
</script>
