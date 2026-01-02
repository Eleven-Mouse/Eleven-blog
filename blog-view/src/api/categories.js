import request from '@/utils/request';

/**
 * 获取分类列表
 */
export function fetchCategories() {
  return request({
    url: '/api/categories',
    method: 'get',
  });
}

/**
 * 创建分类
 * @param {object} data - 分类数据
 */
export function createCategory(data) {
  return request({
    url: '/api/categories',
    method: 'post',
    data,
  });
}

/**
 * 根据ID获取分类详情
 * @param {number} id - 分类ID
 */
export function fetchCategoryById(id) {
  return request({
    url: `/api/categories/${id}`,
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
    url: `/api/categories/${id}/articles`,
    method: 'get',
    params,
  });
}


