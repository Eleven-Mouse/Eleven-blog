import request from '@/utils/request';
import { getStaticArchive, withContentSource } from '@/content/siteContent'

/**
 * 获取归档数据
 */
export function fetchArchive() {
  return withContentSource(
    (site) => getStaticArchive(site),
    () =>
      request({
        url: '/archive',
        method: 'get',
      }),
  )
}

