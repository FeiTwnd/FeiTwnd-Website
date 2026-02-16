package cc.feitwnd.service.impl;

import cc.feitwnd.entity.ArticleTags;
import cc.feitwnd.mapper.ArticleTagMapper;
import cc.feitwnd.service.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ArticleTagServiceImpl implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    public List<ArticleTags> listAll() {
        List<ArticleTags> list = articleTagMapper.listAll();
        return list != null ? list : Collections.emptyList();
    }

    public void addTag(ArticleTags articleTag) {
        articleTagMapper.insert(articleTag);
    }

    public void updateTag(ArticleTags articleTag) {
        articleTagMapper.update(articleTag);
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        // 先删除关联关系中涉及这些标签的记录
        articleTagMapper.batchDelete(ids);
    }

    public List<ArticleTags> getVisibleTags() {
        List<ArticleTags> list = articleTagMapper.getVisibleTags();
        return list != null ? list : Collections.emptyList();
    }
}
