package cc.feitwnd.service.impl;

import cc.feitwnd.entity.SystemConfig;
import cc.feitwnd.exception.BaseException;
import cc.feitwnd.mapper.SystemConfigMapper;
import cc.feitwnd.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    /**
     * 获取所有系统配置
     * @return
     */
    @Override
    public List<SystemConfig> listAll() {
        return systemConfigMapper.listAll();
    }

    /**
     * 根据配置键获取配置
     * @param configKey
     * @return
     */
    @Override
    public SystemConfig getByKey(String configKey) {
        return systemConfigMapper.getByKey(configKey);
    }

    /**
     * 根据ID获取配置
     * @param id
     * @return
     */
    @Override
    public SystemConfig getById(Long id) {
        return systemConfigMapper.getById(id);
    }

    /**
     * 添加系统配置
     * @param systemConfig
     */
    @Override
    public void addConfig(SystemConfig systemConfig) {
        // 检查配置键是否已存在
        SystemConfig existingConfig = systemConfigMapper.getByKey(systemConfig.getConfigKey());
        if (existingConfig != null) {
            throw new BaseException("配置键已存在");
        }
        systemConfigMapper.insert(systemConfig);
    }

    /**
     * 更新系统配置
     * @param systemConfig
     */
    @Override
    public void updateConfig(SystemConfig systemConfig) {
        systemConfigMapper.update(systemConfig);
    }

    /**
     * 删除系统配置
     * @param id
     */
    @Override
    public void deleteConfig(Long id) {
        systemConfigMapper.deleteById(id);
    }
}
