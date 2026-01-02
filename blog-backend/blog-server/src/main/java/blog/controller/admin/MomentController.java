package blog.controller.admin;

import blog.dto.MomentDTO;
import blog.dto.MomentQueryDTO;
import blog.result.Result;
import blog.service.MomentService;
import blog.vo.MomentVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping({"/list", "/"})
    @ApiOperation("查询动态")
    public Result<List<MomentVO>> listMoment(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status)
    {
        log.info("查询动态列表：keyword={}, status={}", keyword, status);

        List<MomentVO> moments;

        if ((keyword == null || keyword.isEmpty()) && status == null)
        {
            moments = momentService.listAllMoments();
        }
        else
        {
            MomentQueryDTO momentQueryDTO = new MomentQueryDTO();
            momentQueryDTO.setKeyword(keyword);
            momentQueryDTO.setStatus(status);
            moments = momentService.listMoments(momentQueryDTO);
        }

        return Result.success(moments);
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
