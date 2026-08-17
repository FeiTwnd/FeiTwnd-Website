package cc.feitwnd.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模块配置（feitwnd.ai.*）
 *
 * 配置来源为 application.yml 及环境变量（见 application.yml.template），
 * 秘钥必须通过环境变量注入，禁止直接写死在配置文件中。
 * 只保留模型通用配置；各功能特有的参数（如提示词、输出长度限制）随功能实现存放，不入全局配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "feitwnd.ai")
public class AiProperties {

    /** 是否启用 AI 模块（未启用时整个模块不装配，不影响应用启动） */
    private Boolean enabled = false;

    /** 模型接口地址（OpenAI 兼容协议，如 DeepSeek/通义/智谱/OpenRouter 等） */
    private String baseUrl;

    /** API 秘钥（生产环境务必通过环境变量注入） */
    private String apiKey;

    /** 模型名称 */
    private String modelName = "deepseek-chat";

    /** 采样温度，越低生成越稳定 */
    private Double temperature = 0.3;

    /** 请求超时时间（秒），AI 服务不可用时快速失败，避免长时间占用虚拟线程 */
    private Integer timeoutSeconds = 180;
}
