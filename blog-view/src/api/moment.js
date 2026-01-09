import request from '@/utils/request'

/**
 * 获取动态列表
 */
export function getMoments(params) {
  return request({
    url: '/moments',
    method: 'get',
    params,
  })
}
