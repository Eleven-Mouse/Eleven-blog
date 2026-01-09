package blog.controller.user;

import blog.dto.CommentDTO;
import blog.dto.CommentQueryDTO;
import blog.result.Result;
import blog.service.CommentService;
import blog.vo.CommentVO;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论管理控制器
 *
 * @author Eleven
 * @version 1.0
 */
@RestController("userCommentsController")
@RequestMapping("/api/comments")
@Slf4j
@ApiOperation("评论管理")
public class CommentController {

    @Autowired
    private CommentService commentService;


    /**
     *   根据页面或文章id查询所有评论
     * 	 @param page     要查询的页面(博客文章or友链...)
     * 	 @param blogId   如果是博客文章页面 需要提供博客id
     */
    @GetMapping
    @ApiOperation("查询评论详情")
    public Result<List<CommentVO>> getComments(@RequestParam(required = false) String page,
                                               @RequestParam(required = false) Long blogId)
    {
        log.info("查询评论详情，ID：{}", blogId);

        List<CommentVO> comments = commentService.getComments(page,blogId,-1L);

        return Result.success(comments);
    }

    /**
     * 提交评论
     */
    @PostMapping("/comment")
    @ApiOperation("提交评论")
    public Result postComment(
            @RequestBody CommentDTO commentDTO,
            jakarta.servlet.http.HttpServletRequest request)
    {

        try {
            // 验证必填参数
            String content = commentDTO.getContent();
            String nickname = commentDTO.getNickname();

            if (content == null || content.trim().isEmpty()) {
                return Result.error("评论内容不能为空");
            }
            if (nickname == null || nickname.trim().isEmpty()) {
                return Result.error("昵称不能为空");
            }

            // 内容长度验证
            if (content.trim().length() > 1000) {
                return Result.error("评论内容不能超过1000字符");
            }
            if (nickname.trim().length() > 50) {
                return Result.error("昵称不能超过50字符");
            }

            // 创建评论DTO
            commentDTO.setNickname(nickname.trim());
            commentDTO.setContent(content.trim());
            commentDTO.setEmail(commentDTO.getEmail() != null ? commentDTO.getEmail() .trim() : "");
            commentDTO.setWebsite(commentDTO.getWebsite() != null ? commentDTO.getWebsite() .trim() : "");
            // 默认公开
            commentDTO.setStatus(true);
            // 获取真实用户IP
            String clientIp = getClientIpAddress(request);
            commentDTO.setIp(clientIp);



            commentService.createComment(commentDTO);


        } catch (Exception e) {
            log.error("提交评论失败，错误：{}", e.getMessage(), e);
            return Result.error("评论提交失败，请稍后重试");
        }
        return Result.success();
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String xRealIP = request.getHeader("X-Real-IP");
        String remoteAddr = request.getRemoteAddr();

        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For可能包含多个IP，取第一个
            return xForwardedFor.split(",")[0].trim();
        }
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }
        return remoteAddr != null ? remoteAddr : "unknown";
    }
}

