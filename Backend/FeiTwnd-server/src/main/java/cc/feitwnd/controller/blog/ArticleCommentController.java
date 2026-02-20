package cc.feitwnd.controller.blog;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.dto.ArticleCommentDTO;
import cc.feitwnd.dto.ArticleCommentEditDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleCommentService;
import cc.feitwnd.vo.ArticleCommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客端文章评论接口
 */
@RestController("blogArticleCommentController")
@RequestMapping("/blog/articleComment")
@Slf4j
public class ArticleCommentController {

    @Autowired
    private ArticleCommentService articleCommentService;

    /**
     * 根据文章ID获取评论列表（树形结构，含当前访客的未审核评论）
     */
    @GetMapping("/article/{articleId}")
    public Result<List<ArticleCommentVO>> getCommentTree(@PathVariable Long articleId,
                                                         @RequestParam(required = false) Long visitorId) {
        log.info("博客端获取文章评论树: articleId={}, visitorId={}", articleId, visitorId);
        List<ArticleCommentVO> commentTree = articleCommentService.getCommentTree(articleId, visitorId);
        return Result.success(commentTree);
    }

    /**
     * 提交评论（添加评论/回复评论）
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "评论过于频繁，请稍后再试")
    public Result<String> submitComment(@Valid @RequestBody ArticleCommentDTO articleCommentDTO,
                                        HttpServletRequest request) {
        log.info("访客提交文章评论: {}", articleCommentDTO);
        articleCommentService.submitComment(articleCommentDTO, request);
        return Result.success();
    }

    /**
     * 访客编辑评论
     */
    @PutMapping("/edit")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> editComment(@Valid @RequestBody ArticleCommentEditDTO editDTO) {
        log.info("访客编辑评论: {}", editDTO);
        articleCommentService.editComment(editDTO);
        return Result.success();
    }

    /**
     * 访客删除评论
     */
    @DeleteMapping("/{id}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> deleteComment(@PathVariable Long id, @RequestParam Long visitorId) {
        log.info("访客删除评论: id={}, visitorId={}", id, visitorId);
        articleCommentService.visitorDeleteComment(id, visitorId);
        return Result.success();
    }
}
