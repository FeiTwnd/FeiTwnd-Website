package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.SystemConfig;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    /**
     * 获取所有系统配置
     * @return
     */
    @Select("select * from system_config order by id desc")
    List<SystemConfig> listAll();

    /**
     * 根据配置键获取配置
     * @param configKey
     * @return
     */
    @Select("select * from system_config where config_key = #{configKey}")
    SystemConfig getByKey(String configKey);

    /**
     * 根据ID获取配置
     * @param id
     * @return
     */
    @Select("select * from system_config where id = #{id}")
    SystemConfig getById(Long id);

    /**
     * 添加系统配置
     * @param systemConfig
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(SystemConfig systemConfig);

    /**
     * 更新系统配置
     * @param systemConfig
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(SystemConfig systemConfig);

    /**
     * 删除系统配置
     * @param id
     */
    @Delete("delete from system_config where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除系统配置
     * @param ids
     */
    void batchDelete(List<Long> ids);
}
