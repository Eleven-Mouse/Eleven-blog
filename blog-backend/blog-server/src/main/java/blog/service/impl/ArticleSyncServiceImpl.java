package blog.service.impl;

import blog.dto.ArticleDTO;
import blog.entity.Article;
import blog.entity.Category;
import blog.mapper.ArticleMapper;
import blog.mapper.CategoryMapper;
import blog.service.ArticleSyncService;
import blog.service.GithubMarkdownFetcher;
import blog.service.GithubRepoScanner;
import blog.service.SystemConfigService;
import blog.utils.MarkdownFrontMatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章同步服务实现
 * 核心能力：
 * 1. 按 github_url 同步单篇/全部文章
 * 2. 自动发现：扫描仓库 → 解析 Front Matter → 匹配分类 → 创建或更新文章
 * 匹配优先级：
 *   - 分类：Front Matter category > 文件夹名 > 不设置
 *   - 标题：Front Matter title > 文件名（去.md）
 */
@Service
@Slf4j
public class ArticleSyncServiceImpl implements ArticleSyncService {

    private static final String CONFIG_KEY_REPO = "github_sync_repo";
    private static final String CONFIG_KEY_OWNER = "github_sync_owner";
    private static final String CONFIG_KEY_BRANCH = "github_sync_branch";
    private static final String CONFIG_KEY_PATH = "github_sync_path";
    private static final String CONFIG_KEY_TOKEN = "github_sync_token";
    private static final String CONFIG_KEY_HOME_FEATURED = "home_featured_article_id";
    private static final String HOME_FEATURED_TITLE = "首页";
    private static final Pattern CATEGORY_PREFIX_PATTERN = Pattern.compile("^(\\d+)\\s*[-_. ]\\s*(.+)$");
    private static final String ASSET_ROOT_DIR = "sync-assets";
    private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("(\\!\\[[^\\]]*]\\(|\\[[^\\]]*]\\()([^\\s\\)]+)([^\\)]*\\))");
    private static final Pattern OBSIDIAN_EMBED_PATTERN = Pattern.compile("!\\[\\[([^\\]|]+)(?:\\|[^\\]]+)?]]");
    private static final Pattern OBSIDIAN_LINK_PATTERN = Pattern.compile("(?<!!)\\[\\[([^\\]|]+)(?:\\|([^\\]]+))?]]");
    private static final Set<String> ASSET_EXTENSIONS = Set.of(
            "png", "jpg", "jpeg", "gif", "webp", "svg", "pdf"
    );

    private final GithubMarkdownFetcher fetcher;
    private final GithubRepoScanner scanner;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final SystemConfigService configService;
    private final RestTemplate assetRestTemplate;

    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    public ArticleSyncServiceImpl(GithubMarkdownFetcher fetcher,
                                  GithubRepoScanner scanner,
                                  ArticleMapper articleMapper,
                                  CategoryMapper categoryMapper,
                                  SystemConfigService configService) {
        this.fetcher = fetcher;
        this.scanner = scanner;
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.configService = configService;
        this.assetRestTemplate = new RestTemplate();
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(30));
        this.assetRestTemplate.setRequestFactory(factory);
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

        String processedMarkdown = processMarkdownAssetsByGithubUrl(markdown, dto.getGithubUrl());

        if (processedMarkdown.equals(dto.getContent())) {
            log.info("同步跳过：内容未变化，ID={}", articleId);
            markSyncSuccess(articleId);
            return true;
        }

        Article updateEntity = new Article();
        updateEntity.setId(articleId);
        updateEntity.setContent(processedMarkdown);
        updateEntity.setSyncStatus(1);
        updateEntity.setLastSyncTime(LocalDateTime.now());
        articleMapper.update(updateEntity);

