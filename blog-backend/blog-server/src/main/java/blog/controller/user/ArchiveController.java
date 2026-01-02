package blog.controller.user;

import blog.dto.ArticleQueryDTO;
import blog.result.Result;
import blog.service.ArticleService;
import blog.vo.ArticleVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/archive")
@Slf4j
@ApiOperation("文章页面")
public class ArchiveController
{

    @Autowired
    private ArticleService articleService;
    /**
     * 获取归档数据（按年月分组）
     */
    @GetMapping
    @ApiOperation("获取归档数据")
    public Result<Map<String, Object>> getArchive()
    {
        log.info("获取归档数据");

        ArticleQueryDTO queryDTO = new ArticleQueryDTO();
        queryDTO.setStatus(1);
        List<ArticleVO> articles = articleService.listArticles(queryDTO);

        Map<String, List<ArticleVO>> archiveMap = articles.stream()
                .collect(Collectors.groupingBy(article -> {
                    if (article.getPublishTime() != null) {
                        return article.getPublishTime().getYear() + "-" +
                                String.format("%02d", article.getPublishTime().getMonthValue());
                    }
                    return "未知";
                }));

        Map<String, Object> result = new HashMap<>();
        result.put("total", articles.size());
        result.put("archive", archiveMap);

        return Result.success(result);
    }
}
