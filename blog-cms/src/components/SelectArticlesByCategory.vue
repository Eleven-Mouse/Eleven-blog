<template>
  <el-select
    :model-value="modelValue"
    @update:model-value="handleChange"
    :placeholder="placeholder"
    :clearable="clearable"
    :filterable="filterable"
    :loading="loading"
    style="width: 250px"
  >
    <el-option
      v-for="item in categories"
      :key="item.id"
      :label="item.name"
      :value="item.id"
    />
  </el-select>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { getAllCategories } from "@/api/category";

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: "",
  },

  placeholder: {
    type: String,
    default: "请选择分类",
  },

  clearable: {
    type: Boolean,
    default: true,
  },

  filterable: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(["update:modelValue", "change"]);

const categories = ref([]);
const loading = ref(false);

const getAllCategoriesList = async () => {
  loading.value = true;
  try {
    const data = await getAllCategories();

    categories.value = data || [];
  } catch (error) {
    console.error("加载分类列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 处理选中值变化
const handleChange = (val) => {
  emit("update:modelValue", val);
  emit("change", val);
};

// 挂载时自动加载数据
onMounted(() => {
  getAllCategoriesList();
});
</script>
