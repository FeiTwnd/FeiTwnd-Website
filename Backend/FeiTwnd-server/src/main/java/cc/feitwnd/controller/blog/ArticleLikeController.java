package cc.feitwnd.controller.blog;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.article.ArticleLikeService;
import cc.feitwnd.service.visitor.VisitorTokenService;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    private VisitorTokenService visitorTokenService;

    /**
     * 点赞文章（需token验证访客身份）
     */
    @PostMapping("/{articleId}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
              timeWindow = 60, message = "点赞操作过于频繁，请稍后再试")
    public Result<String> like(@PathVariable Long articleId, HttpServletRequest request) {
        Long visitorId = visitorTokenService.resolveVisitorId(request);
        log.info("访客点赞文章: articleId={}, visitorId={}", articleId, visitorId);
        articleLikeService.likeArticle(articleId, visitorId);
        return Result.success();
    }

    /**
     * 取消点赞（需token验证访客身份）
     */
    @DeleteMapping("/{articleId}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> unlike(@PathVariable Long articleId, HttpServletRequest request) {
        Long visitorId = visitorTokenService.resolveVisitorId(request);
        log.info("访客取消点赞: articleId={}, visitorId={}", articleId, visitorId);
        articleLikeService.unlikeArticle(articleId, visitorId);
        return Result.success();
    }

    /**
     * 检查是否已点赞（需token验证访客身份）
     */
    @GetMapping("/{articleId}")
    public Result<Boolean> hasLiked(@PathVariable Long articleId, HttpServletRequest request) {
        Long visitorId = visitorTokenService.resolveVisitorId(request);
        log.info("检查是否已点赞: articleId={}, visitorId={}", articleId, visitorId);
        boolean liked = articleLikeService.hasLiked(articleId, visitorId);
        return Result.success(liked);
    }
}
