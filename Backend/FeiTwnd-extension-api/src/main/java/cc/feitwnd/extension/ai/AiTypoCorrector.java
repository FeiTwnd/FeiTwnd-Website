package cc.feitwnd.extension.ai;

/**
 * AI 错别字/病句纠错契约（可插拔模块接口）
 *
 * 该接口定义在契约层 FeiTwnd-extension-api 中：
 * 主程序（FeiTwnd-server）编译期只依赖此接口，
 * 实际实现由 FeiTwnd-ai 模块提供，仅在以 -Pwith-ai 构建时才打包进产物。
 * 未打包 AI 模块时，调用方通过 ObjectProvider 判空跳过，应用照常运行。
 */
public interface AiTypoCorrector {

    /**
     * 对文章 Markdown 内容进行错别字/病句纠错，返回纠错后的完整内容
     *
     * @param contentMarkdown 文章 Markdown 内容
     * @return 纠错后的完整 Markdown 内容；生成失败或结果为空时返回 null，调用方保留原文
     */
    String correctTypo(String contentMarkdown);
}
