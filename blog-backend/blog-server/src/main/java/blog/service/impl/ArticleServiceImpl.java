package blog.service.impl;

import blog.dto.ArticleDTO;
import blog.dto.ArticleQueryDTO;
import blog.entity.Article;
import blog.mapper.ArticleMapper;
import blog.mapper.CommentMapper;
import blog.mapper.TagsMapper;
import blog.service.ArticleService;
import blog.service.ViewCountService;
import blog.vo.ArticleVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * 文章服务实现类
 */
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ViewCountService viewCountService;



    @Override
    @Transactional
    public Long saveArticle(ArticleDTO articleDTO) {
        log.info("开始创建文章：{}", articleDTO.getTitle());

        Article article = new Article();
        copyProperties(articleDTO, article);


        LocalDateTime now = LocalDateTime.now();
        article.setCreateTime(now);
        article.setUpdateTime(now);
        article.setViewCount(0);


        if (article.getStatus() == null)
        {
            article.setStatus(0);
        }

        if (article.getIsComment() == null)
        {
            article.setIsComment(1);
        }

        if (article.getStatus() == 1)
        {
            article.setPublishTime(now);
        }


        if (article.getContent() == null)
        {
            article.setContent("");
        }

        if (article.getSummary() == null)
        {
            article.setSummary("");
        }


        articleMapper.insert(article);

        if (articleDTO.getTags() != null && !articleDTO.getTags().trim().isEmpty())
        {
            log.info("文章标签：{}", articleDTO.getTags());
        }

        log.info("文章创建成功，文章ID：{}，状态：{}", article.getId(),
                article.getStatus() == 1 ? "已发布" : "草稿");

        return article.getId();
    }

    @Override
    public List<Map<String, Object>> getContributionData()
    {
        return articleMapper.getContributionData();
    }

    @Override
    public List<Map<String, Object>> getCategoryCount()
    {
        return articleMapper.getCategoryCount();
    }




    @Override
    public List<ArticleVO> listArticles(ArticleQueryDTO queryDTO)
    {
        log.info("根据条件查询文章：{}", queryDTO);
        List<ArticleVO> articles;

        // 根据不同条件查询
        if (queryDTO.getCategoryId() != null) {
            articles = articleMapper.selectByCategoryId(queryDTO.getCategoryId());
        } else if (queryDTO.getTagId() != null) {
            articles = articleMapper.selectByTagId(queryDTO.getTagId());
        } else if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            articles = articleMapper.selectByKeyword(queryDTO.getKeyword());
        } else if (queryDTO.getStatus() != null) {
            articles = articleMapper.selectByStatus(queryDTO.getStatus());
        } else {
            articles = articleMapper.selectAll();
        }

        if (articles == null || articles.isEmpty()) {
            return articles;
        }

        // 提取所有文章中的所有标签ID
        java.util.Set<Long> tagIds = articles.stream()
                .filter(article -> article.getTags() != null && !article.getTags().trim().isEmpty())
                .flatMap(article -> java.util.Arrays.stream(article.getTags().split(",")))
                .map(Long::valueOf)
                .collect(java.util.stream.Collectors.toSet());

        if (!tagIds.isEmpty()) {
            // 一次性查询所有标签ID对应的标签名称
            List<blog.entity.Tags> tagsList = tagsMapper.selectByIds(new java.util.ArrayList<>(tagIds));
            java.util.Map<Long, String> tagIdToNameMap = tagsList.stream()
                    .collect(java.util.stream.Collectors.toMap(blog.entity.Tags::getId, blog.entity.Tags::getName));

            // 遍历文章，将标签ID替换为名称
            articles.forEach(article -> {
                article.setStatusText(getStatusText(article.getStatus()));
                if (article.getTags() != null && !article.getTags().trim().isEmpty()) {
                    String tagNames = java.util.Arrays.stream(article.getTags().split(","))
                            .map(idStr -> tagIdToNameMap.getOrDefault(Long.valueOf(idStr), ""))
                            .collect(java.util.stream.Collectors.joining(","));
                    article.setTags(tagNames);
                }
            });
        } else {
            articles.forEach(article -> {
                article.setStatusText(getStatusText(article.getStatus()));
            });
        }

        return articles;
    }

    @Override
    public ArticleVO getArticleById(Long id,String userIp)
    {
        log.info("查询文章详情，ID：{}", id);
        ArticleDTO article = articleMapper.selectById(id);

        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article,vo);
        vo.setId(id);
        viewCountService.incrementViewCount(id,userIp);

        Integer viewCount = viewCountService.getViewCount(id);

        vo.setViewCount(viewCount);
        return vo;
    }



    @Override
    public void updateArticle(Long id, ArticleDTO articleDTO)
    {
        log.info("更新文章，ID：{}，标题：{}", id, articleDTO.getTitle());

        Article article = new Article();
        copyProperties(articleDTO, article);
        article.setId(id);

        article.setUpdateTime(LocalDateTime.now());


        if (article.getStatus() != null && article.getStatus() == 1)
        {
            ArticleDTO existingArticle = articleMapper.selectById(id);
            if (existingArticle != null && existingArticle.getStatus() != 1)
            {
                article.setPublishTime(LocalDateTime.now());
            }
        }


        articleMapper.update(article);

        log.info("文章更新成功，文章ID：{}，状态：{}", id,
                article.getStatus() == 1 ? "已发布" : "草稿");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id)
    {
        log.info("删除文章，ID：{}", id);
        commentMapper.deleteByArticleId(id);
        articleMapper.deleteById(id);
    }

    @Override
    public Long countTotal()
    {
        return articleMapper.countTotal();
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status)
    {
        if (status == null)
        {
            return "未知";
        }
        switch (status)
        {
            case 0:
                return "草稿";
            case 1:
                return "已发布";
            case 2:
                return "已删除";
            default:
                return "未知";
        }
    }
}
