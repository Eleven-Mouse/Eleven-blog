package blog.controller.user;

import blog.dto.ArticleQueryDTO;
import blog.dto.CommentDTO;
import blog.dto.CommentQueryDTO;
import blog.dto.MomentQueryDTO;
import blog.result.Result;
import blog.service.ArticleService;
import blog.service.CategoryService;
import blog.service.CommentService;
import blog.service.FriendLinkService;
import blog.service.MomentService;
import blog.service.TagsService;
import blog.vo.ArticleVO;
import blog.vo.CategoryVO;
import blog.vo.CommentVO;
import blog.vo.FriendLinkVO;
import blog.vo.MomentVO;
import blog.vo.TagsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公共API控制器（/api前缀）
 * 用于处理前端直接请求的API路径
 *
 * @author Eleven
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class PublicController {




    /**
     * 处理favicon.ico请求（避免405错误）
     */
    @GetMapping("/favicon.ico")
    public void favicon(jakarta.servlet.http.HttpServletResponse response) {
        response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * 处理OPTIONS预检请求（冗余，主要由nginx处理）
     */
    @RequestMapping(value = {"/**"}, method = RequestMethod.OPTIONS)
    public Result<String> handleOptions()
    {
        log.debug("处理OPTIONS预检请求");
        return Result.success("OK");
    }
}
