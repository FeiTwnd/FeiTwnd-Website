package cc.feitwnd.service;

import cc.feitwnd.entity.ArticleTags;

import java.util.List;

public interface ArticleTagService {

    /**
     * 获取所有标签
     */
    List<ArticleTags> listAll();

    /**
     * 添加标签
     */
    void addTag(ArticleTags articleTag);

    /**
     * 修改标签
     */
    void updateTag(ArticleTags articleTag);

    /**
     * 批量删除标签
     */
    void batchDelete(List<Long> ids);

    /**
     * 获取有已发布文章的标签列表（博客端）
     */
    List<ArticleTags> getVisibleTags();
}
