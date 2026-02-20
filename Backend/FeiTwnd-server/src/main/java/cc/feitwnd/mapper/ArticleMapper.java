package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.ArticlePageQueryDTO;
import cc.feitwnd.dto.ArticleTitleViewCountDTO;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.vo.ArticleArchiveItemVO;
import cc.feitwnd.vo.ArticleVO;
import cc.feitwnd.vo.BlogArticleDetailVO;
import cc.feitwnd.vo.BlogArticleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 插入文章
     * @param articles
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Articles articles);

    /**
     * 分页条件查询文章（含分类名称）
     * @param articlePageQueryDTO
     * @return
     */
    Page<ArticleVO> pageQuery(ArticlePageQueryDTO articlePageQueryDTO);

    /**
     * 根据ID查询文章
     * @param id
     * @return
     */
    @Select("select * from articles where id = #{id}")
    Articles getById(Long id);

    /**
     * 更新文章
     * @param articles
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Articles articles);

    /**
     * 批量删除文章
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 全文搜索文章（标题、内容）
     * @param keyword
     * @return
     */
    Page<ArticleVO> search(String keyword);

    // ===== 博客端方法 =====

    /**
     * 获取已发布文章列表（分页）
     */
    Page<BlogArticleVO> getPublishedPage();

    /**
     * 根据slug获取文章详情
     */
    BlogArticleDetailVO getBySlug(String slug);

    /**
     * 根据分类ID获取已发布文章列表（分页）
     */
    Page<BlogArticleVO> getPublishedByCategoryId(Long categoryId);

    /**
     * 获取文章归档列表
     */
    List<ArticleArchiveItemVO> getArchiveList();

    /**
     * 博客端文章搜索（仅已发布）
     */
    Page<BlogArticleVO> searchPublished(String keyword);

    /**
     * 浏览量+1
     */
    @Update("update articles set view_count = view_count + 1 where id = #{id}")
    void incrementViewCount(Long id);

    /**
     * 浏览量批量累加（定时同步Redis增量）
     */
    @AutoFill(value = OperationType.UPDATE)
    @Update("update articles set view_count = view_count + #{increment} where id = #{id}")
    void addViewCount(@Param("id") Long id, @Param("increment") int increment);

    /**
     * 点赞数+1
     */
    @AutoFill(value = OperationType.UPDATE)
    @Update("update articles set like_count = like_count + 1 where id = #{id}")
    void incrementLikeCount(Long id);

    /**
     * 点赞数-1
     */
    @AutoFill(value = OperationType.UPDATE)
    @Update("update articles set like_count = case when like_count > 0 then like_count - 1 else 0 end where id = #{id}")
    void decrementLikeCount(Long id);

    /**
     * 统计已发布文章数
     */
    @Select("select count(*) from articles where is_published = 1")
    Integer countPublished();

    /**
     * 根据分类ID统计文章数
     */
    @Select("select count(*) from articles where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    List<ArticleTitleViewCountDTO> getViewTop10();

    /**
     * 根据标签ID获取已发布文章列表（分页）
     */
    Page<BlogArticleVO> getPublishedByTagId(Long tagId);

    /**
     * 获取当前文章的上一篇(按发布时间)
     */
    BlogArticleVO getPrevArticle(Long id);

    /**
     * 获取当前文章的下一篇(按发布时间)
     */
    BlogArticleVO getNextArticle(Long id);

    /**
     * 获取相关文章(同分类,排除当前文章,最多6篇)
     */
    List<BlogArticleVO> getRelatedArticles(Long articleId, Long categoryId);
}
