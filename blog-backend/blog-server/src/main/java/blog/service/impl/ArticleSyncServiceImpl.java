package blog.service.impl;

import blog.dto.ArticleDTO;
import blog.entity.Article;
import blog.entity.Category;
import blog.entity.Tags;
import blog.mapper.ArticleMapper;
import blog.mapper.CategoryMapper;
import blog.mapper.TagsMapper;
import blog.service.ArticleSyncService;
import blog.service.GithubMarkdownFetcher;
import blog.service.GithubRepoScanner;
import blog.service.SystemConfigService;
import blog.utils.MarkdownFrontMatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章同步服务实现
 * 核心能力：
 * 1. 按 github_url 同步单篇/全部文章
 * 2. 自动发现：扫描仓库 → 解析 Front Matter → 匹配分类/标签 → 创建或更新文章
 * 匹配优先级：
 *   - 分类：Front Matter category > 文件夹名 > 不设置
 *   - 标签：Front Matter tags > 不设置
 *   - 标题：Front Matter title > 文件名（去.md）
 */
@Service
@Slf4j
public class ArticleSyncServiceImpl implements ArticleSyncService {

    private static final String CONFIG_KEY_REPO = "github_sync_repo";
    private static final String CONFIG_KEY_OWNER = "github_sync_owner";
    private static final String CONFIG_KEY_BRANCH = "github_sync_branch";
    private static final String CONFIG_KEY_PATH = "github_sync_path";

    private final GithubMarkdownFetcher fetcher;
    private final GithubRepoScanner scanner;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagsMapper tagsMapper;
    private final SystemConfigService configService;

    public ArticleSyncServiceImpl(GithubMarkdownFetcher fetcher,
                                  GithubRepoScanner scanner,
                                  ArticleMapper articleMapper,
                                  CategoryMapper categoryMapper,
                                  TagsMapper tagsMapper,
                                  SystemConfigService configService) {
        this.fetcher = fetcher;
        this.scanner = scanner;
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagsMapper = tagsMapper;
        this.configService = configService;
    }

    // ==================== 同步（原有逻辑，未改动） ====================

    @Override
    public boolean syncArticle(Long articleId) {
        var dto = articleMapper.selectById(articleId);
        if (dto == null) {
            log.warn("同步失败：文章不存在，ID={}", articleId);
            return false;
        }
        if (dto.getGithubUrl() == null || dto.getGithubUrl().isBlank()) {
            log.warn("同步跳过：文章未配置 github_url，ID={}", articleId);
            return false;
        }

        String markdown = fetcher.fetchMarkdown(dto.getGithubUrl());
        if (markdown == null || markdown.isBlank()) {
            markSyncFailed(articleId);
            return false;
        }

        if (markdown.equals(dto.getContent())) {
            log.info("同步跳过：内容未变化，ID={}", articleId);
            markSyncSuccess(articleId);
            return true;
        }

        Article updateEntity = new Article();
        updateEntity.setId(articleId);
        updateEntity.setContent(markdown);
        updateEntity.setSummary(generateSummary(markdown, 200));
        updateEntity.setSyncStatus(1);
        updateEntity.setLastSyncTime(LocalDateTime.now());
        articleMapper.update(updateEntity);

        log.info("同步成功：文章ID={}，内容长度={}", articleId, markdown.length());
        return true;
    }

    @Override
    public int syncAllArticles() {
        List<Article> candidates = articleMapper.selectSyncCandidates();
        if (candidates.isEmpty()) {
            log.info("没有需要同步的文章");
            return 0;
        }

        log.info("开始批量同步，共{}篇文章", candidates.size());
        int successCount = 0;
        for (Article article : candidates) {
            try {
                if (syncArticle(article.getId())) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("同步异常：文章ID={}，原因: {}", article.getId(), e.getMessage());
                markSyncFailed(article.getId());
            }
        }
        log.info("批量同步完成：成功{}/{}", successCount, candidates.size());
        return successCount;
    }

    // ==================== 自动发现 ====================

