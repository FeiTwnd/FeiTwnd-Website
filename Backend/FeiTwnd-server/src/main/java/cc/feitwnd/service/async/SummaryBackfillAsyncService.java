package cc.feitwnd.service.async;

/**
 * AI 摘要异步回填服务
 *
 * 发布文章后异步生成摘要并写回数据库。AI 模块未打包时自动跳过，
 * 绝不影响发布主流程。
 */
public interface SummaryBackfillAsyncService {

    /**
     * 异步生成文章摘要并回填到 articles.summary
     *
     * @param articleId        文章ID
     * @param title            文章标题
     * @param contentMarkdown  文章 Markdown 内容（随参数传入，避免异步线程读取未提交数据）
     */
    void backfillSummaryAsync(Long articleId, String title, String contentMarkdown);
}
