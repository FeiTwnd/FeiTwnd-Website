package cc.feitwnd.service;

import cc.feitwnd.dto.ArticleCommentPageQueryDTO;
import cc.feitwnd.dto.ArticleCommentReplyDTO;
import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.result.PageResult;

import java.util.List;

/**
 * 文章评论服务
 */
public interface ArticleCommentService {

    /**
     * 分页条件查询评论（时间、是否审核）
     * @param articleCommentPageQueryDTO
     * @return
     */
    PageResult pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO);

    /**
     * 根据文章ID查询评论
     * @param articleId
     * @return
     */
    List<ArticleComments> getByArticleId(Long articleId);

    /**
     * 批量审核通过评论
     * @param ids
     */
    void batchApprove(List<Long> ids);

    /**
     * 批量删除评论
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 管理员回复评论
     * @param articleCommentReplyDTO
     */
    void adminReply(ArticleCommentReplyDTO articleCommentReplyDTO);
}
