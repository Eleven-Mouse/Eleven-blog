import request from '@/utils/request'

/**
 * 获取评论列表
 * @param {string} page 页面
 * @param {Long} blogId，博客文章
 */
// 查询评论列表，queryParams 支持 blogId、page(pageName) 或分页参数等
export function fetchComments(queryParams) {
  return request({
    url: '/comments',
    method: 'get',
    params: queryParams,
  })
}

/**
 * 提交评论
 * @param {object} commentData - 评论数据
 */
export function createComment(commentData) {
  return request({
    url: '/comments/comment',
    method: 'post',
    data: commentData,
  })
}


