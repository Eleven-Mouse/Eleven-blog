package blog.mapper;

import blog.entity.Category;
import blog.vo.CategoryVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper
{
    @Insert("insert into category(name, slug, description, cover_image, sort_order, create_time, update_time) " +
            "values (#{name}, #{slug}, #{description}, #{coverImage}, #{sortOrder}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    void update(Category category);

    /**
     * 查询所有分类（带文章数统计）
     */
    List<CategoryVO> selectAll();

    /**
     * 根据名称查询分类
     */
    @Select("SELECT * FROM category WHERE name = #{name} LIMIT 1")
    Category selectByName(String name);

    /**
     * 根据ID查询分类
     */
    @Select("SELECT id, name, slug, description, cover_image as coverImage, sort_order as sortOrder, " +
            "create_time as createTime, update_time as updateTime FROM category WHERE id = #{id}")
    Category selectById(Long id);

}
