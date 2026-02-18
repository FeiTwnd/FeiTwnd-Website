package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.dto.SystemConfigDTO;
import cc.feitwnd.entity.SystemConfig;
import cc.feitwnd.exception.BaseException;
import cc.feitwnd.exception.SystemConfigException;
import cc.feitwnd.mapper.SystemConfigMapper;
import cc.feitwnd.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    /**
     * 获取所有系统配置
     * @return
     */
    public List<SystemConfig> listAll() {
        return systemConfigMapper.listAll();
    }

    /**
     * 根据配置键获取配置
     * @param configKey
     * @return
     */
    public SystemConfig getByKey(String configKey) {
        return systemConfigMapper.getByKey(configKey);
    }

    /**
     * 根据ID获取配置
     * @param id
     * @return
     */
    public SystemConfig getById(Long id) {
        return systemConfigMapper.getById(id);
    }

    /**
     * 添加系统配置
     * @param systemConfigDTO
     */
    public void addConfig(SystemConfigDTO systemConfigDTO) {
        // 检查配置键是否已存在
        SystemConfig existingConfig = systemConfigMapper.getByKey(systemConfigDTO.getConfigKey());
        if (existingConfig != null) {
            throw new SystemConfigException(MessageConstant.ConfigKeyExists);
        }
        SystemConfig systemConfig = new SystemConfig();
        BeanUtils.copyProperties(systemConfigDTO, systemConfig);
        systemConfigMapper.insert(systemConfig);
    }

    /**
     * 更新系统配置
     * @param systemConfigDTO
     */
    public void updateConfig(SystemConfigDTO systemConfigDTO) {
        SystemConfig systemConfig = new SystemConfig();
        BeanUtils.copyProperties(systemConfigDTO, systemConfig);
        systemConfigMapper.update(systemConfig);
    }

    /**
     * 批量删除系统配置
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        systemConfigMapper.batchDelete(ids);
    }
}
