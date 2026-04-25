import request from '@/utils/request'

/**
 * 获取博客配置
 */
export function fetchBlogConfig() {
  return request({
    url: '/blog/config',
    method: 'get',
  })
}
