import request from '@/utils/request';

/**
 * 获取分类列表
 */
export function fetchCategories() {
  return request({
    url: '/categories',
    method: 'get',
  });
}


/**
 * 根据分类ID获取文章列表
 * @param {number} id - 分类ID
 * @param {object} params - 查询参数（如分页）
 */
export function fetchArticlesByCategoryId(id, params) {
  return request({
    url: `/categories/${id}/articles`,
    method: 'get',
    params,
  });
}


