import request from "@/utils/request";

/**
 * @description: 创建标签
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function createTag(data) {
  return request({
    url: "/tags",
    method: "post",
    data,
  });
}

/**
 * @description: 修改标签
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function updateTag(data) {
  return request({
    url: "/tags",
    method: "put",
    data,
  });
}



/**
 * @description: 查询所有标签
 * @returns {Promise}
 */
export function getAllTags() {
  return request({
    url: "/tags/list",
    method: "get",
  });
}

/**
 * @description: 根据id删除标签
 * @param {number | string} id 标签ID
 * @returns {Promise}
 */
export function deleteTagById(id) {
  return request({
    url: `/tags/${id}`,
    method: "delete",
  });
}
