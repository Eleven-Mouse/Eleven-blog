package blog.mapper;

import blog.entity.SystemConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统配置Mapper接口
 *
 * @author Eleven
 * @version 1.0
 */
@Mapper
public interface SystemConfigMapper {

    /**
     * 查询所有配置
     */
    @Select("SELECT * FROM system_config")
    List<SystemConfig> selectAll();

    /**
     * 根据key查询配置
     */
    @Select("SELECT * FROM system_config WHERE config_key = #{key}")
    SystemConfig selectByKey(String key);

    /**
     * 插入配置
     */
    @Insert("INSERT INTO system_config(config_key, config_value, config_type, description, create_time, update_time) " +
            "VALUES(#{configKey}, #{configValue}, #{configType}, #{description}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SystemConfig config);

    /**
     * 根据key更新配置
     */
    void updateByKey(SystemConfig config);

    /**
     * 根据key删除配置
     */
    @Delete("DELETE FROM system_config WHERE config_key = #{key}")
    void deleteByKey(String key);
}
