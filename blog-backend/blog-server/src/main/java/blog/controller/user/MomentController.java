package blog.controller.user;


import blog.dto.MomentQueryDTO;
import blog.result.Result;
import blog.service.MomentService;
import blog.vo.MomentVO;
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
    public Result<List<MomentVO>> getMoment(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("获取动态列表 - 第{}页，每页{}条", page, size);

        try {
            MomentQueryDTO queryDTO = new MomentQueryDTO();
            queryDTO.setStatus(1); // 只查询已发布的

            // 1. 【核心修复】开启分页
            // 这一行代码会自动拦截下面的一条 SQL 查询，加上 limit offset
            PageHelper.startPage(page, size, "publish_time desc");

            // 2. 执行查询
            // 不需要再去 listAllMoments，如果这里查不到，那就是真没有
            List<MomentVO> moments = momentService.listMoments(queryDTO);

            // 3. 【核心修复】确保返回空数组而不是 null
            // 如果 moments 为 null，返回空集合
            if (moments == null) {
                moments = Collections.emptyList();
            }

            log.info("查询成功，本页数量：{}", moments.size());

            // 4. 返回结果
            return Result.success(moments);

        } catch (Exception e) {
            log.error("获取动态列表异常", e);
            // 发生异常时也返回空数组，保证前端不报错
            return Result.success(Collections.emptyList());
        }
    }
}
