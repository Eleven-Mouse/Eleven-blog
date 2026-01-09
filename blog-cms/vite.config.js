import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],

  define: {
    "process.env": {},
  },

  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },

  server: {
    // 【情况一】修改前端项目启动端口
    port: 8080,
    host: "0.0.0.0", // 允许局域网访问
    open: true, // 启动后自动打开浏览器
    proxy: {
      "/admin": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
    },
  },
});
