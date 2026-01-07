<template>
  <!-- 使用 ref 属性来创建一个模板引用 -->
  <div ref="chartContainer" class="chart-container"></div>
</template>

<script setup>
import * as echarts from "echarts";
// 从 vue 中导入 ref 和 onMounted
import { ref, onMounted } from "vue";

// 创建一个 ref 来引用 DOM 元素
const chartContainer = ref(null);

onMounted(() => {
  // onMounted 钩子确保在 DOM 元素挂载后再执行
  // 通过 .value 访问 ref 引用的元素
  if (chartContainer.value) {
    const myChart = echarts.init(chartContainer.value);

    const option = {
      xAxis: {
        type: "category",
        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          data: [820, 932, 901, 934, 1290, 1330, 1320],
          type: "line",
          smooth: true,
        },
      ],
    };

    myChart.setOption(option);
  }
});
</script>

<style scoped>
/* 必须为图表容器指定一个高度，否则它将不可见 */
.chart-container {
  height: 350px;
}
</style>