    @Override
    public Map<String, Integer> autoDiscover() {
        Map<String, String> config = configService.getAllConfigAsMap();
        String owner = config.get(CONFIG_KEY_OWNER);
        String repo = config.get(CONFIG_KEY_REPO);
        String branch = config.getOrDefault(CONFIG_KEY_BRANCH, "main");
        String path = config.getOrDefault(CONFIG_KEY_PATH, "");

        if (owner == null || owner.isBlank() || repo == null || repo.isBlank()) {
            log.warn("自动发现未配置：请设置 github_sync_owner 和 github_sync_repo");
            return emptyResult();
        }

        log.info("开始自动发现：{}/{} branch={} path={}", owner, repo, branch, path);
        List<GithubRepoScanner.MdFileInfo> files = scanner.scanMarkdownFiles(owner, repo, branch, path);
        log.info("扫描到{}个 Markdown 文件", files.size());

        int matched = 0;
        int created = 0;
        int failed = 0;

        for (GithubRepoScanner.MdFileInfo file : files) {
            try {
                // SHA 增量比对：SHA 不变则跳过拉取
                if (file.getSha() != null) {
                    ArticleDTO existingByTitle = articleMapper.selectByTitle(
                            file.getTitle() != null ? file.getTitle() : file.getName().replace(".md", ""));
                    if (existingByTitle != null && file.getSha().equals(existingByTitle.getGithubSha())) {
                        log.info("SHA 未变化，跳过：'{}'", file.getName());
                        continue;
                    }
                }

                // 拉取 Markdown 内容
                String rawMarkdown = fetcher.fetchMarkdown(file.getDownloadUrl());
                if (rawMarkdown == null || rawMarkdown.isBlank()) {
                    failed++;
                    log.warn("自动发现-跳过：无法拉取 '{}'", file.getName());
                    continue;
                }

                // 解析 Front Matter
                MarkdownFrontMatter fm = MarkdownFrontMatter.parse(rawMarkdown);

                // 确定标题：Front Matter title > 文件名
                String title = fm.getTitle() != null ? fm.getTitle() : file.getTitle();

                // 按标题匹配已有文章
                ArticleDTO existing = articleMapper.selectByTitle(title);

                if (existing != null) {
                    updateExistingArticle(existing, file.getDownloadUrl(), fm, file.getPath(), file.getSha());
                    syncArticle(existing.getId());
                    matched++;
                    log.info("自动发现-匹配：'{}' → 文章ID={}", title, existing.getId());
                } else {
                    createArticleFromGithub(title, file.getDownloadUrl(), fm, rawMarkdown, file.getPath(), file.getSha());
                    created++;
                    log.info("自动发现-新建：'{}'", title);
                }
            } catch (Exception e) {
                failed++;
                log.error("自动发现异常：{}，原因: {}", file.getName(), e.getMessage());
            }
        }

        log.info("自动发现完成：匹配={}，新建={}，失败={}", matched, created, failed);
        Map<String, Integer> result = new HashMap<>();
        result.put("matched", matched);
        result.put("created", created);
        result.put("failed", failed);
        return result;
    }

    @Override
    public List<GithubRepoScanner.MdFileInfo> previewDiscover() {
        Map<String, String> config = configService.getAllConfigAsMap();
        String owner = config.get(CONFIG_KEY_OWNER);
        String repo = config.get(CONFIG_KEY_REPO);
        String branch = config.getOrDefault(CONFIG_KEY_BRANCH, "main");
        String path = config.getOrDefault(CONFIG_KEY_PATH, "");

        if (owner == null || owner.isBlank() || repo == null || repo.isBlank()) {
            return List.of();
        }
        return scanner.scanMarkdownFiles(owner, repo, branch, path);
    }

    // ==================== 分类/标签解析 ====================

    /**
     * 解析分类：Front Matter category > 文件夹名
     * 匹配数据库分类（按名称），找不到则自动创建
     */
    private Long resolveCategoryId(MarkdownFrontMatter fm, String filePath) {
        String categoryName = fm.getCategory();
        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = MarkdownFrontMatter.inferCategoryFromPath(filePath);
        }
        if (categoryName == null) {
            return null;
        }

        Category category = categoryMapper.selectByName(categoryName);
        if (category != null) {
            log.info("分类匹配：'{}' → ID={}", categoryName, category.getId());
            return category.getId();
        }

