package cc.feitwnd.ai.service;

import cc.feitwnd.ai.properties.AiProperties;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AI 摘要生成器单元测试
 * 覆盖：正常生成去空白、结果为空返回 null、模型异常返回 null、超长内容截断
 */
@ExtendWith(MockitoExtension.class)
class AiSummaryGeneratorImplTest {

    @Mock
    private ChatModel chatModel;

    private AiProperties properties;

    private AiSummaryGeneratorImpl generator;

    @BeforeEach
    void setUp() {
        properties = new AiProperties();
        generator = new AiSummaryGeneratorImpl(properties, chatModel);
    }

    /**
     * 正常生成时去除首尾空白后返回
     */
    @Test
    void should_trim_summary_when_generated() {
        when(chatModel.chat(any(ChatRequest.class)))
                .thenReturn(ChatResponse.builder().aiMessage(AiMessage.from("  摘要内容  ")).build());

        String result = generator.generateSummary("标题", "正文");

        assertEquals("摘要内容", result);
    }

    /**
     * 模型返回空白时返回 null，由调用方决定不回填
     */
    @Test
    void should_return_null_when_summary_blank() {
        when(chatModel.chat(any(ChatRequest.class)))
                .thenReturn(ChatResponse.builder().aiMessage(AiMessage.from("  ")).build());

        assertNull(generator.generateSummary("标题", "正文"));
    }

    /**
     * 模型调用抛异常时返回 null，不向上抛
     */
    @Test
    void should_return_null_when_model_fails() {
        when(chatModel.chat(any(ChatRequest.class))).thenThrow(new RuntimeException("连接超时"));

        assertNull(generator.generateSummary("标题", "正文"));
    }

    /**
     * 超过最大字符数的内容截断后再发送给模型
     */
    @Test
    void should_truncate_long_content() {
        String longContent = "一".repeat(AiSummaryGeneratorImpl.MAX_CONTENT_CHARS + 1);
        when(chatModel.chat(any(ChatRequest.class)))
                .thenReturn(ChatResponse.builder().aiMessage(AiMessage.from("摘要")).build());

        generator.generateSummary("标题", longContent);

        ArgumentCaptor<ChatRequest> captor = ArgumentCaptor.forClass(ChatRequest.class);
        verify(chatModel).chat(captor.capture());
        UserMessage userMessage = (UserMessage) captor.getValue().messages().get(1);
        assertEquals(AiSummaryGeneratorImpl.MAX_CONTENT_CHARS,
                userMessage.singleText().replace("文章标题：标题\n\n文章内容：\n", "").length());
    }

    /**
     * 未配置 api-key 时返回 null，不创建模型、不抛异常（fail-safe）
     */
    @Test
    void should_return_null_when_api_key_missing() {
        properties.setApiKey(null);
        properties.setBaseUrl("https://example.com/v1");
        AiSummaryGeneratorImpl lazyGenerator = new AiSummaryGeneratorImpl(properties);

        assertNull(lazyGenerator.generateSummary("标题", "正文"));
    }
}
