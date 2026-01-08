<template>
  <div class="statistics-container">
    <el-row :gutter="20">
      <el-col
        v-for="(item, index) in statCards"
        :key="index"
        :xs="24"
        :sm="12"
        :md="6"
      >
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <!-- 左侧：图标 -->
            <div
              class="icon-wrapper"
              :style="{ backgroundColor: item.bgColor }"
            >
              <el-icon :size="30" :color="item.iconColor">
                <component :is="item.icon" />
              </el-icon>
            </div>

            <!-- 右侧：统计数值 -->
            <div class="info-wrapper">
              <el-statistic :value="item.value" :precision="0">
                <template #title>
                  <div class="stat-title">{{ item.label }}</div>
                </template>
              </el-statistic>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from "vue";
import {
  Document,
  ChatLineRound,
  Picture,
  Link,
} from "@element-plus/icons-vue"; 
import { getStatistics } from "@/api/dashboard";
const rawData = ref({
  articleCount: 0,
  commentCount: 0,
  momentCount: 0,
  friendLinkCount: 0,
});
const loading = ref(false);

// 核心配置：定义每个卡片的 标题、对应字段、图标、颜色
// 使用 computed 确保当 rawData 变化时，statCards 自动更新
const statCards = computed(() => [
  {
    label: "文章总数",
    value: rawData.value.articleCount,
    icon: Document,
    iconColor: "#409EFF", // 蓝色
    bgColor: "#ecf5ff", // 浅蓝背景
  },
  {
    label: "评论总数",
    value: rawData.value.commentCount,
    icon: ChatLineRound,
    iconColor: "#67C23A", // 绿色
    bgColor: "#f0f9eb",
  },
  {
    label: "动态总数",
    value: rawData.value.momentCount,
    icon: Picture,
    iconColor: "#E6A23C", // 橙色
    bgColor: "#fdf6ec",
  },
  {
    label: "友链总数",
    value: rawData.value.friendLinkCount,
    icon: Link,
    iconColor: "#909399", // 灰色
    bgColor: "#f4f4f5",
  },
]);

// 获取数据方法
const fetchData = async () => {
  loading.value = true;
  try {
    const data = await getStatistics();
    // 假设 res.data 返回的就是那个 Map 结构
    rawData.value = data;
  } catch (err) {
    console.error("获取统计数据失败", err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchData();
});
</script>
<style scoped>
.statistics-container {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
  align-items: center;
  border-radius: 10px;
}

:deep(.el-card__body) {
  width: 100%;
  padding: 0 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  width: 70px;
  height: 100px;

  display: flex;
  justify-content: center;
  align-items: center;
}

.info-wrapper {
  text-align: center;
  flex: 1;
}

.stat-title {
  font-size: 16px;
  color: #909399;
  margin-bottom: 4px;
}

/* 修改 Statistic 数字的样式 */
:deep(.el-statistic__content) {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
</style>
