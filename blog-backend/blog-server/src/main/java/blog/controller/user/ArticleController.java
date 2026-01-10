package blog.controller.user;

import blog.dto.ArticleDTO;
import blog.dto.ArticleQueryDTO;
import blog.entity.Article;
import blog.result.Result;
import blog.service.ArticleService;
import blog.vo.ArticleVO;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
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
@RestController("userArticleController")
@RequestMapping("/api/articles")
@Slf4j
@ApiOperation("文章页面")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取文章列表（支持分页）
     */
    @GetMapping()
    @ApiOperation("获取文章列表")
    public Result<Map<String, Object>> getArticles(
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
     * 根据ID获取文章详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取文章详情")
    public Result<ArticleVO> getArticleById(@PathVariable Long id, HttpServletRequest request)
    {
        log.info("获取文章详情，ID：{}", id);
        String userIp = getIpAddress(request);

        // 只获取已发布的文章
        ArticleVO articleVO = new ArticleVO();
        articleVO.setStatus(1);

        articleVO = articleService.getArticleById(id,userIp);

        if (articleVO == null) {
            return Result.error("文章不存在");
        }

        return Result.success(articleVO);
    }
    /**
     * 获取真实IP
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，x-forwarded-for 可能是 "1.1.1.1, 2.2.2.2"，第一个才是真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }



    /**
     * 获取随机文章（推荐文章）
     */
    @GetMapping("/articles/random")
    @ApiOperation("获取随机文章")
    public Result<List<ArticleVO>> getRandomArticles(@RequestParam(defaultValue = "6") int limit)
    {
        log.info("获取随机文章，数量：{}", limit);

        ArticleQueryDTO queryDTO = new ArticleQueryDTO();
        queryDTO.setStatus(1);
        List<ArticleVO> articles = articleService.listArticles(queryDTO);

        // 简单随机打乱（实际项目中可以用更好的算法）
        java.util.Collections.shuffle(articles);

        // 取前limit个
        if (articles.size() > limit) {
            articles = articles.subList(0, limit);
        }

        return Result.success(articles);
    }

}
