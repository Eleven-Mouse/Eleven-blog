import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";

const BASE_URL = import.meta.env.VITE_APP_UPLOAD_URL;

export function formatTime(datetime) {
  if (!datetime) return "--";
  const date = new Date(datetime);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

export function formatDateTime(datetime) {
  if (!datetime) return "--";
  const date = new Date(datetime);
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function parseImages(imageStr) {
  if (!imageStr) return [];
  return imageStr.split(",").map((path) =>
    path.startsWith("http") ? path : BASE_URL + path
  );
}

export function formatTags(tags) {
  if (!tags) return [];
  if (Array.isArray(tags)) return tags;
  if (typeof tags === "string") {
    return tags
      .split(",")
      .map((t) => t.trim())
      .filter((t) => t);
  }
  return [];
}

export function useFormat() {
  return { formatTime, formatDateTime, parseImages, formatTags };
}
