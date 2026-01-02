package blog.controller.user;

import blog.dto.ArticleQueryDTO;
import blog.dto.TagsDTO;
import blog.result.Result;
import blog.service.ArticleService;
import blog.service.TagsService;
import blog.vo.ArticleVO;
import blog.vo.TagsVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController("userTagsController")
@RequestMapping("/api/tags")
@Slf4j
@ApiOperation("标签管理页面")
public class TagsController
{
    @Autowired
    private TagsService tagsService;

    @Autowired
    private ArticleService articleService;
    /**
     * 获取标签列表
     */
    @GetMapping()
    @ApiOperation("获取标签列表")
    public Result<List<TagsVO>> getTags()
    {
        log.info("获取标签列表");
        try {
            List<TagsVO> tags = tagsService.selectAll();
            log.info("从数据库获取到标签数量：{}", tags != null ? tags.size() : 0);

            if (tags == null) {
                tags = new ArrayList<>();
            }

            // 可以添加标签排序逻辑
            tags = tags.stream()
                    .sorted((t1, t2) -> {
                        // 按创建时间排序
                        if (t1.getCreateTime() == null) return 1;
                        if (t2.getCreateTime() == null) return -1;
                        return t1.getCreateTime().compareTo(t2.getCreateTime());
                    })
                    .collect(Collectors.toList());

            return Result.success(tags);
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
            return Result.success(new ArrayList<>());
        }
    }


    /**
     * 根据标签ID获取文章列表
     */
    @GetMapping("/{id}/articles")
    @ApiOperation("根据标签ID获取文章列表")
    public Result<Map<String, Object>> getArticlesByTagId(
            @PathVariable("id") Long tagId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        log.info("根据标签ID获取文章列表, tagId: {}, page: {}, size: {}", tagId, page, size);
        ArticleQueryDTO queryDTO = new ArticleQueryDTO();
        queryDTO.setTagId(tagId);
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
