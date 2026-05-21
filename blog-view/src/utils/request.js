import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_API_URL,
  timeout: 20000,
})

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data

    // 检查响应是否是我们期望的 Result DTO 格式
    // 如果 code 字段不存在，我们假定这是一个成功的请求，且响应体就是原始数据
    if (res.code === undefined) {
      return res
    }

    // 如果是 Result DTO 格式，我们根据 code 的值来判断
    if (res.code === 1) {
      return res.data // code 为 1 表示成功，直接返回 data 部分
    } else {
      // 处理已知的错误情况 (code 不为 1)
      ElMessage({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 5 * 1000,
      })
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  (error) => {
    const config = error.config
    const silent = Boolean(config?.silent)
    const status = error?.response?.status
    const url = String(config?.url || '')

    // 已知公开接口若被后端误拦截为 401，前端静默降级处理
    if (status === 401 && (url.includes('/comments') || url.includes('/blog/config'))) {
      return Promise.reject(error)
    }

    // 超时自动重试一次
    if (error.code === 'ECONNABORTED' && config && !config._retry) {
      config._retry = true
      return service(config)
    }
    if (silent) {
      return Promise.reject(error)
    }
    console.error('Network Error:', error)
    ElMessage({
      message: error.message || '网络错误，请检查您的连接',
      type: 'error',
      duration: 5 * 1000,
    })
    return Promise.reject(error)
  },
)

export default service
