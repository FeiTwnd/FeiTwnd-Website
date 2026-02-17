package cc.feitwnd.controller.common;

import cc.feitwnd.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {
    /**
     * 健康检查
     */
    @GetMapping
    public Result<String> health() {
        return Result.success("Server is running");
    }
}
