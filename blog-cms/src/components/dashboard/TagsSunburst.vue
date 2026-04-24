<template>
  <el-card shadow="never" class="chart-card">
    <template #header>
      <div class="card-header">
        <span>热门标签分布</span>
      </div>
    </template>
    <div ref="chartRef" style="width: 100%; height: 350px"></div>
  </el-card>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { getTagStatistics } from "@/api/dashboard";

const chartRef = ref(null);
let myChart = null;

const initChart = (data) => {
  const option = {
    color: [
      "#409EFF",
      "#67C23A",
      "#E6A23C",
      "#F56C6C",
      "#909399",
      "#36cfc9",
      "#9254de",
      "#f759ab",
    ],

    tooltip: {
      trigger: "item",
      formatter: "{b} : {c} 篇 ({d}%)",
    },
    legend: {
      top: "bottom",
      type: "scroll",
    },
    toolbox: {
      show: true,
      feature: {
        saveAsImage: { show: true },
      },
    },
    series: [
      {
        name: "文章标签",
        type: "pie",
        roseType: "radius",

        radius: ["15%", "75%"],
        center: ["50%", "50%"],
        itemStyle: {
          borderRadius: 8,
          borderColor: "#fff",
          borderWidth: 1,
        },
        label: {
          show: true,
        },
        emphasis: {
          label: {
            show: true,
            fontWeight: "bold",
          },
        },

        data: data,
      },
    ],
  };

  myChart.setOption(option);
};

const fetchData = async () => {
  try {
    const data = await getTagStatistics();
    const list = data || [];

    initChart(list);
  } catch (error) {
    console.error("获取标签统计失败", error);
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
<style scoped></style>
