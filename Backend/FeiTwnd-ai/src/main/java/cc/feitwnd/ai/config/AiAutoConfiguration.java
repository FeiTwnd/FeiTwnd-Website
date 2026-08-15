package cc.feitwnd.ai.config;

import cc.feitwnd.ai.properties.AiProperties;
import cc.feitwnd.ai.service.AiSummaryGeneratorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 模块自动装配
 *
 * 仅当 feitwnd.ai.enabled=true 时注册摘要生成器 Bean；
 * 未启用或配置缺失时本配置类不生效，应用照常启动（fail-safe）。
 */
@Configuration
@ConditionalOnProperty(prefix = "feitwnd.ai", name = "enabled", havingValue = "true")
public class AiAutoConfiguration {

    /**
     * 注册 AI 摘要生成器
     * @param aiProperties AI 模块配置
     * @return 摘要生成器实现
     */
    @Bean
    public AiSummaryGeneratorImpl aiSummaryGenerator(AiProperties aiProperties) {
        return new AiSummaryGeneratorImpl(aiProperties);
    }
}
