package cc.feitwnd.mapper;

import cc.feitwnd.dto.ArticleCommentPageQueryDTO;
import cc.feitwnd.entity.ArticleComments;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleCommentMapper {

    /**
     * 保存评论
     * @param articleComments
     */
    void save(ArticleComments articleComments);

    /**
     * 分页条件查询评论
     * @param articleCommentPageQueryDTO
     * @return
     */
    List<ArticleComments> pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO);

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
}
