package blog.service.impl;

import blog.dto.ArticleDTO;
import blog.dto.CommentDTO;
import blog.entity.Article;
import blog.entity.Comment;
import blog.mapper.ArticleMapper;
import blog.mapper.CommentMapper;
import blog.service.CommentService;
import blog.service.MailService;
import blog.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 评论服务实现类
 *
 * 升级点:
 * - 博主标识: nickname匹配 blog.owner.name → isOwner=true
 * - 楼层号: 自动递增 floor
 * - 点赞: likeCount + 1
 * - IP归属地: 调用外部API解析
 * - 防 spam: Redis 30秒限流
 * - 邮件通知: 回复时发邮件
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired(required = false)
    private MailService mailService;

    @Value("${blog.owner.email:your-email@example.com}")
    private String ownerEmail;

    @Value("${blog.owner.avatar:your-avatar-url.png}")
    private String ownerAvatar;

    @Value("${blog.owner.nickname:博主}")
    private String ownerNickname;

    @Value("${blog.owner.github-id:0}")
    private Long ownerGithubId;

    /** 防 spam: 同一IP 60秒内最多10条评论 */
    private static final long SPAM_WINDOW_SECONDS = 60;
    private static final int SPAM_MAX_COUNT = 10;

    @Override
    @Transactional
    public void createComment(CommentDTO commentDTO) {
        log.info("开始创建评论，提交内容：{}", commentDTO);

        // --- 防 spam 检查 ---
        checkSpamFilter(commentDTO);

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);

        // 1. 判断是否为博主评论 (通过 GitHub ID 匹配，优先级最高)
        boolean isOwner = ownerGithubId != null && ownerGithubId > 0
                && ownerGithubId.equals(commentDTO.getGithubId());
        if (isOwner) {
            log.info("检测到博主评论");
            comment.setNickname(ownerNickname);
            comment.setStatus(true);
            comment.setNotice(true);
            comment.setIsOwner(true);
        } else {
            comment.setIsOwner(false);
            if (comment.getAvatar() == null || comment.getAvatar().isEmpty()) {
                comment.setAvatar("default-guest-avatar.png");
            }
        }

        // 2. 初始化点赞数
        comment.setLikeCount(0);

        // 3. 处理页面和文章ID的关联关系
        if (commentDTO.getBlogId() != null) {
            comment.setPage(null);
        } else if (commentDTO.getPage() != null && !commentDTO.getPage().trim().isEmpty()) {
            comment.setBlogId(null);
        } else {
            if (commentDTO.getParentCommentId() == null) {
                throw new IllegalArgumentException("页面标识(page)和文章ID(blogId)不能同时为空");
            }
        }

        // 4. 处理父子评论关系
        if (commentDTO.getParentCommentId() != null) {
            log.info("此评论为子评论，父评论ID: {}", commentDTO.getParentCommentId());
            CommentVO parentComment = commentMapper.selectById(commentDTO.getParentCommentId());
            if (parentComment == null) {
                throw new IllegalArgumentException("回复的父评论不存在");
            }
            comment.setPage(parentComment.getPage());
            comment.setBlogId(parentComment.getBlogId());
            // 子评论不占楼层
            comment.setFloor(null);

            // --- 邮件通知: 回复时通知被回复者 ---
            notifyReply(commentDTO, parentComment);

            // --- 通知博主: 非博主回复时也通知博主 ---
            if (!isOwner) {
                notifyOwnerNewComment(commentDTO, parentComment.getTitle());
            }
        } else {
            // 5. 一级评论: 自动计算楼层号
            Integer maxFloor = commentMapper.selectMaxFloor(comment.getBlogId(), comment.getPage());
            comment.setFloor(maxFloor == null ? 1 : maxFloor + 1);

            // --- 通知博主: 访客发表新评论 ---
            if (!isOwner) {
                String articleTitle = resolveArticleTitle(comment.getBlogId());
                notifyOwnerNewComment(commentDTO, articleTitle);
            }
        }

        // 6. 解析IP归属地
        if (comment.getIp() != null) {
            comment.setLocation(resolveIpLocation(comment.getIp()));
        }

        commentMapper.insert(comment);
        log.info("评论创建成功，ID：{}，楼层：{}", comment.getId(), comment.getFloor());
    }

    /**
     * 防 spam: 同一IP 60秒内最多10条
     */
    private void checkSpamFilter(CommentDTO commentDTO) {
        String redisKey = "comment:spam:ip_" + commentDTO.getIp();

        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, SPAM_WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        if (count != null && count > SPAM_MAX_COUNT) {
            throw new RuntimeException("评论太频繁，请稍后再试");
        }
    }

    /**
     * 邮件通知: A评论 → B回复A → A收到邮件
     */
    private void notifyReply(CommentDTO reply, CommentVO parent) {
        if (mailService == null) {
            log.debug("MailService未配置，跳过邮件通知");
            return;
        }
        // 只通知有邮箱且不是博主自己的回复
        String parentEmail = parent.getEmail();
        if (parentEmail == null || parentEmail.isEmpty()) {
            return;
        }
        if (ownerEmail.equalsIgnoreCase(parentEmail) && reply.getNickname() != null
                && ownerNickname.equals(reply.getNickname())) {
            return;
        }
        try {
            String articleTitle = parent.getTitle() != null ? parent.getTitle() : "文章";
            mailService.sendReplyNotification(
                    parentEmail,
                    parent.getNickname(),
                    reply.getNickname(),
                    reply.getContent(),
                    articleTitle
            );
            log.info("已发送回复通知邮件给: {}", parentEmail);
        } catch (Exception e) {
            log.warn("发送邮件通知失败: {}", e.getMessage());
        }
    }

    /**
     * 解析IP归属地
     * 使用 ip-api.com 免费API (仅限生产环境有公网IP时生效)
     */
    private String resolveIpLocation(String ip) {
        if (ip == null || ip.isEmpty() || "unknown".equals(ip)
                || ip.startsWith("127.") || ip.startsWith("192.168.")
                || ip.startsWith("10.") || ip.equals("0:0:0:0:0:0:0:1")) {
            return "本地";
        }
        try {
            String url = "http://ip-api.com/json/" + ip + "?lang=zh-CN&fields=status,regionName,city";
            String response = new org.springframework.web.client.RestTemplate()
                    .getForObject(url, String.class);
            if (response != null) {
                com.fasterxml.jackson.databind.JsonNode node =
                        new com.fasterxml.jackson.databind.ObjectMapper().readTree(response);
                if ("success".equals(node.get("status").asText())) {
                    String region = node.has("regionName") ? node.get("regionName").asText() : "";
                    String city = node.has("city") ? node.get("city").asText() : "";
                    return region.equals(city) ? city : region + " " + city;
                }
            }
        } catch (Exception e) {
            log.warn("IP归属地解析失败: ip={}, error={}", ip, e.getMessage());
        }
        return null;
    }

    @Override
    public Long countTotal() {
        return commentMapper.countTotal();
    }

    /**
     * 获取评论列表（二级结构）
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

        // 3. 补充文章标题
        for (CommentVO vo : topLevelComments) {
            if (vo.getBlogId() != null) {
                ArticleDTO article = articleMapper.selectById(vo.getBlogId());
                if (article != null) {
                    vo.setTitle(article.getTitle());
                }
            }
        }

        // 4. 补充父评论昵称
        for (CommentVO vo : topLevelComments) {
            fillParentNickname(vo);
            // 子评论也填充
            if (vo.getReplyComments() != null) {
                for (CommentVO reply : vo.getReplyComments()) {
                    fillParentNickname(reply);
                }
            }
        }

        return topLevelComments;
    }

    private void fillParentNickname(CommentVO vo) {
        if (vo.getParentCommentId() != null) {
            CommentVO parent = commentMapper.selectById(vo.getParentCommentId());
            if (parent != null) {
                vo.setParentNickname(parent.getNickname());
            } else {
                vo.setParentNickname("该评论已删除");
            }
        }
    }

    @Override
    @Transactional
    public void deleteCommentById(Long id) {
        List<CommentVO> childComments = commentMapper.selectByParentId(id);
        for (CommentVO child : childComments) {
            deleteCommentById(child.getId());
        }
        commentMapper.deleteCommentById(id);
        log.info("成功删除评论及其子评论，ID: {}", id);
    }

    @Override
    public void updateStatus(Long id, Boolean status) {
        commentMapper.updateStatus(id, status);
    }

    @Override
    public void likeComment(Long id) {
        CommentVO comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        commentMapper.incrementLikeCount(id);
    }

    /**
     * 通知博主有新评论（一级评论或回复）
     */
    private void notifyOwnerNewComment(CommentDTO commentDTO, String articleTitle) {
        if (mailService == null) {
            log.debug("MailService未配置，跳过博主通知");
            return;
        }
        try {
            String title = articleTitle != null ? articleTitle : "文章";
            mailService.sendNewCommentNotification(
                    commentDTO.getNickname(),
                    commentDTO.getContent(),
                    title
            );
            log.info("已发送新评论通知给博主");
        } catch (Exception e) {
            log.warn("发送博主通知失败: {}", e.getMessage());
        }
    }

    /**
     * 根据blogId解析文章标题
     */
    private String resolveArticleTitle(Long blogId) {
        if (blogId == null) {
            return "留言板";
        }
        ArticleDTO article = articleMapper.selectById(blogId);
        return article != null ? article.getTitle() : "文章";
    }
}
