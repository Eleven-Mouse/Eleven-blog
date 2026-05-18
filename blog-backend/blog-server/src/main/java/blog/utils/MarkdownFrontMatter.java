package blog.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown Front Matter (YAML) 解析器
 * 解析文件顶部的 --- 包裹的元数据块
 *
 * 示例输入：
 * ---
 * title: Spring Boot 入门
 * category: Java
 * tags: Spring Boot,后端
 * summary: 这是一篇教程
 * ---
 * # 正文
 *
 * 输出：{title: "Spring Boot 入门", category: "Java", tags: ["Spring Boot", "后端"], summary: "这是一篇教程"}
 */
public class MarkdownFrontMatter {

    private static final Pattern FRONT_MATTER_PATTERN = Pattern.compile(
            "^---\\s*\\n(.*?)\\n---\\s*\\n?", Pattern.DOTALL
    );

    private String title;
    private String category;
    private List<String> tags;
    private String summary;
    private Integer chapterOrder;
    private Integer readingMinutes;
    private Integer isCore;
    private Boolean homeFeatured;
    /** Front Matter 之后的正文内容（去掉了头部元数据） */
    private String bodyContent;

    public String getTitle() { return title; }
    public String getCategory() { return category; }

    public Integer getChapterOrder() { return chapterOrder; }
    public Integer getReadingMinutes() { return readingMinutes; }
    public Integer getIsCore() { return isCore; }
    public Boolean getHomeFeatured() { return homeFeatured; }

    /**
     * 从 Markdown 文本中解析 Front Matter
     *
     * @param markdown 完整的 Markdown 文本
     * @return 解析结果，如果没有 Front Matter 则各字段为 null
     */
    public static MarkdownFrontMatter parse(String markdown) {
        MarkdownFrontMatter fm = new MarkdownFrontMatter();
        if (markdown == null || markdown.isEmpty()) {
            fm.bodyContent = markdown;
            return fm;
        }

        Matcher matcher = FRONT_MATTER_PATTERN.matcher(markdown);
        if (!matcher.find()) {
            fm.bodyContent = markdown;
            return fm;
        }

        String yamlBlock = matcher.group(1);
        fm.bodyContent = markdown.substring(matcher.end());

        // 解析简单的 key: value 行（不支持嵌套）
        Map<String, String> kv = new LinkedHashMap<>();
        for (String line : yamlBlock.split("\\n")) {
            int colonIdx = line.indexOf(':');
            if (colonIdx > 0) {
                String key = line.substring(0, colonIdx).trim().toLowerCase();
                String value = line.substring(colonIdx + 1).trim();
                kv.put(key, value);
            }
        }

        fm.title = kv.get("title");
        fm.category = kv.get("category");
        fm.summary = kv.get("summary");
        fm.chapterOrder = parseInteger(kv.get("chapter_order"));
        if (fm.chapterOrder == null) {
            fm.chapterOrder = parseInteger(kv.get("chapterOrder"));
        }
        fm.readingMinutes = parseInteger(kv.get("reading_minutes"));
        if (fm.readingMinutes == null) {
            fm.readingMinutes = parseInteger(kv.get("readingMinutes"));
        }
        fm.isCore = parseBooleanToInt(kv.get("is_core"));
        if (fm.isCore == null) {
            fm.isCore = parseBooleanToInt(kv.get("isCore"));
        }
        fm.homeFeatured = parseBoolean(kv.get("home_featured"));
        if (fm.homeFeatured == null) {
            fm.homeFeatured = parseBoolean(kv.get("homeFeatured"));
        }

        String tagsStr = kv.get("tags");
        if (tagsStr != null && !tagsStr.isEmpty()) {
            // 支持逗号分隔：tags: Spring Boot,后端
            // 也支持中文逗号：tags: Spring Boot，后端
            fm.tags = Arrays.stream(tagsStr.split("[,，]"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        return fm;
    }

    private static Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if ("true".equals(normalized) || "1".equals(normalized) || "yes".equals(normalized) || "y".equals(normalized)) {
            return true;
        }
        if ("false".equals(normalized) || "0".equals(normalized) || "no".equals(normalized) || "n".equals(normalized)) {
            return false;
        }
        return null;
    }

    private static Integer parseBooleanToInt(String value) {
        Boolean bool = parseBoolean(value);
        if (bool == null) {
            return null;
        }
        return bool ? 1 : 0;
    }

    /**
     * 从文件路径中提取第一级目录名作为分类（专题）
     * 如 "network/tcp/intro.md" → "network"
     * 如 "database/mysql/index.md" → "database"
     * 如 "single.md" → null
     */
    public static String inferCategoryFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        int firstSlash = path.indexOf('/');
        if (firstSlash <= 0) {
            return null;
        }
        String folderName = path.substring(0, firstSlash).trim();
        return folderName.isEmpty() ? null : folderName;
    }

    public String getSummary()
    {
        return summary;
    }
}
