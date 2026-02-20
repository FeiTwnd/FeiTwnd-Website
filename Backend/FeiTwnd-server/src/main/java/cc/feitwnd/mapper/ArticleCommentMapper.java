package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.ArticleCommentPageQueryDTO;
import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.vo.ArticleCommentVO;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface ArticleCommentMapper {

    /**
     * 保存评论
     * @param articleComments
     */
    @AutoFill(value = OperationType.INSERT)
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
    @AutoFill(value = OperationType.UPDATE)
    void batchApprove(List<Long> ids);

    /**
     * 批量删除评论
     * @param ids
     */
    void batchDelete(List<Long> ids);

    // ===== 博客端方法 =====

    /**
     * 根据文章ID获取评论列表（已审核 + 指定访客的未审核评论）
     */
    List<ArticleCommentVO> getApprovedByArticleId(@Param("articleId") Long articleId, @Param("visitorId") Long visitorId);

    /**
     * 评论数+1
     */
    @Update("update articles set comment_count = comment_count + 1 where id = #{articleId}")
    void incrementCommentCount(Long articleId);

    /**
     * 评论数-1（最小为0）
     */
    @Update("update articles set comment_count = case when comment_count > 0 then comment_count - 1 else 0 end where id = #{articleId}")
    void decrementCommentCount(Long articleId);

    /**
     * 根据ID查询评论
     */
    @Select("select * from article_comments where id = #{id}")
    ArticleComments getById(Long id);

    /**
     * 更新评论内容（访客编辑）
     */
    void updateContent(ArticleComments articleComments);

    /**
     * 删除单条评论
     */
    @Delete("delete from article_comments where id = #{id}")
    void deleteById(Long id);

    /**
     * 统计总评论数
     */
    @Select("select count(*) from article_comments")
    Integer countTotal();

    /**
     * 统计待审核评论数
     */
    @Select("select count(*) from article_comments where is_approved = 0")
    Integer countPending();

    /**
     * 根据根评论ID删除所有子评论
     */
    @Delete("delete from article_comments where root_id = #{rootId}")
    void deleteByRootId(Long rootId);

    /**
     * 统计某根评论下的子评论数
     */
    @Select("select count(*) from article_comments where root_id = #{rootId}")
    Integer countByRootId(Long rootId);
}
