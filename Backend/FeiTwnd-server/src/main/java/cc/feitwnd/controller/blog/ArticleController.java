package cc.feitwnd.controller.blog;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleService;
import cc.feitwnd.vo.ArticleArchiveVO;
import cc.feitwnd.vo.BlogArticleDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客端文章接口
 */
@RestController("blogArticleController")
@RequestMapping("/blog/article")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_COUNT_KEY = "article:viewCount";

    /**
     * 获取已发布文章列表（分页）
     */
    @GetMapping("/page")
    public Result<PageResult> getPublishedPage(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端获取已发布文章列表: page={}, pageSize={}", page, pageSize);
        PageResult pageResult = articleService.getPublishedPage(page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据slug获取文章详情（浏览量+1）
     */
    @GetMapping("/detail/{slug}")
    public Result<BlogArticleDetailVO> getBySlug(@PathVariable String slug) {
        log.info("博客端获取文章详情: slug={}", slug);
        // 先从缓存获取文章详情（避免冗余DB查询）
        BlogArticleDetailVO articleDetail = articleService.getBySlug(slug);
        // 浏览量+1（写入Redis，基于文章ID，不再需要额外查库）
        articleService.incrementViewCount(articleDetail.getId());
        // 合并Redis中尚未同步到MySQL的浏览量增量，展示实时浏览数
        Object pending = redisTemplate.opsForHash().get(VIEW_COUNT_KEY, articleDetail.getId().toString());
        if (pending != null) {
            long pendingCount = ((Number) pending).longValue();
            // 返回 MySQL基准值 + Redis待同步增量（含本次+1）
            articleDetail.setViewCount(articleDetail.getViewCount() + pendingCount);
        }
        return Result.success(articleDetail);
    }

    /**
     * 根据分类ID获取文章列表（分页）
     */
    @GetMapping("/category/{categoryId}")
    public Result<PageResult> getByCategory(@PathVariable Long categoryId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端根据分类获取文章列表: categoryId={}, page={}, pageSize={}", categoryId, page, pageSize);
        PageResult pageResult = articleService.getPublishedByCategoryId(categoryId, page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 获取文章归档（按年月分组）
     */
    @GetMapping("/archive")
    public Result<List<ArticleArchiveVO>> getArchive() {
        log.info("博客端获取文章归档");
        List<ArticleArchiveVO> archiveList = articleService.getArchive();
        return Result.success(archiveList);
    }

    /**
     * 文章搜索（仅已发布）
     */
    @GetMapping("/search")
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
            timeWindow = 60, message = "搜索过于频繁，请稍后再试")
    public Result<PageResult> search(@RequestParam String keyword,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端文章搜索: keyword={}", keyword);
        PageResult pageResult = articleService.searchPublished(keyword, page, pageSize);
        return Result.success(pageResult);
    }
}
