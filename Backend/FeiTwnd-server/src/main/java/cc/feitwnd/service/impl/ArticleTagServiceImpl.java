package cc.feitwnd.service.impl;

import cc.feitwnd.dto.ArticleTagDTO;
import cc.feitwnd.entity.ArticleTags;
import cc.feitwnd.mapper.ArticleTagMapper;
import cc.feitwnd.service.ArticleTagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ArticleTagServiceImpl implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    /**
     * 获取所有标签
     * @return
     */
    @Cacheable(value = "articleTags", key = "'all'")
    public List<ArticleTags> listAll() {
        List<ArticleTags> list = articleTagMapper.listAll();
        return list != null ? list : Collections.emptyList();
    }

    /**
     * 添加标签
     * @param articleTagDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void addTag(ArticleTagDTO articleTagDTO) {
        ArticleTags articleTag = new ArticleTags();
        BeanUtils.copyProperties(articleTagDTO, articleTag);
        articleTagMapper.insert(articleTag);
    }

    /**
     * 修改标签
     * @param articleTagDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateTag(ArticleTagDTO articleTagDTO) {
        ArticleTags articleTag = new ArticleTags();
        BeanUtils.copyProperties(articleTagDTO, articleTag);
        articleTagMapper.update(articleTag);
    }

    /**
     * 批量删除标签
     * @param ids
     */
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    @Transactional
    public void batchDelete(List<Long> ids) {
        // 先删除关联关系中涉及这些标签的记录
        articleTagMapper.batchDelete(ids);
    }

    /**
     * 获取标签
     * @return
     */
    @Cacheable(value = "articleTags", key = "'visible'")
    public List<ArticleTags> getVisibleTags() {
        List<ArticleTags> list = articleTagMapper.getVisibleTags();
        return list != null ? list : Collections.emptyList();
    }
}
