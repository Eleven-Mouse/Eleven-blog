package blog.mapper;

import blog.dto.MomentDTO;
import blog.entity.Moment;
import blog.vo.MomentVO;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface MomentMapper
{
    @Insert("INSERT INTO moment(content, image, status, publish_time, create_time, update_time) " +
            "VALUES(#{content}, #{image}, #{status}, " +
            "CASE WHEN #{status} = 1 THEN NOW() ELSE NULL END, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MomentDTO momentDTO);

    @Select("SELECT * FROM moment WHERE id = #{id}")
    MomentVO selectById(Long id);
    
    @Select("SELECT * FROM moment")
    List<MomentVO> selectAll();

    @Select("SELECT * FROM moment WHERE content LIKE CONCAT('%', #{keyword}, '%') AND status != 2 ORDER BY create_time DESC")
    List<MomentVO> selectByKeyword(String keyword);

    @Select("SELECT * FROM moment WHERE status = #{status} ORDER BY create_time DESC")
    List<MomentVO> selectByStatus(Integer status);

    @Delete("delete from moment WHERE id = #{id}")
    void deleteById(Long id);

    void update(Moment moment);
    
    /**
     * 统计动态总数
     */
    @Select("SELECT COUNT(*) FROM moment")
    Long countTotal();
}
