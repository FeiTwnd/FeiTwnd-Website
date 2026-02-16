package cc.feitwnd.controller.blog;

import cc.feitwnd.dto.ArticleCommentDTO;
import cc.feitwnd.dto.ArticleCommentEditDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleCommentService;
import cc.feitwnd.vo.ArticleCommentVO;
import jakarta.servlet.http.HttpServletRequest;
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
     * 根据文章ID获取评论列表（树形结构）
     */
    @GetMapping("/article/{articleId}")
    public Result<List<ArticleCommentVO>> getCommentTree(@PathVariable Long articleId) {
        log.info("博客端获取文章评论树: articleId={}", articleId);
        List<ArticleCommentVO> commentTree = articleCommentService.getCommentTree(articleId);
        return Result.success(commentTree);
    }

    /**
     * 提交评论（添加评论/回复评论）
     */
    @PostMapping
    public Result<String> submitComment(@RequestBody ArticleCommentDTO articleCommentDTO,
                                        HttpServletRequest request) {
        log.info("访客提交文章评论: {}", articleCommentDTO);
        articleCommentService.submitComment(articleCommentDTO, request);
        return Result.success();
    }

    /**
     * 访客编辑评论
     */
    @PutMapping("/edit")
    public Result<String> editComment(@RequestBody ArticleCommentEditDTO editDTO) {
        log.info("访客编辑评论: {}", editDTO);
        articleCommentService.editComment(editDTO);
        return Result.success();
    }

    /**
     * 访客删除评论
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteComment(@PathVariable Long id, @RequestParam Long visitorId) {
        log.info("访客删除评论: id={}, visitorId={}", id, visitorId);
        articleCommentService.visitorDeleteComment(id, visitorId);
        return Result.success();
    }
}
