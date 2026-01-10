package blog.service;

import blog.dto.CommentDTO;
import blog.dto.CommentQueryDTO;
import blog.entity.Comment;
import blog.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author Eleven
 * @version 1.0
 */
public interface CommentService {


    /**
     * 创建评论
     */
    void createComment(CommentDTO commentDTO);


    /**
     * 统计评论总数
     */
    Long countTotal();

    List<CommentVO> getComments(String page, Long blogId, Long parentCommentId);

    void deleteCommentById(Long id);

    void updateStatus(Long id, Boolean status);

}

