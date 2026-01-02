<template>
  <!-- 使用 ref 属性来创建一个模板引用 -->
  <div ref="chartContainer" class="chart-container"></div>
</template>

<script setup>
import * as echarts from "echarts";
// 从 vue 中导入 ref 和 onMounted
import { ref, onMounted } from "vue";

// 将 getVirtualData 函数移到 script setup 的顶层
// 这是一个纯函数，不依赖于组件实例，所以放在这里是最佳实践
function getVirtualData(year) {
  const date = +echarts.time.parse(year + "-01-01");
  const end = +echarts.time.parse(+year + 1 + "-01-01");
  const dayTime = 3600 * 24 * 1000;
  const data = [];
  for (let time = date; time < end; time += dayTime) {
    data.push([
      echarts.time.format(time, "{yyyy}-{MM}-{dd}", false),
      Math.floor(Math.random() * 10000),
    ]);
  }
  return data;
}

// 创建一个 ref 来引用 DOM 元素
const chartContainer = ref(null);

onMounted(() => {
  // onMounted 钩子确保在 DOM 元素挂载后再执行
  // 通过 .value 访问 ref 引用的元素
  if (chartContainer.value) {
    const myChart = echarts.init(chartContainer.value);

    const option = {
      title: {
        top: 30,
        left: "center",
        text: "文章贡献表",
      },
      tooltip: {},
      visualMap: {
        min: 0,
        max: 10000,
        type: "piecewise",
        orient: "horizontal",
        left: "center",
        top: 65,
      },
      calendar: {
        top: 120,
        left: 30,
        right: 30,
        cellSize: ["auto", 13],
        range: "2025",
        itemStyle: {
          borderWidth: 0.5,
        },
        yearLabel: { show: false },
      },
      series: {
        type: "heatmap",
        coordinateSystem: "calendar",
        data: getVirtualData("2025"),
      },
    };

    myChart.setOption(option);
  }
});
</script>

<style scoped>
.chart-container {
  height: 300px;
}
</style>
