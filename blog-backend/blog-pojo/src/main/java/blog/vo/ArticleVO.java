package blog.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章VO（用于前端展示）
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ArticleVO implements Serializable {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容（Markdown格式）
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 章节顺序（专题内）
     */
    private Integer chapterOrder;

    /**
     * 预计阅读分钟数
     */
    private Integer readingMinutes;

    /**
     * 是否核心文章（0-否，1-是）
     */
    private Integer isCore;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 浏览次数
     */
    private Integer viewCount;


    /**
     * GitHub Markdown 文件地址
     */
    private String githubUrl;

    /**
     * 同步状态：0-未配置，1-成功，2-失败
     */
    private Integer syncStatus;

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncTime;

    /**
     * 是否允许评论（0-否，1-是）
     */
    private Integer isComment;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