        log.info("同步成功：文章ID={}，内容长度={}", articleId, processedMarkdown.length());
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
        RepoSyncConfig repoSyncConfig = resolveRepoSyncConfig(config);
        String owner = repoSyncConfig.owner();
        String repo = repoSyncConfig.repo();
        String branch = repoSyncConfig.branch();
        String path = repoSyncConfig.path();
        String token = resolveConfigValue(config, CONFIG_KEY_TOKEN);

        if (owner == null || owner.isBlank() || repo == null || repo.isBlank()) {
            log.warn("自动发现未配置：请设置 github_sync_owner 和 github_sync_repo（或 github_sync_repo=owner/repo）");
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
                String processedMarkdown = processMarkdownAssets(rawMarkdown, owner, repo, branch, file.getPath(), token);

                // 解析 Front Matter
                MarkdownFrontMatter fm = MarkdownFrontMatter.parse(rawMarkdown);

                // 确定标题：Front Matter title > 文件名
                String title = fm.getTitle() != null ? fm.getTitle() : file.getTitle();

                // 按标题匹配已有文章
                ArticleDTO existing = articleMapper.selectByTitle(title);

                if (existing != null) {
                    updateExistingArticle(existing, file.getDownloadUrl(), fm, file.getPath(), file.getSha());
                    syncArticle(existing.getId());
                    setHomeFeaturedIfNeeded(existing.getId(), title, fm);
                    matched++;
                    log.info("自动发现-匹配：'{}' → 文章ID={}", title, existing.getId());
                } else {
                    Long createdId = createArticleFromGithub(title, file.getDownloadUrl(), fm, processedMarkdown, file.getPath(), file.getSha());
                    setHomeFeaturedIfNeeded(createdId, title, fm);
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
        RepoSyncConfig repoSyncConfig = resolveRepoSyncConfig(config);
        String owner = repoSyncConfig.owner();
        String repo = repoSyncConfig.repo();
        String branch = repoSyncConfig.branch();
        String path = repoSyncConfig.path();

        if (owner == null || owner.isBlank() || repo == null || repo.isBlank()) {
            return List.of();
        }
        return scanner.scanMarkdownFiles(owner, repo, branch, path);
    }

    // ==================== 分类解析 ====================

    /**
     * 解析分类：一级文件夹名 > Front Matter category
     * 匹配数据库分类（按名称），找不到则自动创建
     */
    private Long resolveCategoryId(MarkdownFrontMatter fm, String filePath) {
        String rawCategory = MarkdownFrontMatter.inferCategoryFromPath(filePath);
        if (rawCategory == null || rawCategory.isEmpty()) {
            rawCategory = fm.getCategory();
        }
        if (rawCategory == null) {
            return null;
        }
        CategoryMeta meta = parseCategoryMeta(rawCategory);
        String categoryName = meta.displayName();
        Integer categorySortOrder = meta.sortOrder();

        Category category = categoryMapper.selectByName(categoryName);
        if (category != null) {
            // 若分类已存在且目录前缀提供了顺序，则同步更新 sort_order
            if (categorySortOrder != null && !Objects.equals(categorySortOrder, category.getSortOrder())) {
                Category toUpdate = new Category();
                toUpdate.setId(category.getId());
                toUpdate.setSortOrder(categorySortOrder);
                toUpdate.setUpdateTime(LocalDateTime.now());
                categoryMapper.update(toUpdate);
                log.info("更新分类排序：'{}' sortOrder {} -> {}", categoryName, category.getSortOrder(), categorySortOrder);
            }
            log.info("分类匹配：'{}' → ID={}", categoryName, category.getId());
            return category.getId();
        }

        // 自动创建不存在的分类
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        // 无显式序号时放在后面，避免和显式排序分类混在一起
        newCategory.setSortOrder(categorySortOrder != null ? categorySortOrder : 1000);
        newCategory.setCreateTime(LocalDateTime.now());
        newCategory.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(newCategory);
        log.info("自动创建分类：'{}' sortOrder={} → ID={}", categoryName, newCategory.getSortOrder(), newCategory.getId());
        return newCategory.getId();
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
        if (fm.getChapterOrder() != null) {
            updateEntity.setChapterOrder(fm.getChapterOrder());
        }
        if (fm.getReadingMinutes() != null) {
            updateEntity.setReadingMinutes(fm.getReadingMinutes());
        }
        if (fm.getIsCore() != null) {
            updateEntity.setIsCore(fm.getIsCore());
        }

        articleMapper.update(updateEntity);
    }

    private Long createArticleFromGithub(String title, String downloadUrl,
                                         MarkdownFrontMatter fm,
                                         String rawMarkdown, String filePath, String sha) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(rawMarkdown);
        article.setGithubUrl(downloadUrl);
        article.setGithubSha(sha);
        article.setCategoryId(resolveCategoryId(fm, filePath));
        article.setSyncStatus(1);
        article.setLastSyncTime(LocalDateTime.now());
        article.setIsComment(1);
        article.setChapterOrder(fm.getChapterOrder() != null ? fm.getChapterOrder() : 0);
        article.setReadingMinutes(fm.getReadingMinutes() != null ? fm.getReadingMinutes() : 8);
        article.setIsCore(fm.getIsCore() != null ? fm.getIsCore() : 0);
        article.setViewCount(0);
        article.setPublishTime(LocalDateTime.now());
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.insert(article);

        log.info("创建文章：'{}'", title);
        return article.getId();
    }

