import request from "@/utils/request";

/**
 * @description: 创建分类
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function createCategory(data) {
  return request({
    url: "/categories",
    method: "post",
    data,
  });
}

/**
 * @description: 修改分类
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function updateCategory(data) {
  return request({
    url: "/categories",
    method: "put",
    data,
  });
}



/**
 * @description: 查询所有分类
 * @returns {Promise}
 */
export function getAllCategories() {
  return request({
    url: "/categories",
    method: "get",
  });
}

/**
 * @description: 根据id删除分类
 * @param {number | string} id 分类ID
 * @returns {Promise}
 */
export function deleteCategoryById(id) {
  return request({
    url: `/categories/${id}`,
    method: "delete",
  });
}
