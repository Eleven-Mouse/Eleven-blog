import request from "@/utils/request";

/**
 * @description: 上传图片
 * @param {FormData} formData FormData object containing the file
 * @returns {Promise}
 */
export function uploadImage(formData) {
  return request({
    url: "/upload/images",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

