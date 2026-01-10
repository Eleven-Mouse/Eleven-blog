package blog.controller.admin;

import blog.dto.CommentDTO;
import blog.dto.CommentQueryDTO;
import blog.result.Result;
import blog.service.CommentService;
import blog.vo.CommentVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理控制器
 *
 * @author Eleven
 * @version 1.0
 */
@RestController("adminCommentsController")
@RequestMapping("/admin/comments")
@Slf4j
@ApiOperation("评论管理")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 更新评论公开状态
     *
     * @param id        评论id
     * @param status   是否公开
     * @return
     */
    @ApiOperation("更新评论公开状态")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Boolean status) {
        commentService.updateStatus(id, status);
        return Result.success("操作成功");
    }


    /**
     * 按id删除该评论,且一并所有子评论
     * 评论id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除评论")
    public Result deleteCommentById(@PathVariable Long id)
    {
        log.info("删除评论，ID：{}", id);
        commentService.deleteCommentById(id);
        return Result.success("评论删除成功");
    }


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



}

