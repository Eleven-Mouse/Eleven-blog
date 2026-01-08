package blog.controller.admin;

import blog.dto.FriendLinkDTO;
import blog.dto.FriendLinkQueryDTO;
import blog.result.Result;
import blog.service.FriendLinkService;
import blog.vo.FriendLinkVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 友链管理控制器
 *
 * @author Eleven
 * @version 1.0
 */
@RestController("adminFriendlinksController")
@RequestMapping("/admin/friendlinks")
@Slf4j
@ApiOperation("友链管理")
public class FriendLinkController
{

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 创建友链
     */
    @PostMapping
    @ApiOperation("创建友链")
    public Result<Void> createFriendLink(@RequestBody FriendLinkDTO friendLinkDTO)
    {
        log.info("创建友链：{}", friendLinkDTO.getName());

        try
        {
            // 验证必填字段
            if (friendLinkDTO.getName() == null || friendLinkDTO.getName().trim().isEmpty())
            {
                return Result.error("友链名称不能为空");
            }
            if (friendLinkDTO.getUrl() == null || friendLinkDTO.getUrl().trim().isEmpty())
            {
                return Result.error("友链地址不能为空");
            }

            friendLinkService.createFriendLink(friendLinkDTO);
            return Result.success("友链创建成功");
        }
        catch (RuntimeException e)
        {
            log.warn("创建友链失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新友链
     */
    @PutMapping
    @ApiOperation("更新友链")
    public Result updateFriendLink(@RequestBody FriendLinkDTO friendLinkDTO) {
        log.info("更新友链：{}", friendLinkDTO);

        try
        {
            // 验证必填字段
            if (friendLinkDTO.getName() == null || friendLinkDTO.getName().trim().isEmpty())
            {
                return Result.error("友链名称不能为空");
            }
            if (friendLinkDTO.getUrl() == null || friendLinkDTO.getUrl().trim().isEmpty())
            {
                return Result.error("友链地址不能为空");
            }

            friendLinkService.updateFriendLink(friendLinkDTO);
            return Result.success("友链更新成功");
        }
        catch (RuntimeException e)
        {
            log.warn("更新友链失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除友链
     */
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除友链")
    public Result<Void> deleteFriendLink(@PathVariable Long id)
    {
        log.info("删除友链，ID：{}", id);

        friendLinkService.deleteFriendLink(id);

        return Result.success("友链删除成功");

    }

    /**
     * 根据ID查询友链
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询友链详情")
    public Result<FriendLinkVO> getFriendLink(@PathVariable Long id)
    {
        log.info("查询友链详情，ID：{}", id);

        FriendLinkVO friendLink = friendLinkService.getFriendLinkById(id);

        return Result.success(friendLink);

    }

    /**
     * 查询所有友链
     */
    @GetMapping
    @ApiOperation("查询友链")
    public Result<List<FriendLinkVO>> listFriendLinks(FriendLinkQueryDTO queryDTO)
    {
        log.info("查询友链列表");

        List<FriendLinkVO> friendLinks;
        if (queryDTO != null && (queryDTO.getName() != null || queryDTO.getStatus() != null))
        {
                friendLinks = friendLinkService.listFriendLinks(queryDTO);
        }
        else
        {
                friendLinks = friendLinkService.listAllFriendLinks();
        }

        return Result.success(friendLinks);

    }
}

