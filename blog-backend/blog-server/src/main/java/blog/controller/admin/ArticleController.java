package blog.controller.admin;

import blog.dto.ArticleDTO;
import blog.dto.ArticleQueryDTO;
import blog.entity.Article;
import blog.result.Result;
import blog.service.ArticleService;
import blog.vo.ArticleVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章管理控制器
 */
@RestController("adminArticleController")
@RequestMapping("/admin/articles")
@Slf4j
@ApiOperation("文章管理页面")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 创建文章
     */
    @PostMapping
    public Result saveArticle(@RequestBody ArticleDTO articleDTO)
    {
        log.info("保存文章：{}", articleDTO);

        if (articleDTO.getStatus() != null)
        {
            if (articleDTO.getContent() == null || articleDTO.getContent().trim().isEmpty())
            {
                return Result.error("发布文章时内容不能为空");
            }
            if (articleDTO.getCategoryId() == null)
            {
                return Result.error("发布文章时必须选择分类");
            }
            if (articleDTO.getTags() == null)
            {
                return Result.error("请选择选择标签");
            }


        }

        articleService.saveArticle(articleDTO);

        String message = articleDTO.getStatus() != null && articleDTO.getStatus() == 1 ? "文章发布成功" : "草稿保存成功";
        return Result.success(message);
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public Result updateArticle(@PathVariable Long id, @RequestBody ArticleDTO articleDTO)
    {
        log.info("更新文章，ID：{}，数据：{}", id, articleDTO);


        Article existingArticle = articleService.getArticleById(id);
        if (existingArticle == null) {
            return Result.error("文章不存在");
        }



        articleService.updateArticle(id, articleDTO);

        String message = articleDTO.getStatus() != null && articleDTO.getStatus() == 1
            ? "文章更新成功" : "草稿保存成功";
        return Result.success(message);
    }







    /**
     * 查询所有文章（简单查询，用于下拉列表等）
     */
    @GetMapping
    @ApiOperation("查询所有文章")
    public Result<List<ArticleVO>> getAllArticles()
    {
        log.info("查询所有文章");
        List<ArticleVO> articles = articleService.listAllArticles();
        return Result.success(articles);
    }

    /**
     * 查询文章列表（带条件查询）
     */
    @GetMapping("/list")
    public Result<List<ArticleVO>> listArticles(ArticleQueryDTO queryDTO)
    {
        log.info("查询文章列表：{}", queryDTO);
        List<ArticleVO> articles;

        if (queryDTO == null || (queryDTO.getKeyword() == null && queryDTO.getStatus() == null))
        {
            articles = articleService.listAllArticles();
        }
        else
        {
            //根据条件查询动态
            articles = articleService.listArticles(queryDTO);
        }

        return Result.success(articles);
    }

    /**
     * 获取最近文章
     */
    @GetMapping("/recent")
    public Result<List<ArticleVO>> getRecentArticles(@RequestParam(defaultValue = "10") Integer limit)
    {
        log.info("获取最近文章，数量：{}", limit);
        List<ArticleVO> articles = articleService.listAllArticles();

        if (articles.size() > limit)
        {
            articles = articles.subList(0, limit);
        }

        return Result.success(articles);
    }

    /**
     * 根据ID查询文章详情
     */
    @GetMapping("/{id}")
    public Result<Article> getArticle(@PathVariable Long id)
    {
        log.info("查询文章详情，ID：{}", id);
        Article article = articleService.getArticleById(id);
        return Result.success(article);
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id)
    {
        log.info("删除文章，ID：{}", id);
        articleService.deleteArticle(id);
        return Result.success();
    }


}
