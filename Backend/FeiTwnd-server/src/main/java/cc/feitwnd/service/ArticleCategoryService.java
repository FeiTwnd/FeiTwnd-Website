package cc.feitwnd.service;

import cc.feitwnd.dto.ArticleCategoryDTO;
import cc.feitwnd.entity.ArticleCategories;

import java.util.List;

public interface ArticleCategoryService {
    /**
     * 获取所有文章分类
     * @return
     */
    List<ArticleCategories> listAll();

    /**
     * 添加文章分类
     * @param articleCategoryDTO
     */
    void addCategory(ArticleCategoryDTO articleCategoryDTO);

    /**
     * 更新文章分类（含排序）
     * @param articleCategoryDTO
     */
    void updateCategory(ArticleCategoryDTO articleCategoryDTO);

    /**
     * 批量删除文章分类
     * @param ids
     */
    void batchDelete(List<Long> ids);

    // ===== 博客端方法 =====

    /**
     * 获取所有有已发布文章的可见分类
     */
    List<ArticleCategories> getVisibleCategories();
}
