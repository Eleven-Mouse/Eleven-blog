package blog.mapper;

import blog.entity.Category;
import blog.vo.CategoryVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper
{
    @Insert("insert into category(name,create_time,update_time) values (#{name},#{createTime},#{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    void update(Category category);
    
    /**
     * 查询所有分类（带文章数统计）
     */
    List<CategoryVO> selectAll();

}
