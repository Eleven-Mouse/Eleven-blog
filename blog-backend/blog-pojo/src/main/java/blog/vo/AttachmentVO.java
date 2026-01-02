package blog.vo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 附件VO类
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AttachmentVO {

    /**
     * 附件ID
     */
    private Long id;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 存储文件名
     */
    private String storedName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 格式化的文件大小
     */
    private String fileSizeFormatted;

    /**
     * 上传时间
     */
    private LocalDateTime createTime;
}














