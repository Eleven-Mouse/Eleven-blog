import request from '@/utils/request'

/**
 * @description: 查询友链列表
 * @returns {Promise}
 */
export function getFriendLinks() {
  return request({
    url: '/friendlinks',
    method: 'get',
  })
}
