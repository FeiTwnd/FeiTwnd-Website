package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.ArticleTagDTO;
import cc.feitwnd.entity.ArticleTags;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleTagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端文章标签接口
 */
@Slf4j
@RestController("adminArticleTagController")
@RequestMapping("/admin/article/tag")
public class ArticleTagController {

    @Autowired
    private ArticleTagService articleTagService;

    /**
     * 获取所有标签
     * @return
     */
    @GetMapping
    public Result<List<ArticleTags>> listAll() {
        List<ArticleTags> list = articleTagService.listAll();
        return Result.success(list);
    }

    /**
     * 添加标签
     * @param articleTag
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "articleTag")
    public Result addTag(@Valid @RequestBody ArticleTagDTO articleTagDTO) {
        log.info("添加文章标签: {}", articleTagDTO);
        articleTagService.addTag(articleTagDTO);
        return Result.success();
    }

    /**
     * 修改标签
     * @param articleTag
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "articleTag", targetId = "#articleTagDTO.id")
    public Result updateTag(@Valid @RequestBody ArticleTagDTO articleTagDTO) {
        log.info("修改文章标签: {}", articleTagDTO);
        articleTagService.updateTag(articleTagDTO);
        return Result.success();
    }

    /**
     * 批量删除标签
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "articleTag", targetId = "#ids")
    public Result batchDelete(@RequestParam List<Long> ids) {
        log.info("批量删除文章标签: {}", ids);
        articleTagService.batchDelete(ids);
        return Result.success();
    }
}
