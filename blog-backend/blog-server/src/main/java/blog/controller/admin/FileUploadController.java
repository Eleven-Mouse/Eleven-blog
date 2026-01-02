package blog.controller.admin;

import blog.dto.ArticleDTO;
import blog.entity.Article;
import blog.result.Result;
import blog.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/admin/upload")
@Api(tags = "文章上传管理")
public class FileUploadController
{
    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    @Autowired
    private ArticleService articleService;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    @ApiOperation("上传图片")

    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file)
    {
        log.info("上传图片：{}", file.getOriginalFilename());


        if (file.isEmpty())
        {
            return Result.error("文件不能为空");
        }


        String originalFilename = file.getOriginalFilename();

        // 验证文件类型（只允许图片）
        String extension = getFileExtension(originalFilename);
        if (!isImageFile(extension))
        {
            return Result.error("只支持图片格式（jpg、jpeg、png、gif、webp）");
        }

        // 验证文件大小（限制10MB）
        if (file.getSize() > 10 * 1024 * 1024)
        {
            return Result.error("图片大小不能超过10MB");
        }

        try
        {

            String uuid = UUID.randomUUID().toString().replace("-", "");
            String newFilename = uuid + "." + extension;

            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

            File dir = new File(uploadDir, datePath);
            if (!dir.exists()) {
                boolean success = dir.mkdirs();
                log.info("文件夹不存在，创建结果: {}, 路径: {}", success, dir.getAbsolutePath());
            }


            // 3. 构建最终保存的文件对象，并使用绝对路径
            File dest = new File(dir, newFilename).getAbsoluteFile();

            log.info("准备保存文件到: {}", dest.getAbsolutePath());

            file.transferTo(dest);

            String url = "/" + datePath.replace("\\", "/") + "/" + newFilename;


            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", newFilename);
            result.put("size", file.getSize());

            log.info("图片上传成功：{}", url);
            return Result.success(result);

        }
        catch (IOException e)
        {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }


    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String extension) {
        return "jpg".equals(extension) ||
               "jpeg".equals(extension) ||
               "png".equals(extension) ||
               "gif".equals(extension) ||
               "webp".equals(extension);
    }

    /**
     * 上传Markdown文件并自动创建文章
     */
    @PostMapping("/markdown")
    @ApiOperation("上传Markdown文章")
    public Result<Map<String, Object>> uploadMarkdown(@RequestParam("file") MultipartFile file)
    {
        log.info("上传Markdown文件：{}", file.getOriginalFilename());


        if (file.isEmpty())
        {
            return Result.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null)
        {
            return Result.error("文件名不能为空");
        }

        // 验证文件类型（只允许.md文件）
        String extension = getFileExtension(originalFilename);
        if (!"md".equalsIgnoreCase(extension) && !"markdown".equalsIgnoreCase(extension))
        {
            return Result.error("只支持Markdown格式文件（.md 或 .markdown）");
        }

        // 验证文件大小（限制10MB）
        if (file.getSize() > 10 * 1024 * 1024)
        {
            return Result.error("文件大小不能超过10MB");
        }

        try
        {
            // 读取Markdown文件内容
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);


            String title = originalFilename.substring(0, originalFilename.lastIndexOf('.'));

            // 生成摘要（取前200个字符）
            String summary = generateSummary(content, 200);


            ArticleDTO articleDTO = new ArticleDTO();
            articleDTO.setTitle(title);
            articleDTO.setContent(content);
            articleDTO.setSummary(summary);
            articleDTO.setStatus(0);
            articleDTO.setIsComment(1);


            Long articleId = articleService.saveArticle(articleDTO);

            Map<String, Object> result = new HashMap<>();
            result.put("articleId", articleId);
            result.put("title", title);
            result.put("content", content);
            result.put("contentLength", content.length());
            result.put("summary", summary);
            result.put("status", 0);
            result.put("message", "Markdown文章上传成功，请选择分类和标签后保存");

            log.info("Markdown文章创建成功，文章ID：{}, 标题：{}", articleId, title);
            return Result.success(result);

        }
        catch (IOException e)
        {
            log.error("读取Markdown文件失败", e);
            return Result.error("读取文件失败：" + e.getMessage());
        }
        catch (Exception e)
        {
            log.error("创建文章失败", e);
            return Result.error("创建文章失败：" + e.getMessage());
        }
    }

    /**
     * 获取图片列表
     */
    @GetMapping("/images")
    @ApiOperation("获取图片列表")
    public Result<List<Map<String, Object>>> getImageList()
    {
        log.info("获取图片列表");

        try
        {
            // 这里应该从数据库或文件系统获取图片列表
            // 暂时返回空列表
            List<Map<String, Object>> images = new ArrayList<>();
            return Result.success(images);
        }
        catch (Exception e)
        {
            log.error("获取图片列表失败", e);
            return Result.error("获取图片列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/images/{filename}")
    @ApiOperation("删除图片")
    public Result<Void> deleteImage(@PathVariable String filename)
    {
        log.info("删除图片：{}", filename);

        try
        {
            // 这里应该实现删除图片的逻辑
            // 暂时只记录日志
            return Result.success("图片删除成功");
        }
        catch (Exception e)
        {
            log.error("删除图片失败", e);
            return Result.error("删除图片失败：" + e.getMessage());
        }
    }

    /**
     * 生成文章摘要
     */
    private String generateSummary(String content, int maxLength)
    {
        if (content == null || content.isEmpty())
        {
            return "";
        }

        // 移除Markdown标记
        String plainText = content
            .replaceAll("#{1,6}\\s+", "")
            .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
            .replaceAll("\\*(.+?)\\*", "$1")
            .replaceAll("\\[(.+?)\\]\\(.+?\\)", "$1")
            .replaceAll("!\\[.*?\\]\\(.*?\\)", "")
            .replaceAll("`(.+?)`", "$1")
            .replaceAll("```[\\s\\S]*?```", "")
            .replaceAll("^>\\s+", "")
            .replaceAll("^[-*+]\\s+", "")
            .replaceAll("\\n+", " ")
            .trim();

        // 截取指定长度
        if (plainText.length() > maxLength)
        {
            return plainText.substring(0, maxLength) + "...";
        }

        return plainText;
    }
}
