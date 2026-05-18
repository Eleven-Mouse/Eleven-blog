package blog.service.impl;

import blog.dto.ArticleDTO;
import blog.dto.ArticleQueryDTO;
import blog.entity.Article;
import blog.mapper.ArticleMapper;
import blog.mapper.CommentMapper;
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

        if (article.getIsComment() == null)
        {
            article.setIsComment(1);
        }
        if (article.getPublishTime() == null) {
            article.setPublishTime(now);
        }

        if (article.getContent() == null)
        {
            article.setContent("");
        }

        if (article.getChapterOrder() == null) {
            article.setChapterOrder(0);
        }
        if (article.getReadingMinutes() == null) {
            article.setReadingMinutes(8);
        }
        if (article.getIsCore() == null) {
            article.setIsCore(0);
        }


        articleMapper.insert(article);
        log.info("文章创建成功，文章ID：{}", article.getId());

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
        } else if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            articles = articleMapper.selectByKeyword(queryDTO.getKeyword());
        } else {
            articles = articleMapper.selectAll();
        }

        if (articles == null || articles.isEmpty()) {
            return articles;
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

        articleMapper.update(article);
        log.info("文章更新成功，文章ID：{}", id);
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

}
