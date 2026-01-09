import request from "@/utils/request";



/**
 * @description: 删除评论(所有子评论)
 * @param {number | string} id 评论ID
 * @returns {Promise}
 */
export function deleteCommentById(id) {
  return request({
    url: `/comments/${id}`,
    method: "delete",
  });
}

/**
 * @description: 根据页面或文章id查询所有评论
 * @param {object} params { page?: string, blogId?: number }
 * @returns {Promise}
 */
export function getComments(params) {
  return request({
    url: "/comments",
    method: "get",
    params,
  });
}
