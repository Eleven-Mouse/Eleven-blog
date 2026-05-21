package blog.mapper;

import blog.dto.ArticleDTO;
import blog.entity.Article;
import blog.vo.ArticleVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper {

    /**
     * 插入文章
     */
    @Insert("INSERT INTO article(title, content, cover_image, category_id, chapter_order, reading_minutes, is_core, " +
            "view_count, is_comment, publish_time, create_time, update_time, github_url, sync_status, last_sync_time, github_sha) " +
            "VALUES(#{title}, #{content}, #{coverImage}, #{categoryId}, #{chapterOrder}, #{readingMinutes}, #{isCore}, " +
            "#{viewCount}, #{isComment}, #{publishTime}, #{createTime}, #{updateTime}, #{githubUrl}, #{syncStatus}, #{lastSyncTime}, #{githubSha})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);

    /**
     * 根据ID查询文章
     */
    @Select("SELECT * FROM article WHERE id = #{id}")
    ArticleDTO selectById(Long id);

    List<ArticleVO> selectAll();

    /**
     * 根据标题模糊查询
     */
    @Select("SELECT a.id, a.title, a.content, a.cover_image as coverImage, " +
            "a.category_id as categoryId, a.chapter_order as chapterOrder, a.reading_minutes as readingMinutes, a.is_core as isCore, a.view_count as viewCount, " +
            "a.is_comment as isComment, a.publish_time as publishTime, " +
            "a.create_time as createTime, a.update_time as updateTime, " +
            "a.github_url as githubUrl, a.sync_status as syncStatus, a.last_sync_time as lastSyncTime, " +
            "c.name as categoryName " +
            "FROM article a " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.title LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY a.category_id ASC, a.chapter_order ASC, COALESCE(a.publish_time, a.create_time) DESC")
    List<ArticleVO> selectByKeyword(String keyword);

    /**
     * 更新文章
     */

    void update(Article article);

    /**
     * 删除文章（逻辑删除）
     */
    @Delete("delete from article where id = #{id}")
    void deleteById(Long id);

    /**
     * 统计文章总数（不包括已删除）
     */
    @Select("SELECT COUNT(*) FROM article")
    Long countTotal();

    /**
     * 根据分类ID查询文章
     */
    List<ArticleVO> selectByCategoryId(Long categoryId);

    List<Map<String, Object>> getContributionData();

    List<Map<String, Object>> getCategoryCount();

    @Update("UPDATE article SET view_count = #{count} WHERE id = #{articleId}")
    void updateViewCount(Long articleId, Integer count);

    /**
     * 查询需要同步的文章（github_url不为空）
     */
    @Select("SELECT id, github_url as githubUrl, sync_status as syncStatus, last_sync_time as lastSyncTime " +
            "FROM article WHERE github_url IS NOT NULL AND github_url != ''")
    List<Article> selectSyncCandidates();

    /**
     * 根据标题精确查询文章
     */
    @Select("SELECT * FROM article WHERE title = #{title} LIMIT 1")
    ArticleDTO selectByTitle(String title);

    /**
     * 根据 GitHub Raw URL 精确查询文章
     */
    @Select("SELECT * FROM article WHERE github_url = #{githubUrl} LIMIT 1")
    ArticleDTO selectByGithubUrl(String githubUrl);

    /**
     * 根据 GitHub Blob SHA 查询文章
     */
    @Select("SELECT * FROM article WHERE github_sha = #{githubSha} LIMIT 1")
    ArticleDTO selectByGithubSha(String githubSha);
}
