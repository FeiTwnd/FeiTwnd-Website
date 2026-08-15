package cc.feitwnd.service.impl;

import cc.feitwnd.entity.Articles;
import cc.feitwnd.extension.ai.AiSummaryGenerator;
import cc.feitwnd.mapper.ArticleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AI 摘要异步回填服务单元测试
 * 覆盖：模块缺失跳过、生成成功回填并清缓存、结果为空不回填、生成异常不影响主流程
 */
@ExtendWith(MockitoExtension.class)
class SummaryBackfillAsyncServiceImplTest {

    @Mock
    private ObjectProvider<AiSummaryGenerator> generatorProvider;

    @Mock
    private AiSummaryGenerator generator;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private CacheManager cacheManager;

    private SummaryBackfillAsyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SummaryBackfillAsyncServiceImpl();
        ReflectionTestUtils.setField(service, "aiSummaryGeneratorProvider", generatorProvider);
        ReflectionTestUtils.setField(service, "articleMapper", articleMapper);
        ReflectionTestUtils.setField(service, "cacheManager", cacheManager);
    }

    /**
     * AI 模块未打包（无实现 bean）时，直接跳过，不触碰数据库与缓存
     */
    @Test
    void should_skip_when_ai_module_not_packaged() {
        when(generatorProvider.getIfAvailable()).thenReturn(null);

        service.backfillSummaryAsync(1L, "标题", "内容");

        verify(articleMapper, never()).update(any());
        verify(cacheManager, never()).getCache(any());
    }

    /**
     * 生成成功时回填摘要，并清理文章相关缓存
     */
    @Test
    void should_backfill_summary_and_evict_caches_when_generated() {
        when(generatorProvider.getIfAvailable()).thenReturn(generator);
        when(generator.generateSummary("标题", "内容")).thenReturn("  这是一段摘要  ");

        // 模拟 4 个文章缓存均存在
        for (String cacheName : new String[]{"articleList", "articleDetail", "articleArchive", "blogReport"}) {
            Cache cache = mock(Cache.class);
            when(cacheManager.getCache(cacheName)).thenReturn(cache);
        }

        service.backfillSummaryAsync(1L, "标题", "内容");

        // 回填的摘要应去除首尾空白
        ArgumentCaptor<Articles> captor = ArgumentCaptor.forClass(Articles.class);
        verify(articleMapper).update(captor.capture());
        assertEquals(1L, captor.getValue().getId());
        assertEquals("这是一段摘要", captor.getValue().getSummary());

        // 4 个缓存全部清理
        verify(cacheManager).getCache(eq("articleList"));
        verify(cacheManager).getCache(eq("articleDetail"));
        verify(cacheManager).getCache(eq("articleArchive"));
        verify(cacheManager).getCache(eq("blogReport"));
    }

    /**
     * 生成结果为空时不做回填，但缓存也应保持不动
     */
    @Test
    void should_not_backfill_when_summary_blank() {
        when(generatorProvider.getIfAvailable()).thenReturn(generator);
        when(generator.generateSummary(any(), any())).thenReturn("   ");

        service.backfillSummaryAsync(1L, "标题", "内容");

        verify(articleMapper, never()).update(any());
        verify(cacheManager, never()).getCache(any());
    }

    /**
     * 生成抛异常时只记日志，不向上抛，保证发布主流程不受影响
     */
    @Test
    void should_not_throw_when_generator_fails() {
        when(generatorProvider.getIfAvailable()).thenReturn(generator);
        when(generator.generateSummary(any(), any())).thenThrow(new RuntimeException("接口超时"));

        service.backfillSummaryAsync(1L, "标题", "内容");

        verify(articleMapper, never()).update(any());
    }
}
