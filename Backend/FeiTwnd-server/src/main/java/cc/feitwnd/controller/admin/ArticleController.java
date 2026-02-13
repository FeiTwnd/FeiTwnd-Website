package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.ArticleDTO;
import cc.feitwnd.dto.ArticlePageQueryDTO;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端文章接口
 */
@Slf4j
@RestController("adminArticleController")
@RequestMapping("/admin/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页条件查询文章列表
     * @param articlePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(ArticlePageQueryDTO articlePageQueryDTO) {
        log.info("分页条件查询文章列表: {}", articlePageQueryDTO);
        PageResult pageResult = articleService.pageQuery(articlePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取文章详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Articles> getById(@PathVariable Long id) {
        log.info("根据ID获取文章详情: {}", id);
        Articles articles = articleService.getById(id);
        return Result.success(articles);
    }

    /**
     * 创建文章
     * @param articleDTO
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "article")
    public Result createArticle(@RequestBody ArticleDTO articleDTO) {
        log.info("创建文章: {}", articleDTO);
        articleService.createArticle(articleDTO);
        return Result.success();
    }

    /**
     * 更新文章
     * @param articleDTO
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "article", targetId = "#articleDTO.id")
    public Result updateArticle(@RequestBody ArticleDTO articleDTO) {
        log.info("更新文章: {}", articleDTO);
        articleService.updateArticle(articleDTO);
        return Result.success();
    }

    /**
     * 批量删除文章
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "article", targetId = "#ids")
    public Result batchDelete(@RequestParam List<Long> ids) {
        log.info("批量删除文章: {}", ids);
        articleService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 发布/取消发布文章
     * @param id
     * @param isPublished 0-取消发布，1-发布
     * @return
     */
    @PutMapping("/publish/{id}")
    @OperationLog(value = OperationType.UPDATE, target = "article", targetId = "#id")
    public Result publishOrCancel(@PathVariable Long id, @RequestParam Integer isPublished) {
        log.info("发布/取消发布文章: id={}, isPublished={}", id, isPublished);
        articleService.publishOrCancel(id, isPublished);
        return Result.success();
    }

    /**
     * 文章搜索（标题、内容）
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/search")
    public Result<PageResult> search(@RequestParam String keyword,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        log.info("文章搜索: keyword={}", keyword);
        PageResult pageResult = articleService.search(keyword, page, pageSize);
        return Result.success(pageResult);
    }
}
