import request from '@/utils/request';
import {
  getStaticArticleList,
  getStaticCategories,
  getStaticCategoryById,
  withContentSource,
} from '@/content/siteContent'

/**
 * 获取分类列表
 */
export function fetchCategories() {
  return withContentSource(
    (site) => getStaticCategories(site),
    () =>
      request({
        url: '/categories',
        method: 'get',
      }),
  )
}

/**
 * 获取分类详情
 * @param {number} id - 分类ID
 */
export function fetchCategoryById(id) {
  return withContentSource(
    (site) => getStaticCategoryById(site, id),
    () =>
      request({
        url: `/categories/${id}`,
        method: 'get',
      }),
  )
}


/**
 * 根据分类ID获取文章列表
 * @param {number} id - 分类ID
 * @param {object} params - 查询参数（如分页）
 */
export function fetchArticlesByCategoryId(id, params) {
  const mergedParams = {
    ...(params || {}),
    categoryId: id,
  };
  return withContentSource(
    (site) => getStaticArticleList(site, mergedParams),
    () =>
      request({
        url: '/articles',
        method: 'get',
        params: mergedParams,
      }),
  )
}