        // 自动创建不存在的分类
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory.setCreateTime(LocalDateTime.now());
        newCategory.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(newCategory);
        log.info("自动创建分类：'{}' → ID={}", categoryName, newCategory.getId());
        return newCategory.getId();
    }

    /**
     * 解析标签：从 Front Matter tags 列表
     * 匹配数据库标签（按名称），找不到的自动创建
     * 返回逗号分隔的标签 ID 字符串
     */
    private String resolveTagIds(MarkdownFrontMatter fm) {
        List<String> tagNames = fm.getTags();
        if (tagNames.isEmpty()) {
            return null;
        }

        // 先查已有的
        List<Tags> existingTags = tagsMapper.selectByNames(tagNames);
        Map<String, Long> nameToId = existingTags.stream()
                .collect(Collectors.toMap(t -> t.getName().toLowerCase(), Tags::getId));

        List<Long> tagIds = new ArrayList<>();
        for (String name : tagNames) {
            Long id = nameToId.get(name.toLowerCase());
            if (id != null) {
                tagIds.add(id);
            } else {
                Tags newTag = new Tags();
                newTag.setName(name);
                newTag.setCreateTime(LocalDateTime.now());
                newTag.setUpdateTime(LocalDateTime.now());
                try {
                    tagsMapper.insert(newTag);
                    tagIds.add(newTag.getId());
                    log.info("自动创建标签：'{}' → ID={}", name, newTag.getId());
                } catch (org.springframework.dao.DuplicateKeyException e) {
                    // 并发场景下另一个线程已插入，回查获取ID
                    Tags existing = tagsMapper.selectByName(name);
                    tagIds.add(existing.getId());
                    log.info("标签'{}'已存在（并发创建），使用已有ID={}", name, existing.getId());
                }
            }
        }

        return tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    // ==================== 创建/更新文章 ====================

    private void updateExistingArticle(ArticleDTO existing, String downloadUrl,
                                       MarkdownFrontMatter fm, String filePath, String sha) {
        Article updateEntity = new Article();
        updateEntity.setId(existing.getId());
        updateEntity.setGithubUrl(downloadUrl);
        if (sha != null) {
            updateEntity.setGithubSha(sha);
        }

        if (existing.getCategoryId() == null) {
            Long categoryId = resolveCategoryId(fm, filePath);
            if (categoryId != null) {
                updateEntity.setCategoryId(categoryId);
            }
        }
        if (existing.getTags() == null || existing.getTags().isEmpty()) {
            String tagIds = resolveTagIds(fm);
            if (tagIds != null) {
                updateEntity.setTags(tagIds);
            }
        }

        articleMapper.update(updateEntity);
    }

    private void createArticleFromGithub(String title, String downloadUrl,
                                         MarkdownFrontMatter fm,
                                         String rawMarkdown, String filePath, String sha) {
        String bodyContent = fm.getBodyContent() != null ? fm.getBodyContent() : rawMarkdown;
        String summary = fm.getSummary() != null ? fm.getSummary() : generateSummary(bodyContent, 200);

        boolean autoPublish = isAutoPublishEnabled();

        Article article = new Article();
        article.setTitle(title);
        article.setContent(rawMarkdown);
        article.setSummary(summary);
        article.setGithubUrl(downloadUrl);
        article.setGithubSha(sha);
        article.setCategoryId(resolveCategoryId(fm, filePath));
        article.setTags(resolveTagIds(fm));
        article.setSyncStatus(1);
        article.setLastSyncTime(LocalDateTime.now());
        article.setStatus(autoPublish ? 1 : 0);
        article.setIsComment(1);
        article.setViewCount(0);
        if (autoPublish) {
            article.setPublishTime(LocalDateTime.now());
        }
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.insert(article);

        log.info("创建文章：'{}'，状态={}", title, autoPublish ? "已发布" : "草稿");
    }

    private boolean isAutoPublishEnabled() {
        try {
            Map<String, String> config = configService.getAllConfigAsMap();
            String val = config.get("github_sync_auto_publish");
            return "true".equalsIgnoreCase(val);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== 辅助方法 ====================

    private Map<String, Integer> emptyResult() {
        Map<String, Integer> empty = new HashMap<>();
        empty.put("matched", 0);
        empty.put("created", 0);
        empty.put("failed", 0);
        return empty;
    }

    private void markSyncSuccess(Long articleId) {
        Article entity = new Article();
        entity.setId(articleId);
        entity.setSyncStatus(1);
        entity.setLastSyncTime(LocalDateTime.now());
        articleMapper.update(entity);
    }

    private void markSyncFailed(Long articleId) {
        Article entity = new Article();
        entity.setId(articleId);
        entity.setSyncStatus(2);
        articleMapper.update(entity);
        log.warn("同步标记为失败：文章ID={}", articleId);
    }

    private String generateSummary(String content, int maxLength) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        String plainText = content
                .replaceAll("#{1,6}\\s+", "")
                .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                .replaceAll("\\*(.+?)\\*", "$1")
                .replaceAll("\\[(.+?)\\]\\(.+?\\)", "$1")
                .replaceAll("!\\[.*?\\]\\(.*?\\)", "")
                .replaceAll("`(.+?)`", "$1")
                .replaceAll("```[\\s\\S]*?```", "")
                .replaceAll("^>\\s+", "")
                .replaceAll("^[-*+]\\s+", "")
                .replaceAll("\\n+", " ")
                .trim();
        if (plainText.length() > maxLength) {
            return plainText.substring(0, maxLength) + "...";
        }
        return plainText;
    }
}
