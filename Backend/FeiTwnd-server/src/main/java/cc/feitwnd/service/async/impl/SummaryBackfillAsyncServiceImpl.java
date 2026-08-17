package cc.feitwnd.service.async.impl;

import cc.feitwnd.entity.Articles;
import cc.feitwnd.extension.ai.AiSummaryGenerator;
import cc.feitwnd.mapper.ArticleMapper;
import cc.feitwnd.service.async.SummaryBackfillAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 摘要异步回填服务实现
 *
 * 通过 ObjectProvider 获取 AI 生成器：AI 模块未打包（classpath 无实现类）时
 * getIfAvailable 返回 null，直接跳过，保证可插拔构建下主程序照常运行。
 */
@Slf4j
@Service
public class SummaryBackfillAsyncServiceImpl implements SummaryBackfillAsyncService {

    /** 摘要回填后需要清理的缓存（与文章发布时的缓存清理保持一致） */
    private static final List<String> ARTICLE_CACHE_NAMES =
            List.of("articleList", "articleDetail", "articleArchive", "blogReport");

    @Autowired
    private ObjectProvider<AiSummaryGenerator> aiSummaryGeneratorProvider;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 异步生成摘要并回填；全程失败仅记日志，绝不影响发布主流程
     */
    @Async("taskExecutor")
    public void backfillSummaryAsync(Long articleId, String title, String contentMarkdown) {
        AiSummaryGenerator generator = aiSummaryGeneratorProvider.getIfAvailable();
        if (generator == null) {
            log.info("AI 摘要模块未启用，跳过摘要生成: articleId={}", articleId);
            return;
        }
        try {
            String generated = generator.generateSummary(title, contentMarkdown);
            if (generated == null || generated.isBlank()) {
                log.warn("AI 摘要生成结果为空，不回填: articleId={}", articleId);
                return;
            }
            // 防御性处理首尾空白，保证回填内容干净
            String summary = generated.trim();
            // 动态更新只写非空字段，回填摘要不会影响文章其他内容
            articleMapper.update(Articles.builder().id(articleId).summary(summary).build());
            evictArticleCaches();
            log.info("AI 摘要回填成功: articleId={}", articleId);
        } catch (Exception e) {
            log.error("AI 摘要回填异常: articleId={}, ex={}", articleId, e.getMessage());
        }
    }

    /**
     * 清理文章相关缓存，保证博客端立即可见新摘要
     */
    private void evictArticleCaches() {
        for (String cacheName : ARTICLE_CACHE_NAMES) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}
