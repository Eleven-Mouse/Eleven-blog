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
    @Insert("INSERT INTO article(title, summary, content, cover_image, category_id, tags, " +
            "view_count, is_comment, status, publish_time, create_time, update_time) " +
            "VALUES(#{title}, #{summary}, #{content}, #{coverImage}, #{categoryId}, #{tags}, " +
            "#{viewCount}, #{isComment}, #{status}, #{publishTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);

    /**
     * 根据ID查询文章
     */
    @Select("SELECT * FROM article WHERE id = #{id}")
    ArticleDTO selectById(Long id);

    List<ArticleVO> selectAll();

    /**
     * 根据状态查询文章
     */
    @Select("SELECT a.id, a.title, a.summary, a.content, a.cover_image as coverImage, " +
            "a.category_id as categoryId, a.tags, a.view_count as viewCount, " +
            "a.is_comment as isComment, a.status, a.publish_time as publishTime, " +
            "a.create_time as createTime, a.update_time as updateTime, c.name as categoryName " +
            "FROM article a " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.status = #{status} " +
            "ORDER BY COALESCE(a.publish_time, a.create_time) DESC")
    List<ArticleVO> selectByStatus(Integer status);

    /**
     * 根据标题模糊查询
     */
    @Select("SELECT a.id, a.title, a.summary, a.content, a.cover_image as coverImage, " +
            "a.category_id as categoryId, a.tags, a.view_count as viewCount, " +
            "a.is_comment as isComment, a.status, a.publish_time as publishTime, " +
            "a.create_time as createTime, a.update_time as updateTime, c.name as categoryName " +
            "FROM article a " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.title LIKE CONCAT('%', #{keyword}, '%') " +
            "AND a.status != 2 " +
            "ORDER BY COALESCE(a.publish_time, a.create_time) DESC")
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

    /**
     * 根据标签ID查询文章
     */
    List<ArticleVO> selectByTagId(Long tagId);


    List<Map<String, Object>> getContributionData();

    List<Map<String, Object>> getCategoryCount();

    @Update("UPDATE article SET view_count = #{count} WHERE id = #{articleId}")
    void updateViewCount(Long articleId, Integer count);
}
