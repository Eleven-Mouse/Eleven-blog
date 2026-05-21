import request from '@/utils/request'

/**
 * 静默触发后端自动发现同步（前端不做提示）
 */
export function triggerSilentGithubSync() {
  return request({
    url: '/blog/sync/silent',
    method: 'get',
    silent: true,
  })
}

