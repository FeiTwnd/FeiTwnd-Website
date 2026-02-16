package cc.feitwnd.service.impl;

import cc.feitwnd.entity.ArticleCategories;
import cc.feitwnd.mapper.ArticleCategoryMapper;
import cc.feitwnd.service.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    /**
     * 获取所有文章分类
     * @return
     */
    @Cacheable(value = "articleCategories", key = "'all'")
    public List<ArticleCategories> listAll() {
        return articleCategoryMapper.listAll();
    }

    /**
     * 添加文章分类
     * @param articleCategories
     */
    @CacheEvict(value = "articleCategories", allEntries = true)
    public void addCategory(ArticleCategories articleCategories) {
        articleCategoryMapper.insert(articleCategories);
    }

    /**
     * 更新文章分类（含排序）
     * @param articleCategories
     */
    @CacheEvict(value = "articleCategories", allEntries = true)
    public void updateCategory(ArticleCategories articleCategories) {
        articleCategoryMapper.update(articleCategories);
    }

    /**
     * 批量删除文章分类
     * @param ids
     */
    @CacheEvict(value = "articleCategories", allEntries = true)
    public void batchDelete(List<Long> ids) {
        articleCategoryMapper.batchDelete(ids);
    }

    // ===== 博客端方法 =====

    @Cacheable(value = "articleCategories", key = "'visible'")
    public List<ArticleCategories> getVisibleCategories() {
        return articleCategoryMapper.getVisibleCategories();
    }
}
