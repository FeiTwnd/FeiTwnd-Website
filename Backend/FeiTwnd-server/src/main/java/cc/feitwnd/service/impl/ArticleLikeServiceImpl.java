package cc.feitwnd.service.impl;

import cc.feitwnd.entity.ArticleLikes;
import cc.feitwnd.mapper.ArticleLikeMapper;
import cc.feitwnd.mapper.ArticleMapper;
import cc.feitwnd.service.ArticleLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ArticleLikeServiceImpl implements ArticleLikeService {

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Transactional
    public void likeArticle(Long articleId, Long visitorId) {
        // 检查是否已经点赞
        int count = articleLikeMapper.countByArticleIdAndVisitorId(articleId, visitorId);
        if (count > 0) {
            return;
        }
        // 插入点赞记录
        ArticleLikes articleLikes = ArticleLikes.builder()
                .articleId(articleId)
                .visitorId(visitorId)
                .likeTime(LocalDateTime.now())
                .build();
        articleLikeMapper.insert(articleLikes);
        // 文章点赞数+1
        articleMapper.incrementLikeCount(articleId);
    }

    @Transactional
    public void unlikeArticle(Long articleId, Long visitorId) {
        // 检查是否已经点赞
        int count = articleLikeMapper.countByArticleIdAndVisitorId(articleId, visitorId);
        if (count == 0) {
            return;
        }
        // 删除点赞记录
        articleLikeMapper.deleteByArticleIdAndVisitorId(articleId, visitorId);
        // 文章点赞数-1
        articleMapper.decrementLikeCount(articleId);
    }

    public boolean hasLiked(Long articleId, Long visitorId) {
        return articleLikeMapper.countByArticleIdAndVisitorId(articleId, visitorId) > 0;
    }
}
