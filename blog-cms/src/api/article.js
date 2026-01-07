import request from "@/utils/request";

/**
 * @description: 创建文章
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function createArticle(data) {
  return request({
    url: "/articles",
    method: "post",
    data,
  });
}

/**
 * @description: 更新文章
 * @param {number | string} id 文章ID
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function updateArticle(id, data) {
  return request({
    url: `/articles/${id}`,
    method: "put",
    data,
  });
}

/**
 * @description: 查询所有文章（简单查询，用于下拉列表等）
 * @returns {Promise}
 */
export function getAllArticles() {
  return request({
    url: "/articles",
    method: "get",
  });
}

/**
 * @description: 查询文章列表（带条件查询）
 * @param {object} params 查询参数
 * @returns {Promise}
 */
export function getArticlesList(params) {
  return request({
    url: "/articles/list",
    method: "get",
    params,
  });
}

/**
 * @description: 获取最近文章
 * @param {object} params 查询参数 { limit: number }
 * @returns {Promise}
 */
export function getRecentArticles(params) {
  return request({
    url: "/articles/recent",
    method: "get",
    params,
  });
}

/**
 * @description: 根据ID查询文章详情
 * @param {number | string} id 文章ID
 * @returns {Promise}
 */
export function getArticleById(id) {
  return request({
    url: `/articles/${id}`,
    method: "get",
  });
}

/**
 * @description: 删除文章
 * @param {number | string} id 文章ID
 * @returns {Promise}
 */
export function deleteArticleById(id) {
  return request({
    url: `/articles/${id}`,
    method: "delete",
  });
}
