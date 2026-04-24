<template>
  <div class="common-table">
    <el-table
      v-bind="$attrs"
      :data="data"
      v-loading="loading"
      :stripe="stripe"
      :border="border"
      :table-layout="tableLayout"
      :default-sort="defaultSort"
      style="width: 100%"
    >
      <el-table-column
        v-if="selectable"
        type="selection"
        width="48"
        @selection-change="handleSelectionChange"
      />
      <slot />
    </el-table>

    <div v-if="showPagination" class="table-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="currentSize"
        :total="total"
        :page-sizes="pageSizes"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  data: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  stripe: { type: Boolean, default: true },
  border: { type: Boolean, default: false },
  tableLayout: { type: String, default: "fixed" },
  selectable: { type: Boolean, default: false },
  showPagination: { type: Boolean, default: true },
  page: { type: Number, default: 1 },
  size: { type: Number, default: 10 },
  total: { type: Number, default: 0 },
  pageSizes: { type: Array, default: () => [10, 20, 50] },
  defaultSort: { type: Object, default: undefined },
});

const emit = defineEmits([
  "update:page",
  "update:size",
  "page-change",
  "size-change",
  "selection-change",
]);

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit("update:page", val),
});

const currentSize = computed({
  get: () => props.size,
  set: (val) => emit("update:size", val),
});

const handlePageChange = (page) => emit("page-change", page);
const handleSizeChange = (size) => emit("size-change", size);
const handleSelectionChange = (val) => emit("selection-change", val);
</script>

<style scoped>
.table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
