package cc.feitwnd.service.impl;

import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.dto.ArticleCommentDTO;
import cc.feitwnd.dto.ArticleCommentEditDTO;
import cc.feitwnd.dto.ArticleCommentPageQueryDTO;
import cc.feitwnd.dto.ArticleCommentReplyDTO;
import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.exception.ValidationException;
import cc.feitwnd.mapper.ArticleCommentMapper;
import cc.feitwnd.properties.WebsiteProperties;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.ArticleCommentService;
import cc.feitwnd.service.AsyncEmailService;
import cc.feitwnd.service.UserAgentService;
import cc.feitwnd.utils.IpUtil;
import cc.feitwnd.utils.MarkdownUtil;
import cc.feitwnd.vo.ArticleCommentVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章评论服务实现
 */
@Slf4j
@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private UserAgentService userAgentService;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private WebsiteProperties websiteProperties;

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
        // 先查询每条评论的articleId，用于减少对应文章的评论数
        for (Long id : ids) {
            ArticleComments comment = articleCommentMapper.getById(id);
            if (comment != null && comment.getArticleId() != null) {
                articleCommentMapper.decrementCommentCount(comment.getArticleId());
            }
        }
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
        articleComments.setNickname(websiteProperties.getTitle());
        articleComments.setCreateTime(LocalDateTime.now());
        articleComments.setUpdateTime(LocalDateTime.now());

        articleCommentMapper.save(articleComments);

        // 检查父评论是否开启邮箱通知
        notifyParentIfNeeded(articleCommentReplyDTO.getParentId(), "FeiTwnd",
                articleCommentReplyDTO.getContent(), "comment");
    }

    // ===== 博客端方法 =====

    /**
     * 根据文章ID获取评论列表（树形结构）
     * @param articleId
     * @return
     */
    public List<ArticleCommentVO> getCommentTree(Long articleId) {
        List<ArticleCommentVO> allComments = articleCommentMapper.getApprovedByArticleId(articleId);
        // 构建树形结构：根评论（rootId为null或0）作为一级，其余挂到根评论下
        List<ArticleCommentVO> rootComments = new ArrayList<>();
        Map<Long, ArticleCommentVO> commentMap = allComments.stream()
                .collect(Collectors.toMap(ArticleCommentVO::getId, c -> c));

        for (ArticleCommentVO comment : allComments) {
            if (comment.getRootId() == null || comment.getRootId() == 0) {
                // 根评论
                comment.setChildren(new ArrayList<>());
                rootComments.add(comment);
            } else {
                // 子评论，挂到根评论下
                ArticleCommentVO rootComment = commentMap.get(comment.getRootId());
                if (rootComment != null) {
                    if (rootComment.getChildren() == null) {
                        rootComment.setChildren(new ArrayList<>());
                    }
                    rootComment.getChildren().add(comment);
                }
            }
        }
        return rootComments;
    }

    /**
     * 提交评论（添加评论/回复评论）
     * @param articleCommentDTO
     */
    @Transactional
    public void submitComment(ArticleCommentDTO articleCommentDTO, HttpServletRequest request) {
        // 1. 校验邮箱或QQ号
        validateEmailOrQq(articleCommentDTO.getEmailOrQq());

        // 2. 创建评论实体
        ArticleComments articleComments = new ArticleComments();
        BeanUtils.copyProperties(articleCommentDTO, articleComments);

        // 3. 处理Markdown内容
        if (articleCommentDTO.getIsMarkdown() != null && articleCommentDTO.getIsMarkdown() == 1) {
            String html = MarkdownUtil.toHtml(articleCommentDTO.getContent());
            articleComments.setContentHtml(html);
        } else {
            articleComments.setContentHtml(articleCommentDTO.getContent());
        }

        // 4. 设置访客ID
        Long visitorId = articleCommentDTO.getVisitorId();
        articleComments.setVisitorId(visitorId);

        // 5. 获取IP地址信息
        String clientIp = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
        String location = String.format("%s-%s-%s",
                geoInfo.getOrDefault("country", ""),
                geoInfo.getOrDefault("province", ""),
                geoInfo.getOrDefault("city", "")
        );
        articleComments.setLocation(location);

        // 6. 解析UserAgent
        String userAgent = request.getHeader("User-Agent");
        String osName = userAgentService.getOsName(userAgent);
        String browserName = userAgentService.getBrowserName(userAgent);
        articleComments.setUserAgentOs(osName);
        articleComments.setUserAgentBrowser(browserName);

        // 7. 设置默认值
        articleComments.setIsApproved(0);
        articleComments.setIsEdited(0);
        articleComments.setIsAdminReply(0);
        articleComments.setCreateTime(LocalDateTime.now());
        articleComments.setUpdateTime(LocalDateTime.now());

        // 8. 保存到数据库
        articleCommentMapper.save(articleComments);

        // 9. 文章评论数+1
        articleCommentMapper.incrementCommentCount(articleCommentDTO.getArticleId());

        // 10. 检查父评论是否开启邮箱通知
        if (articleCommentDTO.getParentId() != null) {
            notifyParentIfNeeded(articleCommentDTO.getParentId(),
                    articleCommentDTO.getNickname(), articleCommentDTO.getContent(), "comment");
        }

        log.info("访客提交文章评论成功: {}", articleComments);
    }

    /**
     * 访客编辑评论
     */
    public void editComment(ArticleCommentEditDTO editDTO) {
        ArticleComments comment = articleCommentMapper.getById(editDTO.getId());
        if (comment == null) {
            throw new ValidationException("评论不存在");
        }
        if (!comment.getVisitorId().equals(editDTO.getVisitorId())) {
            throw new ValidationException("无权编辑此评论");
        }

        ArticleComments updateComment = new ArticleComments();
        updateComment.setId(editDTO.getId());
        updateComment.setContent(editDTO.getContent());

        if (editDTO.getIsMarkdown() != null && editDTO.getIsMarkdown() == 1) {
            updateComment.setContentHtml(MarkdownUtil.toHtml(editDTO.getContent()));
        } else {
            updateComment.setContentHtml(editDTO.getContent());
        }

        articleCommentMapper.updateContent(updateComment);
        log.info("访客编辑评论成功: id={}, visitorId={}", editDTO.getId(), editDTO.getVisitorId());
    }

    /**
     * 访客删除评论
     */
    public void visitorDeleteComment(Long id, Long visitorId) {
        ArticleComments comment = articleCommentMapper.getById(id);
        if (comment == null) {
            throw new ValidationException("评论不存在");
        }
        if (!comment.getVisitorId().equals(visitorId)) {
            throw new ValidationException("无权删除此评论");
        }

        articleCommentMapper.deleteById(id);
        // 文章评论数-1
        articleCommentMapper.decrementCommentCount(comment.getArticleId());
        log.info("访客删除评论成功: id={}, visitorId={}", id, visitorId);
    }

    /**
     * 检查父评论是否开启邮箱通知，如果是则发送通知邮件
     */
    private void notifyParentIfNeeded(Long parentId, String replyNickname, String replyContent, String type) {
        if (parentId == null) {
            return;
        }
        try {
            ArticleComments parentComment = articleCommentMapper.getById(parentId);
            if (parentComment != null
                    && parentComment.getIsNotice() != null
                    && parentComment.getIsNotice() == 1
                    && parentComment.getEmailOrQq() != null
                    && parentComment.getEmailOrQq().contains("@")) {
                asyncEmailService.sendReplyNotificationAsync(
                        parentComment.getEmailOrQq(),
                        parentComment.getNickname(),
                        parentComment.getContent(),
                        replyNickname,
                        replyContent,
                        type
                );
            }
        } catch (Exception e) {
            log.error("发送评论回复通知邮件异常: parentId={}, ex={}", parentId, e.getMessage());
        }
    }

    /**
     * 校验邮箱或QQ号格式
     */
    private void validateEmailOrQq(String emailOrQq) {
        if (emailOrQq == null || emailOrQq.isEmpty()) {
            throw new ValidationException("请输入邮箱或QQ号");
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String qqRegex = "^[1-9]\\d{4,10}$";
        if (!emailOrQq.matches(emailRegex) && !emailOrQq.matches(qqRegex)) {
            throw new ValidationException("邮箱或QQ号格式不正确");
        }
    }
}
