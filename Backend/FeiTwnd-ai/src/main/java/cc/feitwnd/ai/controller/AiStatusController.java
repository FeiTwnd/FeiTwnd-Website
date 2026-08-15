package cc.feitwnd.ai.controller;

import cc.feitwnd.ai.properties.AiProperties;
import cc.feitwnd.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 模块能力探测接口
 *
 * 前端编辑页据此决定是否显示"AI 生成摘要"开关：
 * 未打包 AI 模块时该接口不存在（404），前端自动隐藏开关；
 * 已打包但 feitwnd.ai.enabled=false 时返回 enabled=false，前端同样隐藏。
 */
@RestController
@RequestMapping("/admin/ai")
@RequiredArgsConstructor
public class AiStatusController {

    private final AiProperties aiProperties;

    /**
     * 查询 AI 模块状态
     * @return enabled-是否启用，modelName-当前模型名称
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", aiProperties.getEnabled());
        data.put("modelName", aiProperties.getModelName());
        return Result.success(data);
    }
}
