package blog.service.impl;

import blog.dto.ArticleDTO;
import blog.mapper.ArticleMapper;
import blog.service.ArticleSyncService;
import blog.service.GithubMarkdownFetcher;
import blog.service.GithubRepoScanner;
import blog.service.SystemConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ArticleSyncServiceRenameIntegrationTest {

    @Autowired
    private ArticleSyncService articleSyncService;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private GithubRepoScanner scanner;

    @MockBean
    private GithubMarkdownFetcher fetcher;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM article");
        jdbcTemplate.execute("DELETE FROM category");
        jdbcTemplate.execute("DELETE FROM system_config");

        systemConfigService.updateConfig(Map.of(
                "github_sync_owner", "demo-owner",
                "github_sync_repo", "demo-repo",
                "github_sync_branch", "main",
                "github_sync_path", "notes"
        ));
    }

    @Test
    void autoDiscover_shouldNotDuplicateAndShouldCleanOldTitle_whenFileRenamed() {
        String oldTitle = "redis-distributed-lock";
        String newTitle = "redis-classic-practice-distributed-lock";

        String oldPath = "notes/redis-distributed-lock.md";
        String newPath = "notes/redis-classic-practice-distributed-lock.md";

        String oldUrl = "https://raw.githubusercontent.com/demo-owner/demo-repo/main/" + oldPath;
        String newUrl = "https://raw.githubusercontent.com/demo-owner/demo-repo/main/" + newPath;

        GithubRepoScanner.MdFileInfo oldFile = new GithubRepoScanner.MdFileInfo(
                "redis-distributed-lock.md", oldTitle, oldUrl, oldPath, "same-sha"
        );
        GithubRepoScanner.MdFileInfo renamedFile = new GithubRepoScanner.MdFileInfo(
                "redis-classic-practice-distributed-lock.md", newTitle, newUrl, newPath, "same-sha"
        );

        when(scanner.scanMarkdownFiles(eq("demo-owner"), eq("demo-repo"), eq("main"), eq("notes")))
                .thenReturn(List.of(oldFile), List.of(renamedFile));
        when(fetcher.fetchMarkdown(oldUrl)).thenReturn("# redis lock\nold content");

        Map<String, Integer> first = articleSyncService.autoDiscover();
        assertThat(first.get("created")).isEqualTo(1);
        assertThat(first.get("matched")).isEqualTo(0);
        assertThat(first.get("failed")).isEqualTo(0);

        ArticleDTO created = articleMapper.selectByTitle(oldTitle);
        assertThat(created).isNotNull();

        Map<String, Integer> second = articleSyncService.autoDiscover();
        assertThat(second.get("created")).isEqualTo(0);
        assertThat(second.get("matched")).isEqualTo(1);
        assertThat(second.get("failed")).isEqualTo(0);

        assertThat(articleMapper.selectByTitle(oldTitle)).isNull();

        ArticleDTO renamed = articleMapper.selectByTitle(newTitle);
        assertThat(renamed).isNotNull();

        assertThat(articleMapper.selectSyncCandidates()).hasSize(1);
    }

    @Test
    void autoDiscover_shouldCreateNewArticle_whenExistingManualArticleHasSameTitle() {
        jdbcTemplate.update(
                """
                INSERT INTO category(name, slug, sort_order, create_time, update_time)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                "面筋", "mi-jin", 10
        );
        Long existingCategoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM category WHERE name = ?",
                Long.class,
                "面筋"
        );
        jdbcTemplate.update(
                """
                INSERT INTO article(title, content, category_id, chapter_order, reading_minutes, is_core, view_count,
                                    is_comment, publish_time, create_time, update_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                "agent总结", "manual content", existingCategoryId, 1, 8, 0, 0, 1
        );

        systemConfigService.updateConfig(Map.of("github_sync_path", ""));

        String newPath = "10-面筋/agent总结.md";
        String newUrl = "https://raw.githubusercontent.com/demo-owner/demo-repo/main/" + newPath;
        GithubRepoScanner.MdFileInfo discoveredFile = new GithubRepoScanner.MdFileInfo(
                "agent总结.md", "agent总结", newUrl, newPath, "agent-sha"
        );

        when(scanner.scanMarkdownFiles(eq("demo-owner"), eq("demo-repo"), eq("main"), eq("")))
                .thenReturn(List.of(discoveredFile));
        when(fetcher.fetchMarkdown(newUrl)).thenReturn("# agent summary\nnew content");

        Map<String, Integer> result = articleSyncService.autoDiscover();

        assertThat(result.get("created")).isEqualTo(1);
        assertThat(result.get("matched")).isEqualTo(0);
        assertThat(result.get("failed")).isEqualTo(0);

        ArticleDTO synced = articleMapper.selectByGithubSha("agent-sha");
        assertThat(synced).isNotNull();
        assertThat(synced.getTitle()).isEqualTo("agent总结");

        Integer countByTitle = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM article WHERE title = ?",
                Integer.class,
                "agent总结"
        );
        assertThat(countByTitle).isEqualTo(2);
    }

    @Test
    void autoDiscover_shouldUpdateCategory_whenArticleMovedToAnotherFolder() {
        systemConfigService.updateConfig(Map.of("github_sync_path", ""));

        String oldPath = "6-Agent/agent总结.md";
        String newPath = "10-面筋/agent总结.md";
        String oldUrl = "https://raw.githubusercontent.com/demo-owner/demo-repo/main/" + oldPath;
        String newUrl = "https://raw.githubusercontent.com/demo-owner/demo-repo/main/" + newPath;

        GithubRepoScanner.MdFileInfo oldFile = new GithubRepoScanner.MdFileInfo(
                "agent总结.md", "agent总结", oldUrl, oldPath, "same-agent-sha"
        );
        GithubRepoScanner.MdFileInfo movedFile = new GithubRepoScanner.MdFileInfo(
                "agent总结.md", "agent总结", newUrl, newPath, "same-agent-sha"
        );

        when(scanner.scanMarkdownFiles(eq("demo-owner"), eq("demo-repo"), eq("main"), eq("")))
                .thenReturn(List.of(oldFile), List.of(movedFile));
        when(fetcher.fetchMarkdown(oldUrl)).thenReturn("# agent summary\nold content");

        Map<String, Integer> first = articleSyncService.autoDiscover();
        assertThat(first.get("created")).isEqualTo(1);

        ArticleDTO original = articleMapper.selectByGithubSha("same-agent-sha");
        assertThat(original).isNotNull();
        Long originalId = original.getId();
        Long originalCategoryId = original.getCategoryId();

        Map<String, Integer> second = articleSyncService.autoDiscover();
        assertThat(second.get("created")).isEqualTo(0);
        assertThat(second.get("matched")).isEqualTo(1);
        assertThat(second.get("failed")).isEqualTo(0);

        ArticleDTO moved = articleMapper.selectByGithubSha("same-agent-sha");
        assertThat(moved).isNotNull();
        assertThat(moved.getId()).isEqualTo(originalId);
        assertThat(moved.getCategoryId()).isNotEqualTo(originalCategoryId);

        String movedCategoryName = jdbcTemplate.queryForObject(
                "SELECT c.name FROM article a LEFT JOIN category c ON a.category_id = c.id WHERE a.id = ?",
                String.class,
                moved.getId()
        );
        assertThat(movedCategoryName).isEqualTo("面筋");
    }
}

