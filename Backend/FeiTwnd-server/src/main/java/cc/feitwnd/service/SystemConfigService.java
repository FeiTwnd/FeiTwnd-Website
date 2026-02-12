package cc.feitwnd.service;

import cc.feitwnd.entity.SystemConfig;

import java.util.List;

import java.util.List;

public interface SystemConfigService {
    /**
     * 获取所有系统配置
     * @return
     */
    List<SystemConfig> listAll();

    /**
     * 根据配置键获取配置
     * @param configKey
     * @return
     */
    SystemConfig getByKey(String configKey);

    /**
     * 根据ID获取配置
     * @param id
     * @return
     */
    SystemConfig getById(Long id);

    /**
     * 添加系统配置
     * @param systemConfig
     */
    void addConfig(SystemConfig systemConfig);

    /**
     * 更新系统配置
     * @param systemConfig
     */
    void updateConfig(SystemConfig systemConfig);

    /**
     * 批量删除系统配置
     * @param ids
     */
    void batchDelete(List<Long> ids);
}
