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
      formatter: "{b}: {c} ({d}%)",
    },
    legend: {
      bottom: "0%",
      left: "center",
    },
    series: [
      {
        name: "文章分类",
        type: "pie",
        radius: ["30%", "70%"],
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
