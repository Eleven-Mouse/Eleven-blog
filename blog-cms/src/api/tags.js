import service from '@/utils/request';

/**
 * @description: 创建标签
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function createTag(data) {
  return service({
    url: '/admin/tags',
    method: 'post',
    data,
  });
}

/**
 * @description: 修改标签
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function updateTag(data) {
  return service({
    url: '/admin/tags',
    method: 'put',
    data,
  });
}

/**
 * @description: 根据id修改标签
 * @param {number | string} id 标签ID
 * @param {object} data 请求体
 * @returns {Promise}
 */
export function updateTagById(id, data) {
  return service({
    url: `/admin/tags/${id}`,
    method: 'put',
    data,
  });
}

/**
 * @description: 查询所有标签
 * @returns {Promise}
 */
export function getAllTags() {
  return service({
    url: '/admin/tags/list',
    method: 'get',
  });
}

/**
 * @description: 根据id删除标签
 * @param {number | string} id 标签ID
 * @returns {Promise}
 */
export function deleteTagById(id) {
  return service({
    url: `/admin/tags/${id}`,
    method: 'delete',
  });
}

