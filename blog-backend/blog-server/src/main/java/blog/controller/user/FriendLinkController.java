package blog.controller.user;

import blog.dto.FriendLinkDTO;
import blog.dto.FriendLinkQueryDTO;
import blog.result.Result;
import blog.service.FriendLinkService;
import blog.vo.FriendLinkVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 友链管理控制器
 *
 * @author Eleven
 * @version 1.0
 */
@RestController("userFriendlinksController")
@RequestMapping("/api/friendlinks")
@Slf4j
@ApiOperation("友链管理")
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 获取友链列表
     */
    @GetMapping
    @ApiOperation("获取友链列表")
    public Result<List<FriendLinkVO>> getFriends()
    {
        log.info("获取友链列表");

        try {
            List<FriendLinkVO> friendLinks = friendLinkService.listAllFriendLinks();
            log.info("从数据库获取到友链数量：{}", friendLinks != null ? friendLinks.size() : 0);

            if (friendLinks == null || friendLinks.isEmpty()) {
                log.warn("数据库中没有找到友链数据");
                return Result.success(new ArrayList<>());
            }

            // 只返回状态为已启用的友链
            List<FriendLinkVO> enabledLinks = friendLinks.stream()
                    .filter(link -> link.getStatus() != null && link.getStatus() == 1)
                    .sorted((l1, l2) -> {
                        // 按创建时间倒序排列
                        if (l1.getCreateTime() == null) return 1;
                        if (l2.getCreateTime() == null) return -1;
                        return l2.getCreateTime().compareTo(l1.getCreateTime());
                    })
                    .collect(Collectors.toList());

            log.info("返回已启用友链数量：{}", enabledLinks.size());
            return Result.success(enabledLinks);

        } catch (Exception e) {
            log.error("获取友链列表失败", e);
            return Result.success(new ArrayList<>());
        }
    }

}

