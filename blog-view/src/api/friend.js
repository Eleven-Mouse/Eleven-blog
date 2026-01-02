import request from '@/utils/request'

/**
 * 获取友链列表
 */
export function fetchFriends() {
  return request({
    url: '/api/friendlinks',
    method: 'get',
  })
}
