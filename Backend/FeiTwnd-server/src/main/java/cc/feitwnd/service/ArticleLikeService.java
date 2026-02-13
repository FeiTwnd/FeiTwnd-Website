package cc.feitwnd.service;

public interface ArticleLikeService {

    /**
     * 点赞文章
     */
    void likeArticle(Long articleId, Long visitorId);

    /**
     * 取消点赞
     */
    void unlikeArticle(Long articleId, Long visitorId);

    /**
     * 检查是否已点赞
     */
    boolean hasLiked(Long articleId, Long visitorId);
}
