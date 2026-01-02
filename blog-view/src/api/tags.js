import request from '@/utils/request';

/**
 * 获取标签列表
 */
export function fetchTags() {
  return request({
    url: '/api/tags',
    method: 'get',
  });
}

/**
 * 创建标签
 * @param {object} data - 标签数据
 */
export function createTag(data) {
  return request({
    url: '/api/tags',
    method: 'post',
    data,
  });
}

/**
 * 根据标签ID获取文章列表
 * @param {number} id - 标签ID
 * @param {object} params - 查询参数（如分页）
 */
export function fetchArticlesByTagId(id, params) {
  return request({
    url: `/api/tags/${id}/articles`,
    method: 'get',
    params,
  });
}


