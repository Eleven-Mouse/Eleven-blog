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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


        ArticleVO existingArticle = articleService.getArticleById(id);
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
     * 获取文章列表（支持分页）
     */
    @GetMapping("/list")
    @ApiOperation("获取文章列表")
    public Result<Map<String, Object>> getArticlesList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        log.info("获取文章列表，分类ID：{}，分类名称：{}，关键词：{}，页码：{}，每页数量：{}", categoryId, category, keyword, page, size);

        try {
            ArticleQueryDTO queryDTO = new ArticleQueryDTO();
            if (keyword != null && !keyword.trim().isEmpty())
            {
                queryDTO.setKeyword(keyword);
            }
            // 只获取已发布的文章
            queryDTO.setStatus(1);

            List<ArticleVO> allArticles = articleService.listArticles(queryDTO);
            log.info("从数据库获取到文章数量：{}", allArticles != null ? allArticles.size() : 0);

            if (allArticles == null) {
                allArticles = new ArrayList<>();
            }

            // 如果有分类ID，过滤分类
            if (categoryId != null)
            {
                allArticles = allArticles.stream()
                        .filter(article -> article.getCategoryId() != null && article.getCategoryId().equals(categoryId))
                        .collect(Collectors.toList());
            }

            // 如果有分类名称，按分类名称过滤
            if (category != null && !category.trim().isEmpty())
            {
                allArticles = allArticles.stream()
                        .filter(article -> article.getCategoryName() != null && article.getCategoryName().equals(category))
                        .collect(Collectors.toList());
            }
            // 手动分页
            int total = allArticles.size();
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, total);

            List<ArticleVO> articles = startIndex < total ?
                    allArticles.subList(startIndex, endIndex) : new ArrayList<>();

            // 构造分页结果
            Map<String, Object> result = new HashMap<>();
            result.put("data", articles);

            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", page);
            pagination.put("totalPage", (int) Math.ceil((double) total / size));
            pagination.put("total", total);
            pagination.put("size", size);
            result.put("pagination", pagination);

            log.info("返回文章列表，当前页：{}，总数：{}，实际返回：{}", page, total, articles.size());
            return Result.success(result);

        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            // 返回空数据而不是错误，避免前端报错
            Map<String, Object> result = new HashMap<>();
            result.put("data", new ArrayList<>());

            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", page);
            pagination.put("totalPage", 0);
            pagination.put("total", 0);
            pagination.put("size", size);
            result.put("pagination", pagination);

            return Result.success(result);
        }
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
    public Result<ArticleVO> getArticleById(@PathVariable Long id)
    {
        log.info("查询文章详情，ID：{}", id);
        ArticleVO article = articleService.getArticleById(id);
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
