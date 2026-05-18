package blog.controller.admin;

import blog.result.Result;
import blog.service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 评论管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/comments")
@Slf4j
@ApiOperation("评论管理（管理端）")
public class CommentAdminController {

    private final CommentService commentService;

    public CommentAdminController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 评论置顶/取消置顶
     */
    @PutMapping("/{id}/pin")
    @ApiOperation("评论置顶/取消置顶")
    public Result pinComment(@PathVariable Long id,
                             @RequestParam(defaultValue = "true") Boolean pinned) {
        try {
            commentService.pinComment(id, pinned);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("评论置顶操作失败，id={}, pinned={}", id, pinned, e);
            return Result.error("评论置顶操作失败");
        }
    }
}

