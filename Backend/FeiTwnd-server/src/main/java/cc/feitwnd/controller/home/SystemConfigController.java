package cc.feitwnd.controller.home;

import cc.feitwnd.entity.SystemConfig;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("homeSystemConfigController")
@RequestMapping("/home/systemConfig")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 根据配置键获取配置
     * @param configKey
     * @return
     */
    @GetMapping("/key/{configKey}")
    public Result<SystemConfig> getByKey(@PathVariable String configKey) {
        log.info("根据配置键获取配置,{}", configKey);
        SystemConfig systemConfig = systemConfigService.getByKey(configKey);
        return Result.success(systemConfig);
    }
}
