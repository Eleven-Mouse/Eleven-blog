<template>
  <el-card shadow="never" class="chart-card">
    <template #header>
      <div class="card-header">
        <span>分类文章统计</span>
      </div>
    </template>
    <div ref="chartRef" style="width: 100%; height: 350px"></div>
  </el-card>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { getCategoryStatistics } from "@/api/dashboard";

const chartRef = ref(null);
let myChart = null;

const initChart = (data) => {
  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} ({d}%)", // 显示格式：分类名: 数量 (百分比)
    },
    legend: {
      bottom: "0%",
      left: "center",
    },
    series: [
      {
        name: "文章分类",
        type: "pie",
        radius: ["30%", "70%"], // 内圆和外圆的大小，设为 ['0%', '70%'] 就是实心饼图
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: false,
          position: "center",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: false,
        },
        // 这里的数据直接使用后端返回的 [{name: 'Java', value: 10}, ...]
        data: data,
      },
    ],
  };

  myChart.setOption(option);
};

const fetchData = async () => {
  try {
    const data = await getCategoryStatistics();
    const list = data || [];

    // 如果数据为空，可以给个占位，防止图表空白
    if (list.length === 0) {
      // 可选：显示暂无数据
    }

    initChart(list);
  } catch (error) {
    console.error("获取分类统计失败", error);
  }
};

onMounted(() => {
  myChart = echarts.init(chartRef.value);
  fetchData();
  window.addEventListener("resize", () => myChart.resize());
});

onUnmounted(() => {
  if (myChart) {
    myChart.dispose();
  }
});
</script>
<style scoped>
.tags-article {
  height: 300px;
  width: 200px;
  box-shadow: none;
}
</style>
