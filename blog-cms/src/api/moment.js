import request from "@/utils/request";

/**
 * @description: 创建动态
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function createMoment(data) {
  return request({
    url: "/moment",
    method: "post",
    data,
  });
}

/**
 * @description: 查询动态列表
 * @param {object} params 查询参数
 * @returns {Promise}
 */
export function getMomentList(params) {
  return request({
    url: "/moment/list",
    method: "get",
    params,
  });
}

/**
 * @description: 根据ID查询动态
 * @param {number | string} id 动态ID
 * @returns {Promise}
 */
export function getMomentById(id) {
  return request({
    url: `/moment/${id}`,
    method: "get",
  });
}

/**
 * @description: 根据ID删除动态
 * @param {number | string} id 动态ID
 * @returns {Promise}
 */
export function deleteMomentById(id) {
  return request({
    url: `/moment/${id}`,
    method: "delete",
  });
}

/**
 * @description: 根据ID更新动态
 * @param {number | string} id 动态ID
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function updateMomentById(id, data) {
  return request({
    url: `/moment/${id}`,
    method: "put",
    data,
  });
}
