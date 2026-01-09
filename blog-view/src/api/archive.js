import request from '@/utils/request';

/**
 * 获取归档数据
 */
export function fetchArchive() {
  return request({
    url: '/archive',
    method: 'get',
  });
}


