<template>
  <div class="home">
    <div class="statics">
      <div>
        <StatisticCard />
      </div>
      <div class="medium">
        <div class="category">
          <CategoryPieChart />
        </div>
        <div class="tags">
          <TagsSunburst />
        </div>
      </div>
      <div class="heatmap-card">
        <HeatmapCard></HeatmapCard>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "MyHome",
};
</script>
<script setup>
import CategoryPieChart from "@/components/CategoryPieChart.vue";
import HeatmapCard from "@/components/HeatmapCard.vue";
import StatisticCard from "@/components/StatisticCard.vue";
import TagsSunburst from "@/components/TagsSunburst.vue";
</script>
<style scoped>
.home {
  display: flex;
  margin: 20px;
  height: 80%;
}
.statics {
  width: 100%;
}
.category {
  width: 50%;
  margin-right: 10px;
}
.tags {
  width: 50%;
  margin-left: 10px;
}
.medium {
  display: flex;
}
</style>
