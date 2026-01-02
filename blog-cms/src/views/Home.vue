<template>
  <div class="home">

    <div class="main">
      <div class="statisti-ccard">
        <StatisticCard />
      </div>
      <div class="heatmap-card">
        <HeatmapCard></HeatmapCard>
      </div>
      <div class="views-num-card">
        <ViewsNumCard />
      </div>
    </div>

    <div class="hot-list">
      <div class="hot-article">
        <HotArticle />
      </div>
      <div class="hot-tags">
        <HotTags />
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
import HeatmapCard from "@/components/HeatmapCard.vue";
import HotArticle from "@/components/HotArticle.vue";
import HotTags from "@/components/HotTags.vue";
import StatisticCard from "@/components/StatisticCard.vue";
import ViewsNumCard from "@/components/ViewsNumCard.vue";

</script>
<style scoped>
.home {
  width: 100%;
  display: flex;
}

.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.hot-list {
  display: grid;
  padding-left: 80px;
  float: inline-end;
}
.hot-tags {
  padding-top: 30px;
}
.v-card {
  height: 50px;
}
.main {
  display: grid;
  width: 100%;
  height: 100%;
  padding-left: 50px;
}

.statisti-ccard {
  display: contents;
}
</style>
