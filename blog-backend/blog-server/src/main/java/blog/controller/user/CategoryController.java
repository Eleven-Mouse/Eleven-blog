package blog.controller.user;

import blog.dto.ArticleQueryDTO;
import blog.dto.CategoryDTO;
import blog.result.Result;
import blog.service.ArticleService;
import blog.service.CategoryService;
import blog.vo.ArticleVO;
import blog.vo.CategoryVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("userCategoriesController")
@RequestMapping("/api/categories")
@Slf4j
@ApiOperation("分类管理页面")
public class CategoryController{

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;
    /**
     * 获取分类列表
     */
    @GetMapping()
    @ApiOperation("获取分类列表")
    public Result<List<CategoryVO>> getCategories()
    {
        log.info("获取分类列表");
        try {
            List<CategoryVO> categories = categoryService.selectAll();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.success(new ArrayList<>());
        }
    }


    /**
     * 根据分类ID获取文章列表
     */
    @GetMapping("/{id}/articles")
    @ApiOperation("根据分类ID获取文章列表")
    public Result<Map<String, Object>> getArticlesByCategoryId(
            @PathVariable("id") Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        log.info("根据分类ID获取文章列表, categoryId: {}, page: {}, size: {}", categoryId, page, size);
        ArticleQueryDTO queryDTO = new ArticleQueryDTO();
        queryDTO.setCategoryId(categoryId);
        queryDTO.setStatus(1);

        List<ArticleVO> allArticles = articleService.listArticles(queryDTO);

        // 手动分页
        int total = allArticles.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, total);

        List<ArticleVO> articles = startIndex < total ?
                allArticles.subList(startIndex, endIndex) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("data", articles);
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page);
        pagination.put("totalPage", (int) Math.ceil((double) total / size));
        pagination.put("total", total);
        pagination.put("size", size);
        result.put("pagination", pagination);

        return Result.success(result);
    }

}
