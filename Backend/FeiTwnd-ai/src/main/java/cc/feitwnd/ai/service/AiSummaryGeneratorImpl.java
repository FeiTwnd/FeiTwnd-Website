package cc.feitwnd.ai.service;

import cc.feitwnd.ai.prompt.ArticleSummaryPrompt;
import cc.feitwnd.ai.properties.AiProperties;
import cc.feitwnd.extension.ai.AiSummaryGenerator;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * 基于 langchain4j 的 AI 摘要生成实现（OpenAI 兼容协议）
 *
 * base-url 指向任意兼容 OpenAI 协议的服务，切换模型厂商只需修改配置，无需改动代码。
 * 模型延迟到首次调用时创建：enabled=true 但未配置 api-key/base-url 时不会导致应用启动失败，
 * 仅该功能不可用（generateSummary 返回 null）。
 *
 * 提示词按功能存放于 prompt 包下的独立常量类，新增 AI 功能时新增对应常量类与实现，
 * 不与全局 yml 配置耦合，便于扩展。
 */
@Slf4j
public class AiSummaryGeneratorImpl implements AiSummaryGenerator {

    /** 发送给模型的文章内容最大字符数，超出截断，防止超出模型上下文窗口（包私有便于单元测试引用） */
    static final int MAX_CONTENT_CHARS = 12000;

    private final AiProperties properties;
    private volatile ChatModel chatModel;

    /**
     * 根据配置构造生成器（模型延迟创建，提示词引用 prompt 包常量）
     * @param properties AI 模块配置
     */
    public AiSummaryGeneratorImpl(AiProperties properties) {
        this.properties = properties;
    }

    /**
     * 供单元测试注入 mock 模型使用（包私有）
     * @param properties AI 模块配置
     * @param chatModel  模型客户端
     */
    AiSummaryGeneratorImpl(AiProperties properties, ChatModel chatModel) {
        this.properties = properties;
        this.chatModel = chatModel;
    }

    /**
     * 生成文章摘要
     * @param title            文章标题
     * @param contentMarkdown  文章 Markdown 内容
     * @return 摘要文本；生成失败、结果为空或配置缺失时返回 null，由调用方决定是否回填
     */
    @Override
    public String generateSummary(String title, String contentMarkdown) {
        try {
            ChatModel model = getChatModel();
            if (model == null) {
                return null;
            }

            // 超长内容截断，避免超出模型上下文窗口
            String content = contentMarkdown;
            if (content.length() > MAX_CONTENT_CHARS) {
                content = content.substring(0, MAX_CONTENT_CHARS);
                log.warn("文章内容超过 {} 字符，已截断后发送给模型: title={}", MAX_CONTENT_CHARS, title);
            }

            String userPrompt = "文章标题：" + title + "\n\n文章内容：\n" + content;
            ChatRequest request = ChatRequest.builder()
                    .messages(SystemMessage.from(ArticleSummaryPrompt.SUMMARY_PROMPT), UserMessage.from(userPrompt))
                    .build();

            String summary = model.chat(request).aiMessage().text();
            if (summary == null || summary.isBlank()) {
                log.warn("AI 返回的摘要为空: title={}", title);
                return null;
            }
            return summary.trim();
        } catch (Exception e) {
            // 生成失败只记日志，绝不向上抛，保证不影响发布主流程
            log.error("AI 摘要生成失败: title={}, ex={}", title, e.getMessage());
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
                        log.warn("AI 模块未配置 api-key 或 base-url，摘要生成功能不可用");
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
