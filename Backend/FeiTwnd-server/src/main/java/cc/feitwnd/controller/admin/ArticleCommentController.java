package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.ArticleCommentPageQueryDTO;
import cc.feitwnd.dto.ArticleCommentReplyDTO;
import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端文章评论接口
 */
@Slf4j
@RestController("adminArticleCommentController")
@RequestMapping("/admin/article/comment")
public class ArticleCommentController {

    @Autowired
    private ArticleCommentService articleCommentService;

    /**
     * 分页条件查询评论（时间、是否审核）
     * @param articleCommentPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO) {
        log.info("分页条件查询文章评论: {}", articleCommentPageQueryDTO);
        PageResult pageResult = articleCommentService.pageQuery(articleCommentPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据文章ID查询评论
     * @param articleId
     * @return
     */
    @GetMapping("/{articleId}")
    public Result<List<ArticleComments>> getByArticleId(@PathVariable Long articleId) {
        log.info("根据文章ID查询评论: articleId={}", articleId);
        List<ArticleComments> comments = articleCommentService.getByArticleId(articleId);
        return Result.success(comments);
    }

    /**
     * 批量审核通过评论
     * @param ids
     * @return
     */
    @PutMapping("/approve")
    @OperationLog(value = OperationType.UPDATE, target = "articleComment", targetId = "#ids")
    public Result<String> batchApprove(@RequestParam List<Long> ids) {
        log.info("批量审核通过文章评论: {}", ids);
        articleCommentService.batchApprove(ids);
        return Result.success();
    }

    /**
     * 批量删除评论
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "articleComment", targetId = "#ids")
    public Result<String> batchDelete(@RequestParam List<Long> ids) {
        log.info("批量删除文章评论: {}", ids);
        articleCommentService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 管理员回复评论
     * @param articleCommentReplyDTO
     * @return
     */
    @PostMapping("/reply")
    @OperationLog(value = OperationType.INSERT, target = "articleComment", targetId = "#articleCommentReplyDTO.parentId")
    public Result<String> adminReply(@RequestBody ArticleCommentReplyDTO articleCommentReplyDTO) {
        log.info("管理员回复文章评论: {}", articleCommentReplyDTO);
        articleCommentService.adminReply(articleCommentReplyDTO);
        return Result.success();
    }
}
