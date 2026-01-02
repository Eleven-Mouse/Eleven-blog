package blog.mapper;

import blog.entity.FriendLink;
import blog.vo.FriendLinkVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 友链Mapper接口
 * 
 * @author Eleven
 * @version 1.0
 */
@Mapper
public interface FriendLinkMapper
{
    
    /**
     * 插入友链
     */
    @Insert("INSERT INTO friend_link(name, url, description, logo,  status, view_count, create_time, update_time) " +
            "VALUES(#{name}, #{url}, #{description}, #{logo},#{status},#{viewCount}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(FriendLink friendLink);
    
    /**
     * 根据ID查询友链
     */
    @Select("SELECT * FROM friend_link WHERE id = #{id}")
    FriendLinkVO selectById(Long id);
    
    /**
     * 查询所有友链
     */
    @Select("SELECT * FROM friend_link")
    List<FriendLinkVO> selectAll();
    
    /**
     * 根据名称模糊查询友链
     */
    @Select("SELECT * FROM friend_link WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<FriendLinkVO> selectByName(String name);
    
    /**
     * 根据状态查询友链
     */
    @Select("SELECT * FROM friend_link WHERE status = #{status}")
    List<FriendLinkVO> selectByStatus(Integer status);
    
    /**
     * 更新友链
     */
    void update(FriendLink friendLink);
    
    /**
     * 根据ID删除友链
     */
    @Delete("DELETE FROM friend_link WHERE id = #{id}")
    void deleteById(Long id);
    
    /**
     * 增加浏览次数
     */
    @Update("UPDATE friend_link SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);
    
    /**
     * 统计友链总数
     */
    @Select("SELECT COUNT(*) FROM friend_link")
    Long countTotal();
}

