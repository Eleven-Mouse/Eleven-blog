import request from '@/utils/request'
import { getStaticBlogConfig, withContentSource } from '@/content/siteContent'

/**
 * 获取博客配置
 */
export function fetchBlogConfig() {
  return withContentSource(
    (site) => getStaticBlogConfig(site),
    () =>
      request({
        url: '/blog/config',
        method: 'get',
      }),
  )
}
