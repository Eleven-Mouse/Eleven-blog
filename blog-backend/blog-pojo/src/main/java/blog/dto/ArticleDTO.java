package blog.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建文章DTO
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ArticleDTO implements Serializable {

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
     * 草稿时可以为空，发布时必填
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
     * GitHub 文件 SHA
     */
    private String githubSha;

    /**
     * 是否允许评论（0-否，1-是）
     */
    private Integer isComment;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
}

