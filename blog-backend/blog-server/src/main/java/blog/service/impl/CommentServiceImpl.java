package blog.service.impl;

import blog.dto.ArticleDTO;
import blog.dto.CommentDTO;
import blog.entity.Article;
import blog.entity.Comment;
import blog.mapper.ArticleMapper;
import blog.mapper.CommentMapper;
import blog.service.CommentService;
import blog.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务实现类
 *
 * @author Eleven
 * @version 1.1
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;

    // 从配置文件 application.yml 中读取博主邮箱
    @Value("${blog.owner.email:your-email@example.com}")
    private String ownerEmail;

    @Value("${blog.owner.avatar:your-avatar-url.png}")
    private String ownerAvatar;

    @Value("${blog.owner.nickname:博主}")
    private String ownerNickname;



    @Override
    @Transactional
    public void createComment(CommentDTO commentDTO) {
        log.info("开始创建评论，提交内容：{}", commentDTO);

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);

        // 1. 判断是否为博主评论
        if (ownerEmail.equalsIgnoreCase(comment.getEmail())) {
            log.info("检测到博主评论");
            comment.setNickname(ownerNickname);
            comment.setAvatar(ownerAvatar);
            comment.setStatus(true);
            comment.setNotice(true);
        } else {
            if (comment.getAvatar() == null || comment.getAvatar().isEmpty()) {
                comment.setAvatar("default-guest-avatar.png");
            }
        }

        // 2. 处理页面和文章ID的关联关系
        if (commentDTO.getBlogId() != null) {
            log.info("评论关联到文章ID: {}", commentDTO.getBlogId());
            comment.setPage(null);
        } else if (commentDTO.getPage() != null && !commentDTO.getPage().trim().isEmpty()) {
            log.info("评论所在页面: {}", commentDTO.getPage());
            comment.setBlogId(null);
        } else {
            if (commentDTO.getParentCommentId() == null) {
                 throw new IllegalArgumentException("页面标识(page)和文章ID(blogId)不能同时为空");
            }
        }

        // 3. 处理父子评论关系
        if (commentDTO.getParentCommentId() != null) {
            log.info("此评论为子评论，父评论ID: {}", commentDTO.getParentCommentId());
            CommentVO parentComment = commentMapper.selectById(commentDTO.getParentCommentId());
            if (parentComment == null) {
                throw new IllegalArgumentException("回复的父评论不存在");
            }
            comment.setPage(parentComment.getPage());
            comment.setBlogId(parentComment.getBlogId());
        }

        commentMapper.insert(comment);
        log.info("评论创建成功，ID：{}", comment.getId());
    }



    @Override
    public Long countTotal() {
        return commentMapper.countTotal();
    }

    /**
     * 获取二级评论列表
     */
    @Override
    @Transactional
    public List<CommentVO> getComments(String page, Long blogId, Long parentCommentId) {
        // 1. 获取所有顶级评论
        List<CommentVO> topLevelComments = commentMapper.getComments(page, blogId, -1L);

        // 2. 为每个顶级评论获取其下的二级回复
        for (CommentVO topComment : topLevelComments) {
            List<CommentVO> replies = commentMapper.selectByParentId(topComment.getId());
            topComment.setReplyComments(replies);
        }

        for(CommentVO vo : topLevelComments)
        {
            if(vo.getBlogId() != null)
            {
                ArticleDTO article = articleMapper.selectById(vo.getBlogId());
                if(article != null)
                {
                    vo.setTitle(article.getTitle());
                }
            }

        }

        for (CommentVO vo : topLevelComments) {

            if (vo.getParentCommentId() != null) {

                CommentVO parent = commentMapper.selectById(vo.getParentCommentId());
                if (parent != null) {
                    vo.setParentNickname(parent.getNickname());
                } else {
                    vo.setParentNickname("该评论已删除");
                }
            }
        }


        return topLevelComments;
    }

    @Override
    @Transactional
    public void deleteCommentById(Long id)
    {
        List<CommentVO> childComments = commentMapper.selectByParentId(id);
        for (CommentVO child : childComments) {
            deleteCommentById(child.getId());
        }
        commentMapper.deleteCommentById(id);
        log.info("成功删除评论及其子评论，ID: {}", id);
    }

    @Override
    public void updateStatus(Long id, Boolean status)
    {
        commentMapper.updateStatus(id,status);
    }

}
