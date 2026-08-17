package cc.feitwnd.ai.service;

import cc.feitwnd.ai.prompt.TypoCorrectionPrompt;
import cc.feitwnd.ai.properties.AiProperties;
import cc.feitwnd.extension.ai.AiTypoCorrector;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * 基于 langchain4j 的 AI 错别字/病句纠错实现（OpenAI 兼容协议）
 *
 * base-url 指向任意兼容 OpenAI 协议的服务，切换模型厂商只需修改配置，无需改动代码。
 * 模型延迟到首次调用时创建：enabled=true 但未配置 api-key/base-url 时不会导致应用启动失败，
 * 仅该功能不可用（correctTypo 返回 null）。
 *
 * 提示词按功能存放于 prompt 包下的独立常量类，新增 AI 功能时新增对应常量类与实现，
 * 不与全局 yml 配置耦合，便于扩展。
 */
@Slf4j
public class AiTypoCorrectorImpl implements AiTypoCorrector {

    private final AiProperties properties;
    private volatile ChatModel chatModel;

    /**
     * 根据配置构造纠错器（模型延迟创建，提示词引用 prompt 包常量）
     * @param properties AI 模块配置
     */
    public AiTypoCorrectorImpl(AiProperties properties) {
        this.properties = properties;
    }

    /**
     * 对文章 Markdown 内容进行纠错
     * @param contentMarkdown 文章 Markdown 内容
     * @return 纠错后的完整 Markdown；生成失败、结果为空或配置缺失时返回 null，调用方保留原文
     */
    @Override
    public String correctTypo(String contentMarkdown) {
        try {
            ChatModel model = getChatModel();
            if (model == null) {
                return null;
            }

            ChatRequest request = ChatRequest.builder()
                    .messages(SystemMessage.from(TypoCorrectionPrompt.CORRECTION_PROMPT), UserMessage.from(contentMarkdown))
                    .build();
            String corrected = model.chat(request).aiMessage().text();

            if (corrected == null || corrected.isBlank()) {
                log.warn("AI 返回的纠错结果为空");
                return null;
            }
            return corrected.trim();
        } catch (Exception e) {
            // 纠错失败只记日志，绝不向上抛，保证不影响编辑主流程
            log.error("AI 纠错失败, ex={}", e.getMessage());
            return null;
        }
    }

    /**
     * 懒加载模型客户端；api-key 或 base-url 未配置时返回 null
     * @return 模型客户端，配置缺失时返回 null
     */
    private ChatModel getChatModel() {
        if (chatModel == null) {
            synchronized (this) {
                if (chatModel == null) {
                    if (properties.getApiKey() == null || properties.getApiKey().isBlank()
                            || properties.getBaseUrl() == null || properties.getBaseUrl().isBlank()) {
                        log.warn("AI 模块未配置 api-key 或 base-url，纠错功能不可用");
                        return null;
                    }
                    chatModel = OpenAiChatModel.builder()
                            .baseUrl(properties.getBaseUrl())
                            .apiKey(properties.getApiKey())
                            .modelName(properties.getModelName())
                            .temperature(properties.getTemperature())
                            .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                            .build();
                }
            }
        }
        return chatModel;
    }
}
