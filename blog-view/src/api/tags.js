import request from '@/utils/request';
import {
  getStaticArticlesByTagId,
  getStaticTags,
  withContentSource,
} from '@/content/siteContent'

/**
 * 获取标签列表
 */
export function fetchTags() {
  return withContentSource(
    (site) => getStaticTags(site),
    () =>
      request({
        url: '/tags',
        method: 'get',
      }),
  )
}


/**
 * 根据标签ID获取文章列表
 * @param {number} id - 标签ID
 * @param {object} params - 查询参数（如分页）
 */
export function fetchArticlesByTagId(id, params) {
  return withContentSource(
    (site) => getStaticArticlesByTagId(site, id, params),
    () =>
      request({
        url: `/tags/${id}/articles`,
        method: 'get',
        params,
      }),
  )
}

