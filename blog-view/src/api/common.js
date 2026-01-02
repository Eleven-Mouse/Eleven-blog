import request from '@/utils/request';

/**
 * API健康检查
 */
export function healthCheck() {
  return request({
    url: '/api/health',
    method: 'get',
  });
}


