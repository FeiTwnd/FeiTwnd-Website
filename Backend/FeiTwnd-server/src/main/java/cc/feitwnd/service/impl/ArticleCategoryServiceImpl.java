package cc.feitwnd.service.impl;

import cc.feitwnd.dto.ArticleCategoryDTO;
import cc.feitwnd.entity.ArticleCategories;
import cc.feitwnd.mapper.ArticleCategoryMapper;
import cc.feitwnd.mapper.ArticleMapper;
import cc.feitwnd.service.ArticleCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Autowired
    private ArticleMapper articleMapper;

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
     * @param articleCategoryDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void addCategory(ArticleCategoryDTO articleCategoryDTO) {
        ArticleCategories articleCategories = new ArticleCategories();
        BeanUtils.copyProperties(articleCategoryDTO, articleCategories);
        articleCategoryMapper.insert(articleCategories);
    }

    /**
     * 更新文章分类（含排序）
     * @param articleCategoryDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateCategory(ArticleCategoryDTO articleCategoryDTO) {
        ArticleCategories articleCategories = new ArticleCategories();
        BeanUtils.copyProperties(articleCategoryDTO, articleCategories);
        articleCategoryMapper.update(articleCategories);
    }

    /**
     * 批量删除文章分类
     * @param ids
     */
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void batchDelete(List<Long> ids) {
        // 检查分类下是否有关联文章
        for (Long id : ids) {
            Integer count = articleMapper.countByCategoryId(id);
            if (count != null && count > 0) {
                throw new RuntimeException("分类下存在关联文章，无法删除");
            }
        }
        articleCategoryMapper.batchDelete(ids);
    }

    // ===== 博客端方法 =====

    @Cacheable(value = "articleCategories", key = "'visible'")
    public List<ArticleCategories> getVisibleCategories() {
        return articleCategoryMapper.getVisibleCategories();
    }
}
