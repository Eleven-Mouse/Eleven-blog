import request from "@/utils/request";

/**
 * 获取系统配置
 */
export function getBlogConfig() {
  return request({
    url: "/system/config",
    method: "get",
  });
}

/**
 * 更新系统配置
 * @param {object} data key-value 配置映射
 */
export function updateBlogConfig(data) {
  return request({
    url: "/system/config",
    method: "put",
    data,
  });
}
