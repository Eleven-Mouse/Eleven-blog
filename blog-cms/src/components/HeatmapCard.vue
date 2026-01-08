<template>
  <el-card shadow="never" class="heatmap-card">
    <template #header>
      <div class="card-header">
        <span>文章贡献度</span>
        <span class="year-tag">{{ currentYear }}</span>
      </div>
    </template>
    <!-- 图表容器，必须指定高度 -->
    <div ref="chartRef" style="width: 100%; height: 250px"></div>
  </el-card>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { getContributionData } from "@/api/dashboard";
const chartRef = ref(null);
let myChart = null;
const currentYear = new Date().getFullYear();

// 1. 初始化图表配置
const initChart = (data) => {

  const formattedData = data.map((item) => {
    return [item.date, item.count];
  });

  const option = {
    tooltip: {
      position: "top",
      formatter: function (p) {
        const format = p.data[0];
        const count = p.data[1];
        return `${format}: 发布了 ${count} 篇文章`;
      },
    },
    visualMap: {
      min: 0,
      max: 5, // 假设一天最多发5篇，超过也是最深色，可根据实际调整
      type: "piecewise",
      orient: "horizontal",
      left: "center",
      top: 0,
      textStyle: {
        color: "#000",
      },
      // 定义颜色区间：0篇灰色，1-5篇逐渐变绿
      pieces: [
        { min: 4, label: "≥5 篇", color: "#216e39" }, // 深绿
        { min: 3, max: 3, label: "3 篇", color: "#30a14e" },
        { min: 2, max: 2, label: "2 篇", color: "#40c463" },
        { min: 1, max: 1, label: "1 篇", color: "#9be9a8" }, // 浅绿
        { value: 0, label: "无", color: "#ebedf0" }, // 灰色
      ],
      show: true, // 显示图例
    },
    calendar: {
      top: 70,
      left: 30,
      right: 30,
      cellSize: ["auto", 20], // 方格大小
      range: currentYear, // 显示当前年份
      itemStyle: {
        borderWidth: 0.5,
      },
      yearLabel: { show: false },
      dayLabel: {
        firstDay: 1, // 周一开始
        nameMap: "cn", // 中文显示周几
      },
      monthLabel: {
        nameMap: "cn", // 中文显示月份
      },
    },
    series: {
      type: "heatmap",
      coordinateSystem: "calendar",
      data: formattedData,
    },
  };

  myChart.setOption(option);
};

// 2. 获取数据
const fetchData = async () => {
  try {
    const data = await getContributionData();
    const list = data || [];

    initChart(list);
  } catch (error) {
    console.error("加载热力图失败", error);
  }
};

onMounted(() => {
  myChart = echarts.init(chartRef.value);
  fetchData();

  window.addEventListener("resize", () => myChart.resize());
});

onUnmounted(() => {
  if (myChart) myChart.dispose();
});
</script>

<style scoped>
.heatmap-card {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.year-tag {
  font-size: 15px;
  color: #909399;
}
</style>
