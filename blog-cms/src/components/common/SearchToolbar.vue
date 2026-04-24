<template>
  <div class="search-toolbar">
    <div class="toolbar-left">
      <slot name="filters" />
    </div>
    <div class="toolbar-right">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.search-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
