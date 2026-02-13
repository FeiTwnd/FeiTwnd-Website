package cc.feitwnd.service.impl;

import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.dto.ArticleCommentPageQueryDTO;
import cc.feitwnd.dto.ArticleCommentReplyDTO;
import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.mapper.ArticleCommentMapper;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.ArticleCommentService;
import cc.feitwnd.utils.MarkdownUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章评论服务实现
 */
@Slf4j
@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    /**
     * 分页条件查询评论（时间、是否审核）
     * @param articleCommentPageQueryDTO
     * @return
     */
    public PageResult pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO) {
        PageHelper.startPage(articleCommentPageQueryDTO.getPage(), articleCommentPageQueryDTO.getPageSize());
        Page<ArticleComments> page = (Page<ArticleComments>) articleCommentMapper.pageQuery(articleCommentPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据文章ID查询评论
     * @param articleId
     * @return
     */
    public List<ArticleComments> getByArticleId(Long articleId) {
        return articleCommentMapper.getByArticleId(articleId);
    }

    /**
     * 批量审核通过评论
     * @param ids
     */
    public void batchApprove(List<Long> ids) {
        articleCommentMapper.batchApprove(ids);
    }

    /**
     * 批量删除评论
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        articleCommentMapper.batchDelete(ids);
    }

    /**
     * 管理员回复评论
     * @param articleCommentReplyDTO
     */
    public void adminReply(ArticleCommentReplyDTO articleCommentReplyDTO) {
        ArticleComments articleComments = new ArticleComments();
        BeanUtils.copyProperties(articleCommentReplyDTO, articleComments);

        // 处理Markdown内容
        if (articleCommentReplyDTO.getIsMarkdown() != null && articleCommentReplyDTO.getIsMarkdown() == 1) {
            String html = MarkdownUtil.toHtml(articleCommentReplyDTO.getContent());
            articleComments.setContentHtml(html);
        } else {
            articleComments.setContentHtml(articleCommentReplyDTO.getContent());
        }

        // 设置管理员回复标识
        articleComments.setIsAdminReply(StatusConstant.ENABLE);
        articleComments.setIsApproved(StatusConstant.ENABLE);
        articleComments.setIsEdited(StatusConstant.DISABLE);
        articleComments.setNickname("FeiTwnd");
        articleComments.setCreateTime(LocalDateTime.now());
        articleComments.setUpdateTime(LocalDateTime.now());

        articleCommentMapper.save(articleComments);
    }
}
