package blog.controller.admin;

import blog.dto.CategoryDTO;
import blog.result.Result;
import blog.service.CategoryService;
import blog.vo.CategoryVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoriesController")
@RequestMapping("/admin/categories")
@Slf4j
@ApiOperation("分类管理页面")
public class CategoryController{

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation("创建分类")
    public Result createCategory(@RequestBody CategoryDTO categoryDTO)
    {
        log.info("创建分类：{}",categoryDTO.getName());
        try
        {
            categoryService.insert(categoryDTO);
            return Result.success("分类创建成功");
        }
        catch (RuntimeException e)
        {
            log.warn("创建分类失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO)
    {
        log.info("修改分类：{}",categoryDTO);
        try
        {
            categoryService.update(categoryDTO);
            return Result.success("修改分类成功");
        }
        catch (RuntimeException e)
        {
            log.warn("修改分类失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }

    }


    @GetMapping
    @ApiOperation("查询所有分类")
    public Result<List<CategoryVO>> selectAll()
    {
        log.info("查询分类列表");
        List<CategoryVO> list = categoryService.selectAll();
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除分类")
    public Result deleteCategory(@PathVariable Long id)
    {
        log.info("删除分类：{}",id);
        categoryService.delete(id);
        return Result.success();
    }
}
