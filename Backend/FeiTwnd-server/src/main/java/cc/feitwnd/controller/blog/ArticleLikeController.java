package cc.feitwnd.controller.blog;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客端文章点赞接口
 */
@RestController("blogArticleLikeController")
@RequestMapping("/blog/articleLike")
@Slf4j
public class ArticleLikeController {

    @Autowired
    private ArticleLikeService articleLikeService;

    /**
     * 点赞文章
     */
    @PostMapping("/{articleId}")
    public Result<String> like(@PathVariable Long articleId, @RequestParam Long visitorId) {
        log.info("访客点赞文章: articleId={}, visitorId={}", articleId, visitorId);
        articleLikeService.likeArticle(articleId, visitorId);
        return Result.success();
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{articleId}")
    public Result<String> unlike(@PathVariable Long articleId, @RequestParam Long visitorId) {
        log.info("访客取消点赞: articleId={}, visitorId={}", articleId, visitorId);
        articleLikeService.unlikeArticle(articleId, visitorId);
        return Result.success();
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/{articleId}")
    public Result<Boolean> hasLiked(@PathVariable Long articleId, @RequestParam Long visitorId) {
        log.info("检查是否已点赞: articleId={}, visitorId={}", articleId, visitorId);
        boolean liked = articleLikeService.hasLiked(articleId, visitorId);
        return Result.success(liked);
    }
}
