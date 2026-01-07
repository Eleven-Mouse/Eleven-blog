import axios from "axios";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../store/auth";
import router from "../router";

// 创建 axios 实例
const service = axios.create({
  baseURL: "/admin",
  timeout: 5000, // 请求超时时间
});

// 1. 请求拦截器：每次请求都在 Header 里带上 Token
service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.accessToken) {
      // 注意：标准格式是 "Bearer " + token
      config.headers.Authorization = `Bearer ${authStore.accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    console.log("后端返回的完整数据:", res);
    if (res.code === undefined) {
      return res;
    }

    if (res.code === 1) {
      return res.data; // code 为 1 表示成功，直接返回 data 部分
    } else {
      // 处理已知的错误情况 (code 不为 1)
      ElMessage({
        message: res.msg || "请求失败",
        type: "error",
        duration: 5 * 1000,
      });
      return Promise.reject(new Error(res.msg || "Error"));
    }
  },
  (error) => {
    console.log("【请求进入了错误回调】");
    console.dir(error); // 使用 dir 可以展开看详细对象

    if (error.response) {
      console.log("错误状态码:", error.response.status);
      console.log("错误数据:", error.response.data);
    }
    // 处理 HTTP 状态码错误
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");
      console.warn("登录过期，请重新登录");
      const authStore = useAuthStore();
      authStore.logout();
    }
    return Promise.reject(error);
  }
);

export default service;
