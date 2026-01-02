package blog.mapper;

import blog.entity.Comment;
import blog.vo.CommentVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评论Mapper接口
 *
 * @author Eleven
 * @version 1.0
 */
@Mapper
public interface CommentMapper {


    /**
     * 根据父评论ID和子评论ID删除子评论
     */
    @Delete("DELETE FROM comment WHERE id = #{childCommentId} AND parent_comment_id = #{parentCommentId}")
  void deleteChildCommentById(Long parentCommentId, Long childCommentId);


    /**
     * 插入评论
     */
    void insert(Comment comment);

    /**
     * 根据ID查询评论
     */
    @Select("SELECT * FROM comment WHERE id = #{id}")
    CommentVO selectById(Long id);

    /**
     * 查询所有评论（带文章标题）
     */
    List<CommentVO> selectAll();

    /**
     * 根据文章ID查询评论
     */
    List<CommentVO> selectByBlogId(Long blogId);

    /**
     * 根据条件查询评论
     */
    List<CommentVO> selectByCondition(Long articleId, String page, Integer isPublic, String nickname);


    /**
     * 统计评论总数
     */
    @Select("SELECT COUNT(*) FROM comment")
    Long countTotal();

    /**
     * 更新评论
     */
    void update(Comment comment);

    /**
     * 根据ID删除评论,一并删除子评论
     */
    @Delete("DELETE FROM comment WHERE id = #{id}")
    void deleteCommentById(Long id);


    List<CommentVO> getComments(String page, Long blogId, Long parentCommentId);

    List<CommentVO> selectByParentId(Long id);

    @Update("update comment set status = #{status} where id= #{id}")
    void updateStatus(Long id, Boolean status);
    @Update("update comment set notice = #{notice} where id= #{id}")
    void updateCommentNoticeById(Long id, Boolean notice);

    @Delete("delete from comment where blog_Id = #{blog_Id}")
    void deleteByArticleId(Long id);
}
