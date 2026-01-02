import request from '@/utils/request'

/**
 * 获取文章列表（支持分页和筛选）
 * @param {object} params - 查询参数
 * @param {number} [params.page] - 当前页码
 * @param {number} [params.size] - 每页数量
 * @param {number} [params.categoryId] - 分类ID
 * @param {string} [params.category] - 分类名
 * @param {string} [params.keyword] - 关键词
 * @param {string} [params.sortBy] - 排序字段
 * @param {string} [params.order] - 排序方式
 */
export function fetchArticles(params) {
  return request({
    url: '/api/articles',
    method: 'get',
    params,
  })
}

/**
 * 根据ID获取文章详情
 * @param {number} id - 文章ID
 */
export function fetchArticleById(id) {
  return request({
    url: `/api/articles/${id}`,
    method: 'get',
  })
}

/**
 * 根据分类和slug获取文章详情
 * @param {string} category - 分类名
 * @param {string} slug - 文章slug
 */
export function fetchArticleBySlug(category, slug) {
  return request({
    url: `/api/articles/${category}/${slug}`,
    method: 'get',
  })
}

/**
 * 获取随机文章
 * @param {object} params - 查询参数
 * @param {number} params.limit - 获取数量
 */
export function fetchRandomArticles(params) {
  return request({
    url: '/api/articles/random',
    method: 'get',
    params,
  })
}

/**
 * 增加文章浏览量
 * @param {number} id - 文章ID
 */
export function incrementArticleView(id) {
  return request({
    url: `/api/article/${id}/view`,
    method: 'post',
  })
}

/**
 * 创建文章 (根据文档，这是一个通用的POST接口)
 * @param {object} data - 文章数据
 */
export function createArticle(data) {
  return request({
    url: '/api/articles',
    method: 'post',
    data,
  })
}
