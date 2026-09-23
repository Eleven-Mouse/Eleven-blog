import { ElMessage } from 'element-plus'

const BASE_URL = import.meta.env.VITE_APP_API_URL
const TIMEOUT = 20000

// 参数序列化为 query string（等价于 axios 的 params 行为）
const buildQueryString = (params) => {
  if (!params) return ''
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) search.append(key, String(value))
  })
  const qs = search.toString()
  return qs ? `?${qs}` : ''
}

// 基于 fetch 的请求实现，保持原 axios 实例的调用接口不变：
// request({ url, method, params, data, silent })
const request = async (config) => {
  const { url, method = 'get', params, data, silent = false } = config
  const fullUrl = `${BASE_URL}${url}${buildQueryString(params)}`

  // 超时控制（等价于 axios 的 timeout）
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), TIMEOUT)

  const handleFailure = (error, { status = 0, respMsg = '' } = {}) => {
    // 已知公开接口若被后端误拦截为 401，前端静默降级处理
    if (status === 401 && (url.includes('/comments') || url.includes('/blog/config') || url.includes('/blog/sync/silent'))) {
      return Promise.reject(error)
    }
    if (silent) {
      return Promise.reject(error)
    }
    console.error('Network Error:', error)
    ElMessage({
      message: respMsg || error.message || '网络错误，请检查您的连接',
      type: 'error',
      duration: 5 * 1000,
    })
    return Promise.reject(error)
  }

  try {
    const response = await fetch(fullUrl, {
      method: method.toUpperCase(),
      headers: data !== undefined ? { 'Content-Type': 'application/json' } : undefined,
      body: data !== undefined ? JSON.stringify(data) : undefined,
      signal: controller.signal,
    })

    // HTTP 状态错误（4xx/5xx）
    if (!response.ok) {
      let respMsg = ''
      try {
        const errBody = await response.json()
        respMsg = errBody?.msg || errBody?.message || ''
      } catch {
        // 响应体不是 JSON 时忽略
      }
      throw Object.assign(new Error(`HTTP ${response.status}`), {
        isHttpError: true,
        status: response.status,
        respMsg,
      })
    }

    const res = await response.json()

    // 检查响应是否是我们期望的 Result DTO 格式
    // 如果 code 字段不存在，我们假定这是一个成功的请求，且响应体就是原始数据
    if (res.code === undefined) {
      return res
    }

    // 如果是 Result DTO 格式，我们根据 code 的值来判断
    if (res.code === 1) {
      return res.data // code 为 1 表示成功，直接返回 data 部分
    }

    // 处理已知的错误情况 (code 不为 1)
    ElMessage({
      message: res.msg || '请求失败',
      type: 'error',
      duration: 5 * 1000,
    })
    return Promise.reject(new Error(res.msg || 'Error'))
  } catch (error) {
    // 超时自动重试一次（等价于 axios 的 ECONNABORTED 重试）
    if (error.name === 'AbortError' && config && !config._retry) {
      config._retry = true
      return request(config)
    }
    return handleFailure(error, {
      status: error.status || 0,
      respMsg: error.respMsg || '',
    })
  } finally {
    clearTimeout(timer)
  }
}

export default request
