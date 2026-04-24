import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";

export function useTable(fetchApi, options = {}) {
  const data = ref([]);
  const loading = ref(false);
  const error = ref(null);

  const pagination = ref({
    page: 1,
    size: options.defaultSize || 10,
    total: 0,
  });

  // Extra filter params (e.g. categoryId, keyword, status)
  const filters = ref(options.defaultFilters || {});

  const fetchData = async (extraParams = {}) => {
    loading.value = true;
    error.value = null;
    try {
      const params = {
        page: pagination.value.page,
        size: pagination.value.size,
        ...filters.value,
        ...extraParams,
      };
      // Remove null/undefined params
      Object.keys(params).forEach(
        (k) => (params[k] == null || params[k] === "") && delete params[k]
      );

      const res = await fetchApi(params);
      // Support { list, total } and { data, pagination } response formats
      data.value = res.list || res.data || [];
      pagination.value.total =
        res.total || res.pagination?.total || 0;
    } catch (err) {
      error.value = "加载数据失败，请稍后再试";
      console.error(err);
    } finally {
      loading.value = false;
    }
  };

  const handleDeleteSuccess = (deleteId) => {
    data.value = data.value.filter((item) => item.id !== deleteId);
    if (data.value.length === 0 && pagination.value.page > 1) {
      pagination.value.page--;
      fetchData();
    }
  };

  const handlePageChange = (page) => {
    pagination.value.page = page;
    fetchData();
  };
  const handleSizeChange = (size) => {
    pagination.value.page = 1;
    pagination.value.size = size;
    fetchData();
  };

  const resetFilters = () => {
    pagination.value.page = 1;
    fetchData();
  };

  const refresh = () => fetchData();

  if (options.immediate !== false) {
    onMounted(() => fetchData());
  }

  return {
    data,
    loading,
    error,
    pagination,
    filters,
    fetchData,
    refresh,
    handleDeleteSuccess,
    handlePageChange,
    handleSizeChange,
    resetFilters,
  };
}

/**
 * For non-paginated lists (categories, tags, comments, friendlinks)
 */
export function useSimpleList(fetchApi, options = {}) {
  const data = ref([]);
  const loading = ref(false);

  const fetchData = async () => {
    loading.value = true;
    try {
      data.value = (await fetchApi(options.params)) || [];
    } catch (err) {
      console.error(err);
      ElMessage.error("加载数据失败");
    } finally {
      loading.value = false;
    }
  };

  const handleDeleteSuccess = (deleteId) => {
    data.value = data.value.filter((item) => item.id !== deleteId);
  };

  const refresh = () => fetchData();

  if (options.immediate !== false) {
    onMounted(() => fetchData());
  }

  return { data, loading, fetchData, refresh, handleDeleteSuccess };
}
