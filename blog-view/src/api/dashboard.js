import request from "@/utils/request";

/**
 * 获取仪表盘统计数据
 * @returns {Promise}
 */
export function getStatistics() {
  return request({
    url: "/dashboard/statistics",
    method: "get",
  });
}

export function getContributionData() {
  return request({
    url: "/dashboard/statistics/contribution",
    method: "get",
  });
}
export function getCategoryStatistics() {
  return request({
    url: "/dashboard/statistics/category", // 对应你 Controller 的路径
    method: "get",
  });
}
export function getTagStatistics() {
  return request({
    url: "/dashboard/statistics/tag",
    method: "get",
  });
}
