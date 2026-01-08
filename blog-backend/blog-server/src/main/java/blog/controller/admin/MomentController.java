package blog.controller.admin;

import blog.dto.MomentDTO;
import blog.dto.MomentQueryDTO;
import blog.result.Result;
import blog.service.MomentService;
import blog.vo.MomentVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController("adminMomentController")
@RequestMapping ("/admin/moment")
@Slf4j
@ApiOperation("动态管理页面")
public class MomentController
{
    @Autowired
    private MomentService momentService;



    @PostMapping
    @ApiOperation("创建动态")
    public Result creatMoment(@RequestBody MomentDTO momentDTO)
    {
        log.info("创建动态：{}", momentDTO);
        momentService.createMoment(momentDTO);
        return Result.success();
    }

    /**
     * 获取动态列表
     */
    @GetMapping
    @ApiOperation("获取动态列表")
    public Result<PageInfo<MomentVO>> getMoment(@RequestParam(defaultValue = "1") int page,
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


    @GetMapping("/{id}")
    @ApiOperation("根据ID查询动态")
    public Result<MomentVO> getMoment(@PathVariable Long id)
    {
        log.info("查询动态详情，ID：{}", id);
        MomentVO moment = momentService.getMomentById(id);
        if (moment == null) {
            return Result.error("动态不存在");
        }
        return Result.success(moment);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除动态")
    public Result<Void> deleteMoment(@PathVariable Long id)
    {
        log.info("删除动态：{}", id);
        momentService.deleteMoment(id);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("更新动态")
    public Result updateMoment(@PathVariable Long id, @RequestBody MomentDTO momentDTO)
    {
        log.info("更新动态，ID：{}，数据：{}", id, momentDTO);

        momentService.updateMoment(id.intValue(), momentDTO);

        return Result.success();
    }







}
