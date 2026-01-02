import request from '@/utils/request';

/**
 * 获取动态列表
 */
export function fetchMoments() {
  return request({
    url: '/api/moments',
    method: 'get',
  });
}

/**
 * 创建动态
 * @param {object} data - 动态数据
 */
export function createMoment(data) {
  return request({
    url: '/api/moment',
    method: 'post',
    data,
  });
}


