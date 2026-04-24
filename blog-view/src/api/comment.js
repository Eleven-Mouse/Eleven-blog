import request from '@/utils/request'

/**
 * 获取评论列表
 * @param {string} page 页面
 * @param {Long} blogId，博客文章
 */
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

/**
 * 评论点赞
 * @param {Long} id 评论ID
 */
export function likeComment(id) {
  return request({
    url: `/comments/like/${id}`,
    method: 'post',
  })
}

/**
 * 获取 GitHub 登录跳转 URL
 */
export function getGitHubLoginUrl() {
  return request({
    url: '/oauth/github/url',
    method: 'get',
  })
}
