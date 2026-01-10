package blog.controller.admin;

import blog.dto.CategoryDTO;
import blog.dto.TagsDTO;
import blog.result.Result;
import blog.service.TagsService;
import blog.vo.CategoryVO;
import blog.vo.TagsVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminTagsController")
@RequestMapping("/admin/tags")
@Slf4j
@ApiOperation("标签管理页面")
public class TagsController
{
    @Autowired
    private TagsService tagsService;

    @PostMapping
    @ApiOperation("创建标签")
    public Result createTags(@RequestBody TagsDTO tagsDTO)
    {
        log.info("创建标签：{}", tagsDTO.getName());
        try
        {
            tagsService.insertTags(tagsDTO);
            return Result.success("标签创建成功");
        }
        catch (RuntimeException e)
        {
            log.warn("创建标签失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    @ApiOperation("修改标签")
    public Result updateTag(@RequestBody TagsDTO tagsDTO)
    {
        log.info("修改标签：{}", tagsDTO);
        try
        {
            tagsService.update(tagsDTO);
            return Result.success("标签修改成功");
        }
        catch (RuntimeException e)
        {
            log.warn("修改标签失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }




    @GetMapping("/list")
    @ApiOperation("查询所有标签")
    public Result<List<TagsVO>> selectAll()
    {
        log.info("查询标签列表");
        List<TagsVO> list = tagsService.selectAll();
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除标签")
    public Result deleteTags(@PathVariable Long id)
    {
        log.info("删除分类：{}",id);
        tagsService.deleteTag(id);
        return Result.success();
    }


}
