package cc.feitwnd.mapper;

import cc.feitwnd.dto.ArticleCommentPageQueryDTO;
import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.vo.ArticleCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

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

    // ===== 博客端方法 =====

    /**
     * 根据文章ID获取已审核的评论列表（用于构建树形结构）
     */
    List<ArticleCommentVO> getApprovedByArticleId(Long articleId);

    /**
     * 评论数+1
     */
    @Update("update articles set comment_count = comment_count + 1 where id = #{articleId}")
    void incrementCommentCount(Long articleId);
}
