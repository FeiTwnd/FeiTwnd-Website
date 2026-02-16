package cc.feitwnd.controller.blog;

import cc.feitwnd.annotation.RateLimit;
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
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
              timeWindow = 60, message = "点赞操作过于频繁，请稍后再试")
    public Result<String> like(@PathVariable Long articleId, @RequestParam Long visitorId) {
        log.info("访客点赞文章: articleId={}, visitorId={}", articleId, visitorId);
        articleLikeService.likeArticle(articleId, visitorId);
        return Result.success();
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{articleId}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
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
