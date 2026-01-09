import request from "@/utils/request";

/**
 * @description: 创建友链
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function createFriendLink(data) {
  return request({
    url: "/friendlinks",
    method: "post",
    data,
  });
}

/**
 * @description: 更新友链
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function updateFriendLink(data) {
  return request({
    url: `/friendlinks`,
    method: "put",
    data,
  });
}

/**
 * @description: 根据id删除友链
 * @param {number | string} id 友链ID
 * @returns {Promise}
 */
export function deleteFriendLinkById(id) {
  return request({
    url: `/friendlinks/${id}`,
    method: "delete",
  });
}


/**
 * @description: 查询友链列表
 * @param {object} params 查询参数
 * @returns {Promise}
 */
export function getFriendLinks(params) {
  return request({
    url: "/friendlinks",
    method: "get",
    params,
  });
}
