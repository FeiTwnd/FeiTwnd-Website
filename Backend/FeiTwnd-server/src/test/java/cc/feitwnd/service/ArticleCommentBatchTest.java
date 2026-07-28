package cc.feitwnd.service;

import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.mapper.ArticleCommentMapper;
import cc.feitwnd.service.impl.ArticleCommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * 校验评论批量操作在修复 N+1 后：
 * 1. 只做一次批量查询（getByIds），不再循环 getById；
 * 2. 文章评论数按聚合结果一次性加减，不再逐次 ±1。
 */
@ExtendWith(MockitoExtension.class)
class ArticleCommentBatchTest {

    @Mock
    private ArticleCommentMapper articleCommentMapper;

    @InjectMocks
    private ArticleCommentServiceImpl service;

    private ArticleComments comment(Long id, Long articleId, Long rootId, Integer approved) {
        ArticleComments c = new ArticleComments();
        c.setId(id);
        c.setArticleId(articleId);
        c.setRootId(rootId);
        c.setIsApproved(approved);
        return c;
    }

    @Test
    void batchApprove_aggregatesIncrementPerArticle() {
        List<Long> ids = List.of(1L, 2L, 3L);
        // 文章100下两条未审核(应+2)，文章200下一条已审核(不计)
        when(articleCommentMapper.getByIds(ids)).thenReturn(List.of(
                comment(1L, 100L, 0L, 0),
                comment(2L, 100L, 0L, null),
                comment(3L, 200L, 0L, 1)
        ));

        service.batchApprove(ids);

        // 只批量查询一次，不再循环 getById
        verify(articleCommentMapper, times(1)).getByIds(ids);
        verify(articleCommentMapper, never()).getById(anyLong());
        // 文章100一次性 +2，文章200不变
        verify(articleCommentMapper, times(1)).addCommentCount(100L, 2);
        verify(articleCommentMapper, never()).addCommentCount(eq(200L), anyInt());
        verify(articleCommentMapper, times(1)).batchApprove(ids);
    }

    @Test
    void batchDelete_subtractsAggregatedCountPerArticle() {
        List<Long> ids = List.of(10L);
        // 已审核的根评论，其下有3条已审核子评论 -> 文章300应 -(3+1)=-4
        when(articleCommentMapper.getByIds(ids)).thenReturn(List.of(
                comment(10L, 300L, 0L, 1)
        ));
        when(articleCommentMapper.countApprovedByRootId(10L)).thenReturn(3);

        service.batchDelete(ids);

        verify(articleCommentMapper, times(1)).getByIds(ids);
        verify(articleCommentMapper, never()).getById(anyLong());
        // 子评论3 + 自身1 聚合为一次 -4，不再逐次 -1
        verify(articleCommentMapper, times(1)).subtractCommentCount(300L, 4);
        verify(articleCommentMapper, never()).decrementCommentCount(anyLong());
        verify(articleCommentMapper, times(1)).deleteByRootId(10L);
        verify(articleCommentMapper, times(1)).batchDelete(ids);
    }

    @Test
    void batchApprove_emptyIds_noOp() {
        service.batchApprove(List.of());
        verifyNoInteractions(articleCommentMapper);
    }
}
