package cc.feitwnd.extension.ai;

/**
 * AI 摘要生成契约（可插拔模块接口）
 *
 * 该接口定义在契约层 FeiTwnd-extension-api 中：
 * 主程序（FeiTwnd-server）编译期只依赖此接口，
 * 实际实现由 FeiTwnd-ai 模块提供，仅在以 -Pwith-ai 构建时才打包进产物。
 * 未打包 AI 模块时，主程序通过 ObjectProvider 判空跳过，应用照常运行。
 */
public interface AiSummaryGenerator {

    /**
     * 根据文章标题与内容生成摘要
     *
     * @param title            文章标题
     * @param contentMarkdown  文章 Markdown 内容
     * @return 生成的摘要文本；生成失败或结果为空时返回 null，调用方不回填
     */
    String generateSummary(String title, String contentMarkdown);
}
