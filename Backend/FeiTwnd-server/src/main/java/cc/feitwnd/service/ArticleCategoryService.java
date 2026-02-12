package cc.feitwnd.service;

import cc.feitwnd.entity.ArticleCategories;

import java.util.List;

import java.util.List;

public interface ArticleCategoryService {
    /**
     * 获取所有文章分类
     * @return
     */
    List<ArticleCategories> listAll();

    /**
     * 添加文章分类
     * @param articleCategories
     */
    void addCategory(ArticleCategories articleCategories);

    /**
     * 更新文章分类（含排序）
     * @param articleCategories
     */
    void updateCategory(ArticleCategories articleCategories);

    /**
     * 批量删除文章分类
     * @param ids
     */
    void batchDelete(List<Long> ids);
}
