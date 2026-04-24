package blog.controller.user;

import blog.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 访客公开上传接口（头像）
 * 不需要管理员权限，任何访客可上传头像
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class VisitorUploadController {

    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    /**
     * 访客上传头像
     * 限制: 仅允许图片, 最大 2MB
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 限制文件大小 2MB
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error("头像文件不能超过2MB");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("仅支持上传图片文件");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        // 存到 avatar 子目录，与管理员上传的图片隔离
        String fileName = "avatar_" + UUID.randomUUID().toString().substring(0, 8) + suffix;
        String folderPath = uploadDir.endsWith("/") || uploadDir.endsWith("\\")
                ? uploadDir : uploadDir + File.separator;
        File avatarDir = new File(folderPath + "avatar");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }

        File dest = new File(avatarDir, fileName);
        try {
            file.transferTo(dest);
            String imageUrl = "/images/avatar/" + fileName;
            log.info("访客头像上传成功: {}", imageUrl);
            return Result.success(imageUrl);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
