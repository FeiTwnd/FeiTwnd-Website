package cc.feitwnd.controller.common;

import cc.feitwnd.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

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
        Thread current = Thread.currentThread();

        return Result.success(String.format("""
            FeiTwnd博客后端状态
            ===================
            状态: 运行正常
            端口: 8080
            线程信息:
              - 名称: %s
              - 类型: %s
              - ID: %d
              - 是否虚拟线程: %s
            """,
                current.getName(),
                current.isVirtual() ? "虚拟线程" : "平台线程",
                current.threadId(),
                current.isVirtual()
        ));
    }
}
