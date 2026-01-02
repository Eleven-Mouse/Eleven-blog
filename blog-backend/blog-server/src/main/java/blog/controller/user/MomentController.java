package blog.controller.user;

import blog.dto.MomentDTO;
import blog.dto.MomentQueryDTO;
import blog.result.Result;
import blog.service.MomentService;
import blog.vo.MomentVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController("userMomentController")
@RequestMapping ("/api/moment")
@Slf4j
@ApiOperation("动态管理页面")
public class MomentController
{
    @Autowired
    private MomentService momentService;



    /**
     * 获取动态列表（说说）
     */
    @GetMapping
    @ApiOperation("获取动态列表")
    public Result<List<MomentVO>> getMoment()
    {
        log.info("获取动态列表");

        try {
            MomentQueryDTO queryDTO = new MomentQueryDTO();
            // 只获取已发布的动态
            queryDTO.setStatus(1);

            List<MomentVO> moments = momentService.listMoments(queryDTO);
            log.info("从数据库获取到动态数量：{}", moments != null ? moments.size() : 0);

            if (moments == null || moments.isEmpty()) {
                log.warn("数据库中没有找到动态数据，尝试获取所有动态");
                // 如果按状态查询为空，尝试获取所有动态
                moments = momentService.listAllMoments();
                if (moments != null) {
                    // 过滤已发布的动态
                    moments = moments.stream()
                            .filter(moment -> moment.getStatus() != null && moment.getStatus() == 1)
                            .sorted((m1, m2) -> {
                                // 按发布时间倒序排列
                                if (m1.getPublishTime() == null) return 1;
                                if (m2.getPublishTime() == null) return -1;
                                return m2.getPublishTime().compareTo(m1.getPublishTime());
                            })
                            .collect(Collectors.toList());
                    log.info("过滤后的动态数量：{}", moments.size());
                }
            }

            return Result.success(moments != null ? moments : new ArrayList<>());
        } catch (Exception e) {
            log.error("获取动态列表失败", e);
            // 返回空列表而不是错误，避免前端报错
            return Result.success(new ArrayList<>());
        }
    }

}
