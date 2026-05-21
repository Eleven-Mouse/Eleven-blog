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
}