    private void setHomeFeaturedIfNeeded(Long articleId, String title, MarkdownFrontMatter fm) {
        if (articleId == null) {
            return;
        }
        boolean byFrontMatter = fm != null && Boolean.TRUE.equals(fm.getHomeFeatured());
        boolean byTitle = HOME_FEATURED_TITLE.equals(title);
        if (!byFrontMatter && !byTitle) {
            return;
        }
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put(CONFIG_KEY_HOME_FEATURED, String.valueOf(articleId));
        configService.updateConfig(updateMap);
        log.info("已设置首页文章ID：{}（规则：{}）", articleId, byTitle ? "标题=首页" : "home_featured=true");
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

    private String processMarkdownAssetsByGithubUrl(String markdown, String githubUrl) {
        RepoContext context = parseRepoContext(githubUrl);
        if (context == null) {
            return markdown;
        }
        String token = getConfigValue(CONFIG_KEY_TOKEN);
        return processMarkdownAssets(markdown, context.owner(), context.repo(), context.branch(), context.filePath(), token);
    }

    private String processMarkdownAssets(String markdown, String owner, String repo, String branch, String mdPath, String token) {
        if (markdown == null || markdown.isBlank()) {
            return markdown;
        }
        String rewritten = rewriteMarkdownLinks(markdown, owner, repo, branch, mdPath, token);
        rewritten = rewriteObsidianEmbeds(rewritten, owner, repo, branch, mdPath, token);
        rewritten = rewriteObsidianLinks(rewritten, owner, repo, branch, mdPath, token);
        return rewritten;
    }

    private String rewriteMarkdownLinks(String markdown, String owner, String repo, String branch, String mdPath, String token) {
        Matcher matcher = MARKDOWN_LINK_PATTERN.matcher(markdown);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String prefix = matcher.group(1);
            String rawUrl = matcher.group(2);
            String suffix = matcher.group(3);

            String replaced = resolveAndMirrorAssetUrl(rawUrl, owner, repo, branch, mdPath, token);
            if (replaced != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(prefix + replaced + suffix));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String rewriteObsidianEmbeds(String markdown, String owner, String repo, String branch, String mdPath, String token) {
        Matcher matcher = OBSIDIAN_EMBED_PATTERN.matcher(markdown);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String target = matcher.group(1);
            String replaced = resolveAndMirrorAssetUrl(target, owner, repo, branch, mdPath, token);
            if (replaced != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement("![](" + replaced + ")"));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String rewriteObsidianLinks(String markdown, String owner, String repo, String branch, String mdPath, String token) {
        Matcher matcher = OBSIDIAN_LINK_PATTERN.matcher(markdown);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String target = matcher.group(1);
            String alias = matcher.group(2);
            String replaced = resolveAndMirrorAssetUrl(target, owner, repo, branch, mdPath, token);
            if (replaced != null) {
                String text = (alias != null && !alias.isBlank()) ? alias : Paths.get(target).getFileName().toString();
                matcher.appendReplacement(sb, Matcher.quoteReplacement("[" + text + "](" + replaced + ")"));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String resolveAndMirrorAssetUrl(String inputUrl, String owner, String repo, String branch, String mdPath, String token) {
        if (inputUrl == null || inputUrl.isBlank()) {
            return null;
        }
        String url = stripWrappers(inputUrl.trim());
        if (url.startsWith("#") || url.startsWith("mailto:") || url.startsWith("javascript:")) {
            return null;
        }

        String lower = url.toLowerCase(Locale.ROOT);
        if (lower.startsWith("/images/") || lower.startsWith("/upload/")) {
            return null;
        }
        if (!isAssetLink(url)) {
            return null;
        }

        AssetLocation location = buildAssetLocation(url, owner, repo, branch, mdPath);
        if (location == null) {
            return null;
        }

        return mirrorAsset(location, token);
    }

    private AssetLocation buildAssetLocation(String link, String owner, String repo, String branch, String mdPath) {
        try {
            if (link.startsWith("http://") || link.startsWith("https://")) {
                RepoContext context = parseRepoContext(link);
                if (context != null) {
                    String assetPath = normalizeRepoPath(context.filePath());
                    if (assetPath == null || assetPath.endsWith(".md")) {
                        return null;
                    }
                    String rawUrl = buildRawUrl(context.owner(), context.repo(), context.branch(), assetPath);
                    return new AssetLocation(rawUrl, context.owner(), context.repo(), context.branch(), assetPath);
                }
                return null;
            }

            String normalizedAssetPath = resolveRelativePath(mdPath, link);
            if (normalizedAssetPath == null) {
                return null;
            }
            String rawUrl = buildRawUrl(owner, repo, branch, normalizedAssetPath);
            return new AssetLocation(rawUrl, owner, repo, branch, normalizedAssetPath);
        } catch (Exception e) {
            log.warn("构建资源URL失败: {}, 原因: {}", link, e.getMessage());
            return null;
        }
    }

    private String mirrorAsset(AssetLocation asset, String token) {
        try {
            String safeOwner = safePathToken(asset.owner());
            String safeRepo = safePathToken(asset.repo());
            String safeBranch = safePathToken(asset.branch());
            String normalizedAssetPath = normalizeRepoPath(asset.assetPath());
            if (normalizedAssetPath == null) {
                return null;
            }

            String relativePath = ASSET_ROOT_DIR + "/" + safeOwner + "/" + safeRepo + "/" + safeBranch + "/" + normalizedAssetPath;
            Path localFile = Paths.get(uploadDir).resolve(relativePath.replace("/", java.io.File.separator)).normalize();
            Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!localFile.toAbsolutePath().startsWith(uploadRoot)) {
                log.warn("检测到非法资源路径，已跳过: {}", localFile);
                return null;
            }

            if (Files.exists(localFile) && Files.size(localFile) > 0) {
                return "/images/" + relativePath;
            }

            Files.createDirectories(localFile.getParent());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            headers.set("User-Agent", "ElevenBlog-Sync/1.0");
            if (token != null && !token.isBlank()) {
                headers.set("Authorization", "Bearer " + token);
            }
            ResponseEntity<byte[]> response = assetRestTemplate.exchange(
                    asset.rawUrl(), HttpMethod.GET, new HttpEntity<>(headers), byte[].class
            );
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || response.getBody().length == 0) {
                log.warn("资源下载失败: status={}, url={}", response.getStatusCode(), asset.rawUrl());
                return null;
            }
            Files.write(localFile, response.getBody());
            return "/images/" + relativePath;
        } catch (IOException e) {
            log.warn("资源落盘失败: {}, 原因: {}", asset.rawUrl(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("资源同步失败: {}, 原因: {}", asset.rawUrl(), e.getMessage());
            return null;
        }
    }

    private String buildRawUrl(String owner, String repo, String branch, String assetPath) {
        return "https://raw.githubusercontent.com/" +
                safeUrlPath(owner) + "/" +
                safeUrlPath(repo) + "/" +
                safeUrlPath(branch) + "/" +
                encodePath(assetPath);
    }

    private String encodePath(String path) {
        String[] segments = path.split("/");
        List<String> encoded = new ArrayList<>();
        for (String seg : segments) {
            encoded.add(URLEncoder.encode(seg, StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return String.join("/", encoded);
    }

    private String resolveRelativePath(String mdPath, String link) {
        if (mdPath == null || mdPath.isBlank()) {
            return null;
        }
        String cleaned = link;
        int q = cleaned.indexOf('?');
        if (q >= 0) cleaned = cleaned.substring(0, q);
        int f = cleaned.indexOf('#');
        if (f >= 0) cleaned = cleaned.substring(0, f);
        if (cleaned.startsWith("/")) {
            return normalizeRepoPath(cleaned.substring(1));
        }

        String normalizedMd = normalizeRepoPath(mdPath);
        if (normalizedMd == null) {
            return null;
        }
        int lastSlash = normalizedMd.lastIndexOf('/');
        String baseDir = lastSlash >= 0 ? normalizedMd.substring(0, lastSlash) : "";
        Path resolved = Paths.get(baseDir).resolve(cleaned).normalize();
        String result = resolved.toString().replace("\\", "/");
        return normalizeRepoPath(result);
    }

    private boolean isAssetLink(String link) {
        String path = link;
        int q = path.indexOf('?');
        if (q >= 0) path = path.substring(0, q);
        int f = path.indexOf('#');
        if (f >= 0) path = path.substring(0, f);
        int lastDot = path.lastIndexOf('.');
        if (lastDot < 0 || lastDot >= path.length() - 1) {
            return false;
        }
        String ext = path.substring(lastDot + 1).toLowerCase(Locale.ROOT);
        return ASSET_EXTENSIONS.contains(ext);
    }

    private RepoContext parseRepoContext(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String normalized = url.trim();
        if (normalized.contains("://github.com/") && normalized.contains("/blob/")) {
            normalized = normalized
                    .replace("://github.com/", "://raw.githubusercontent.com/")
                    .replace("/blob/", "/");
        }
        if (!normalized.contains("://raw.githubusercontent.com/")) {
            return null;
        }
        try {
            URI uri = URI.create(normalized);
            String[] segments = uri.getPath().split("/");
            if (segments.length < 5) {
                return null;
            }
            String owner = segments[1];
            String repo = segments[2];
            String branch = segments[3];
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 4; i < segments.length; i++) {
                if (i > 4) pathBuilder.append('/');
                pathBuilder.append(segments[i]);
            }
            String filePath = pathBuilder.toString();
            return new RepoContext(owner, repo, branch, filePath);
        } catch (Exception e) {
            return null;
        }
    }

    private String safeUrlPath(String token) {
        if (token == null) {
            return "";
        }
        return token.replaceAll("[^a-zA-Z0-9._-]", "");
    }

    private String safePathToken(String token) {
        return safeUrlPath(token);
    }

    private String stripWrappers(String url) {
        String cleaned = url;
        if (cleaned.startsWith("<") && cleaned.endsWith(">")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned;
    }

    private String normalizeRepoPath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return null;
        }
        String normalized = rawPath.replace("\\", "/");
        Path path = Paths.get(normalized).normalize();
        String result = path.toString().replace("\\", "/");
        if (result.startsWith("..") || result.contains("/../") || result.equals(".")) {
            return null;
        }
        return result;
    }

    private String getConfigValue(String key) {
        try {
            Map<String, String> cfg = configService.getAllConfigAsMap();
            return resolveConfigValue(cfg, key);
        } catch (Exception e) {
            String envKey = envKeyForConfig(key);
            if (envKey == null) {
                return null;
            }
            return trimToNull(System.getenv(envKey));
        }
    }

    private RepoSyncConfig resolveRepoSyncConfig(Map<String, String> config) {
        String owner = resolveConfigValue(config, CONFIG_KEY_OWNER);
        String repo = resolveConfigValue(config, CONFIG_KEY_REPO);
        String branch = resolveConfigValue(config, CONFIG_KEY_BRANCH);
        String path = resolveConfigValue(config, CONFIG_KEY_PATH);

        OwnerRepo ownerRepo = parseOwnerRepo(repo);
        if (owner == null) {
            owner = ownerRepo.owner();
        }
        if (ownerRepo.repo() != null) {
            repo = ownerRepo.repo();
        }

        return new RepoSyncConfig(owner, repo, branch != null ? branch : "main", path != null ? path : "");
    }

    private String resolveConfigValue(Map<String, String> config, String key) {
        if (config != null) {
            String value = trimToNull(config.get(key));
            if (value != null) {
                return value;
            }
        }
        String envKey = envKeyForConfig(key);
        if (envKey == null) {
            return null;
        }
        return trimToNull(System.getenv(envKey));
    }

    private OwnerRepo parseOwnerRepo(String repoValue) {
        String raw = trimToNull(repoValue);
        if (raw == null) {
            return new OwnerRepo(null, null);
        }

        String candidate = raw;
        if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
            try {
                URI uri = URI.create(candidate);
                if (uri.getHost() != null && uri.getHost().contains("github.com")) {
                    String path = uri.getPath();
                    if (path != null) {
                        String[] seg = path.replaceAll("^/+", "").split("/");
                        if (seg.length >= 2) {
                            return new OwnerRepo(trimToNull(seg[0]), trimToNull(seg[1]));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            return new OwnerRepo(null, raw);
        }

        if (candidate.contains("/")) {
            String[] parts = candidate.split("/", 2);
            return new OwnerRepo(trimToNull(parts[0]), trimToNull(parts[1]));
        }
        return new OwnerRepo(null, raw);
    }

    private String envKeyForConfig(String key) {
        return switch (key) {
            case CONFIG_KEY_OWNER -> "GITHUB_SYNC_OWNER";
            case CONFIG_KEY_REPO -> "GITHUB_SYNC_REPO";
            case CONFIG_KEY_BRANCH -> "GITHUB_SYNC_BRANCH";
            case CONFIG_KEY_PATH -> "GITHUB_SYNC_PATH";
            case CONFIG_KEY_TOKEN -> "GITHUB_SYNC_TOKEN";
            default -> null;
        };
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private CategoryMeta parseCategoryMeta(String raw) {
        String normalized = raw == null ? "" : raw.trim();
        if (normalized.isEmpty()) {
            return new CategoryMeta(raw, null);
        }

        Matcher matcher = CATEGORY_PREFIX_PATTERN.matcher(normalized);
        if (!matcher.matches()) {
            return new CategoryMeta(normalized, null);
        }

        Integer order = null;
        try {
            order = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ignored) {
        }
        String displayName = matcher.group(2).trim();
        if (displayName.isEmpty()) {
            displayName = normalized;
        }
        return new CategoryMeta(displayName, order);
    }

    private record CategoryMeta(String displayName, Integer sortOrder) {
    }

    private record RepoSyncConfig(String owner, String repo, String branch, String path) {
    }

    private record OwnerRepo(String owner, String repo) {
    }

    private record RepoContext(String owner, String repo, String branch, String filePath) {
    }

    private record AssetLocation(String rawUrl, String owner, String repo, String branch, String assetPath) {
    }

}
