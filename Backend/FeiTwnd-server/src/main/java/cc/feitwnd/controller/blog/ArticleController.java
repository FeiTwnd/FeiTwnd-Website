package cc.feitwnd.controller.blog;

import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleService;
import cc.feitwnd.vo.ArticleArchiveVO;
import cc.feitwnd.vo.BlogArticleDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        // 浏览量+1（写入Redis，与查询分离，不影响缓存）
        articleService.incrementViewCount(slug);
        BlogArticleDetailVO articleDetail = articleService.getBySlug(slug);
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
    public Result<PageResult> search(@RequestParam String keyword,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("博客端文章搜索: keyword={}", keyword);
        PageResult pageResult = articleService.searchPublished(keyword, page, pageSize);
        return Result.success(pageResult);
    }
}
