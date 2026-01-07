package blog.mapper;

import blog.entity.Tags;
import blog.vo.TagsVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TagsMapper
{
    @Insert("insert into tags(name,create_time,update_time) values(#{name},NOW(),NOW())")
    void insert(Tags tags);

    @Delete("delete from tags where id = #{id}")
    void delete(Long id);


    @Select("select count(*) from tags where name = #{name}")
    int countByName(String name);

    @Select("select count(*) from tags where name = #{name} and id != #{id}")
    int countByNameExcludeId(String name, Long id);

    void update(Tags tags);

    List<Tags> selectByIds(List<Long> ids);

    List<TagsVO> selectTagListWithCount();

    List<Map<String, Object>> getTagStatistics();
}
