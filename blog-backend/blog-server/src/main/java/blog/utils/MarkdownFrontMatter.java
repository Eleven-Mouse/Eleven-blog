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
    /** Front Matter 之后的正文内容（去掉了头部元数据） */
    private String bodyContent;

    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public List<String> getTags() { return tags != null ? tags : Collections.emptyList(); }
    public String getSummary() { return summary; }
    public String getBodyContent() { return bodyContent; }

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

    /**
     * 从文件路径中提取上级文件夹名作为分类
     * 如 "Part1-Java基础/Spring Boot入门.md" → "Java基础"
     * 如 "Java/Spring Boot入门.md" → "Java"
     * 如 "Spring Boot入门.md" → null
     * 自动去除 "Part1-"、"01-"、"1." 等编号前缀
     */
    public static String inferCategoryFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return null;
        }
        String parent = path.substring(0, lastSlash);
        int prevSlash = parent.lastIndexOf('/');
        String folderName = prevSlash >= 0 ? parent.substring(prevSlash + 1) : parent;
        if (folderName.isEmpty()) {
            return null;
        }
        // 去除常见编号前缀：Part1-、Part01-、01-、1.、1- 等
        folderName = folderName.replaceFirst("^(?i)Part\\d+[-_\\s]*", "");
        folderName = folderName.replaceFirst("^\\d+[-_.\\s]+", "");
        return folderName.isEmpty() ? null : folderName;
    }
}
