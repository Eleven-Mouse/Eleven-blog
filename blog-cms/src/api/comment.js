import service from '@/utils/request';

/**
 * @description: 更新评论公开状态
 * @param {object} params { id: number, status: boolean }
 * @returns {Promise}
 */
export function updateCommentStatus(params) {
  return service({
    url: '/admin/comments/status',
    method: 'put',
    params,
  });
}

/**
 * @description: 更新评论邮件提醒状态
 * @param {object} params { id: number, notice: boolean }
 * @returns {Promise}
 */
export function updateCommentNotice(params) {
  return service({
    url: '/admin/comments/notice',
    method: 'put',
    params,
  });
}

/**
 * @description: 删除评论(所有子评论)
 * @param {number | string} id 评论ID
 * @returns {Promise}
 */
export function deleteCommentById(id) {
  return service({
    url: `/admin/comments/${id}`,
    method: 'delete',
  });
}

/**
 * @description: 根据页面或文章id查询所有评论
 * @param {object} params { page?: string, blogId?: number }
 * @returns {Promise}
 */
export function getComments(params) {
  return service({
    url: '/admin/comments',
    method: 'get',
    params,
  });
}



