import service from '@/utils/request';

/**
 * @description: 上传图片
 * @param {FormData} formData FormData object containing the file
 * @returns {Promise}
 */
export function uploadImage(formData) {
  return service({
    url: '/admin/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

/**
 * @description: 上传Markdown文章
 * @param {FormData} formData FormData object containing the file
 * @returns {Promise}
 */
export function uploadMarkdown(formData) {
  return service({
    url: '/admin/upload/markdown',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

/**
 * @description: 获取图片列表
 * @returns {Promise}
 */
export function getImageList() {
  return service({
    url: '/admin/upload/images',
    method: 'get',
  });
}

/**
 * @description: 删除图片
 * @param {string} filename 文件名
 * @returns {Promise}
 */
export function deleteImage(filename) {
  return service({
    url: `/admin/upload/images/${filename}`,
    method: 'delete',
  });
}

