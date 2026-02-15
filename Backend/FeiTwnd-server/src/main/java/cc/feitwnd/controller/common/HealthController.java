package cc.feitwnd.controller.common;

import cc.feitwnd.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public Result<String> health() {
        return Result.success("OK, Server is Running!");
    }
}
