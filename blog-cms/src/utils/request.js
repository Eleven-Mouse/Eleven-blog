import axios from "axios";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../store/auth";
import router from "../router";

// 创建 axios 实例
const service = axios.create({
  baseURL:  '/admin',
  timeout: 30000, // 请求超时时间（文章内容较大，需要足够时间）
});

// 是否正在刷新 Token 的标志
let isRefreshing = false;
// 等待 Token 刷新的请求队列
let refreshSubscribers = [];

// 将等待的请求加入队列
function subscribeTokenRefresh(cb) {
  refreshSubscribers.push(cb);
}

// Token 刷新成功后，重新执行队列中的请求
function onTokenRefreshed(newToken) {
  refreshSubscribers.forEach((cb) => cb(newToken));
  refreshSubscribers = [];
}

// 1. 请求拦截器：每次请求都在 Header 里带上 Token
service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.accessToken) {
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
    if (res.code === undefined) {
      return res;
    }

    if (res.code === 1) {
      return res.data;
    } else {

      ElMessage({
        message: res.msg || "请求失败",
        type: "error",
        duration: 5 * 1000,
      });
      return Promise.reject(new Error(res.msg || "Error"));
    }
  },
  (error) => {
    const originalRequest = error.config;

    // 处理 HTTP 状态码错误
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      const authStore = useAuthStore();

      // 如果有 refreshToken，尝试刷新
      if (authStore.refreshToken) {
        originalRequest._retry = true;

        if (!isRefreshing) {
          isRefreshing = true;

          return new Promise((resolve, reject) => {
            axios.post('/admin/auth/refresh', {
              refreshToken: authStore.refreshToken
            })
              .then((res) => {
                const data = res.data;
                if (data && data.accessToken) {
                  // 更新 token
                  authStore.setTokens(data.accessToken, data.refreshToken || authStore.refreshToken);
                  onTokenRefreshed(data.accessToken);

                  // 重试原请求
                  originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
                  resolve(service(originalRequest));
                } else {
                  // 刷新失败，跳转登录
                  authStore.logout();
                  router.push("/login");
                  ElMessage.error("登录已过期，请重新登录");
                  reject(error);
                }
              })
              .catch((refreshError) => {
                // refreshToken 也过期了，跳转登录
                authStore.logout();
                router.push("/login");
                ElMessage.error("登录已过期，请重新登录");
                reject(refreshError);
              })
              .finally(() => {
                isRefreshing = false;
              });
          });
        }

        // 如果正在刷新中，将请求加入队列等待
        return new Promise((resolve) => {
          subscribeTokenRefresh((newToken) => {
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            resolve(service(originalRequest));
          });
        });
      }

      // 没有 refreshToken，直接跳转登录
      authStore.logout();
      router.push("/login");
      ElMessage.error("登录已过期，请重新登录");
    }
    return Promise.reject(error);
  }
);

export default service;
