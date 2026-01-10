package blog.service;

import blog.dto.ArticleDTO;
import blog.dto.ArticleQueryDTO;
import blog.entity.Article;
import blog.vo.ArticleVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 文章服务接口
 */
@Service
public interface ArticleService {


    /**
     * 更新文章
     * @param id 文章ID
     * @param articleDTO 文章更新DTO
     */
    void updateArticle(Long id, ArticleDTO articleDTO);


    /**
     * 根据条件查询文章
     * @param queryDTO 查询条件
     * @return 文章列表
     */
    List<ArticleVO> listArticles(ArticleQueryDTO queryDTO);

    /**
     * 根据ID查询文章
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleVO getArticleById(Long id,String request);


    /**
     * 删除文章
     * @param id 文章ID
     */
    void deleteArticle(Long id);

    /**
     * 统计文章总数
     * @return 文章总数
     */
    Long countTotal();

    Long saveArticle(ArticleDTO articleDTO);


    List<Map<String, Object>> getContributionData();

    List<Map<String, Object>> getCategoryCount();
}
