<template>
  <div class="sprout-loader" role="status" :aria-label="text || '加载中'">
    <div class="sprout-loader__scene" :style="{ '--sprout-scale': scale }">
      <div class="sprout-loader__stem"></div>
      <div class="sprout-loader__pot"></div>
    </div>
    <p v-if="text" class="sprout-loader__text">{{ text }}</p>
  </div>
</template>

<script setup>
defineProps({
  // 加载提示文案，留空则只显示动画
  text: { type: String, default: '' },
  // 整体缩放倍数
  scale: { type: Number, default: 1 }
})
</script>

<style scoped>
.sprout-loader {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  width: 100%;
  min-height: 120px;
  padding: 32px 20px;
  color: var(--text-muted);
  font-size: 15px;
}

.sprout-loader__text {
  margin: 0;
}

.sprout-loader__scene {
  position: relative;
  width: calc(64px * var(--sprout-scale));
  height: calc(72px * var(--sprout-scale));
}

/* 茎：从花盆里长出来，循环“破土—生长—消散” */
.sprout-loader__stem {
  position: absolute;
  bottom: calc(26px * var(--sprout-scale));
  left: 50%;
  width: calc(4px * var(--sprout-scale));
  height: 0;
  border-radius: calc(4px * var(--sprout-scale));
  background: var(--accent-hover);
  transform: translateX(-50%);
  transform-origin: bottom center;
  animation: sprout-grow 2.4s ease-in-out infinite;
}

/* 两片叶子：茎长成后弹出 */
.sprout-loader__stem::before,
.sprout-loader__stem::after {
  content: '';
  position: absolute;
  top: calc(-9px * var(--sprout-scale));
  width: calc(16px * var(--sprout-scale));
  height: calc(10px * var(--sprout-scale));
  background: var(--accent);
  border-radius: 100% 0 100% 0;
  opacity: 0;
}

.sprout-loader__stem::before {
  left: calc(-15px * var(--sprout-scale));
  transform: rotate(-30deg) scale(0);
}

.sprout-loader__stem::after {
  right: calc(-15px * var(--sprout-scale));
  transform: rotate(30deg) scale(0) scaleX(-1);
}

@keyframes sprout-grow {
  0% { height: 0; opacity: 1; }
  12% { height: 0; }
  40% { height: calc(30px * var(--sprout-scale)); }
  82% { height: calc(30px * var(--sprout-scale)); opacity: 1; }
  94% { height: calc(30px * var(--sprout-scale)); opacity: 0; }
  100% { height: 0; opacity: 0; }
}

/* 叶子弹出的缩放放在各伪元素自身，避免覆盖旋转基角 */
.sprout-loader__stem::before {
  animation: sprout-leaf-left 2.4s ease-in-out infinite;
}

.sprout-loader__stem::after {
  animation: sprout-leaf-right 2.4s ease-in-out infinite;
}

@keyframes sprout-leaf-left {
  0%, 30% { transform: rotate(-30deg) scale(0); opacity: 0; }
  42% { transform: rotate(-30deg) scale(1.15); opacity: 1; }
  48% { transform: rotate(-30deg) scale(1); opacity: 1; }
  82% { transform: rotate(-30deg) scale(1); opacity: 1; }
  94% { transform: rotate(-30deg) scale(1); opacity: 0; }
  100% { transform: rotate(-30deg) scale(0); opacity: 0; }
}

@keyframes sprout-leaf-right {
  0%, 30% { transform: rotate(30deg) scaleX(-1) scale(0); opacity: 0; }
  42% { transform: rotate(30deg) scaleX(-1) scale(1.15); opacity: 1; }
  48% { transform: rotate(30deg) scaleX(-1) scale(1); opacity: 1; }
  82% { transform: rotate(30deg) scaleX(-1) scale(1); opacity: 1; }
  94% { transform: rotate(30deg) scaleX(-1) scale(1); opacity: 0; }
  100% { transform: rotate(30deg) scaleX(-1) scale(0); opacity: 0; }
}

/* 花盆：暖土色梯形 + 盆沿，亮暗主题下都协调 */
.sprout-loader__pot {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: calc(40px * var(--sprout-scale));
  height: calc(26px * var(--sprout-scale));
  background: #B98A5E;
  clip-path: polygon(0 18%, 100% 18%, 82% 100%, 18% 100%);
}

.sprout-loader__pot::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: calc(48px * var(--sprout-scale));
  height: calc(8px * var(--sprout-scale));
  border-radius: calc(3px * var(--sprout-scale));
  background: #A8794F;
}

/* 尊重系统的“减少动态效果”偏好 */
@media (prefers-reduced-motion: reduce) {
  .sprout-loader__stem {
    height: calc(30px * var(--sprout-scale));
    animation: none;
  }

  .sprout-loader__stem::before {
    transform: rotate(-30deg) scale(1);
    animation: none;
  }

  .sprout-loader__stem::after {
    transform: rotate(30deg) scaleX(-1) scale(1);
    animation: none;
  }
}
</style>
