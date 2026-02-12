package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.entity.ArticleCategories;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端文章分类接口
 */
@Slf4j
@RestController("adminArticleCategoryController")
@RequestMapping("/admin/articleCategory")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    /**
     * 获取所有文章分类
     * @return
     */
    @GetMapping
    public Result<List<ArticleCategories>> listAll() {
        List<ArticleCategories> categoryList = articleCategoryService.listAll();
        return Result.success(categoryList);
    }

    /**
     * 添加文章分类
     * @param articleCategories
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "articleCategory")
    public Result addCategory(@RequestBody ArticleCategories articleCategories) {
        log.info("添加文章分类,{}", articleCategories);
        articleCategoryService.addCategory(articleCategories);
        return Result.success();
    }

    /**
     * 更新文章分类
     * @param articleCategories
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "articleCategory", targetId = "#articleCategories.id")
    public Result updateCategory(@RequestBody ArticleCategories articleCategories) {
        log.info("更新文章分类,{}", articleCategories);
        articleCategoryService.updateCategory(articleCategories);
        return Result.success();
    }

    /**
     * 批量删除文章分类
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "articleCategory", targetId = "#ids")
    public Result deleteCategory(@RequestParam List<Long> ids) {
        log.info("批量删除文章分类,{}", ids);
        articleCategoryService.batchDelete(ids);
        return Result.success();
    }
}
