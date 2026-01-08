package blog.controller.user;


import blog.dto.MomentQueryDTO;
import blog.result.Result;
import blog.service.MomentService;
import blog.vo.MomentVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageHelper;

import java.util.Collections;
import java.util.List;


@RestController("userMomentController")
@RequestMapping ("/api/moments")
@Slf4j
@ApiOperation("动态管理页面")
public class MomentController
{
    @Autowired
    private MomentService momentService;



    /**
     * 获取动态列表
     */
    @GetMapping
    @ApiOperation("获取动态列表")
    public Result<PageInfo<MomentVO>> getMoments(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("获取动态列表 - 第{}页，每页{}条", page, size);

        try {
            PageHelper.startPage(page, size, "publish_time desc");
            List<MomentVO> moments = momentService.listAllMoments();
            PageInfo<MomentVO> pageInfo = new PageInfo<>(moments);


            log.info("查询成功，当前页数量：{}，总数量：{}", pageInfo.getSize(), pageInfo.getTotal());

            return Result.success(pageInfo);

        } catch (Exception e) {
            log.error("获取动态列表异常", e);
            // 发生异常时也返回空数组，保证前端不报错
            return Result.success(new PageInfo<>(Collections.emptyList()));
        }
    }
}
