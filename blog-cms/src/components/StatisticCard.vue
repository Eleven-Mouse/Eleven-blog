<template>
  <div>
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
        <el-statistic title="博客数量：" :value="268500" />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
        <el-statistic :value="138">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              访问数量
              <el-icon style="margin-left: 4px" :size="12">
                <Male />
              </el-icon>
            </div>
          </template>
          <template #suffix>/100</template>
        </el-statistic>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
        <el-statistic title="评论数量" :value="outputValue" />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
        <el-statistic title="点赞量" :value="562">
          <template #suffix>
            <el-icon style="vertical-align: -0.125em">
              <ChatLineRound />
            </el-icon>
          </template>
        </el-statistic>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>

    
</script>