package cc.feitwnd.service;

import cc.feitwnd.dto.ArticleDTO;
import cc.feitwnd.dto.ArticlePageQueryDTO;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.result.PageResult;

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
}
