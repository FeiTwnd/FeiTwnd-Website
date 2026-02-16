package cc.feitwnd.service;

import cc.feitwnd.dto.ArticleDTO;
import cc.feitwnd.dto.ArticlePageQueryDTO;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.vo.ArticleArchiveVO;
import cc.feitwnd.vo.BlogArticleDetailVO;

import java.util.List;

/**
 * 文章服务
 */
public interface ArticleService {

    /**
     * 创建文章
     * @param articleDTO
     */
    void createArticle(ArticleDTO articleDTO);

    /**
     * 分页条件查询文章列表（含草稿）
     * @param articlePageQueryDTO
     * @return
     */
    PageResult pageQuery(ArticlePageQueryDTO articlePageQueryDTO);

    /**
     * 根据ID获取文章详情
     * @param id
     * @return
     */
    Articles getById(Long id);

    /**
     * 更新文章
     * @param articleDTO
     */
    void updateArticle(ArticleDTO articleDTO);

    /**
     * 批量删除文章
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 发布/取消发布文章
     * @param id
     * @param isPublished
     */
    void publishOrCancel(Long id, Integer isPublished);

    /**
     * 文章搜索（标题、内容）
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    PageResult search(String keyword, int page, int pageSize);

    // ===== 博客端方法 =====

    /**
     * 获取已发布文章列表（分页）
     */
    PageResult getPublishedPage(int page, int pageSize);

    /**
     * 根据slug获取文章详情（浏览量+1）
     */
    BlogArticleDetailVO getBySlug(String slug);

    /**
     * 根据分类ID获取已发布文章列表（分页）
     */
    PageResult getPublishedByCategoryId(Long categoryId, int page, int pageSize);

    /**
     * 获取文章归档（按年月分组）
     */
    List<ArticleArchiveVO> getArchive();

    /**
     * 博客端文章搜索（仅已发布）
     */
    PageResult searchPublished(String keyword, int page, int pageSize);

    /**
     * 根据标签ID获取已发布文章列表
     */
    PageResult getPublishedByTagId(Long tagId, int page, int pageSize);
}
