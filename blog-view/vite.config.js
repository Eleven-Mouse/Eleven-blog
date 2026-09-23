import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [vue(), vueDevTools()],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },

  build: {
    rollupOptions: {
      output: {
        // 将不常变动的依赖拆为独立 vendor chunk：
        // 业务代码迭代后 vendor 文件名（哈希）不变，老用户直接命中浏览器缓存
        manualChunks: {
          'vendor-vue': ['vue', 'vue-router', 'pinia'],
          'vendor-element': ['element-plus', '@element-plus/icons-vue'],
        },
      },
    },
  },

  server: {
    port: 3000,
    host: '0.0.0.0',
    open: false,
  },
})
