package cc.feitwnd.service;

import cc.feitwnd.entity.ArticleComments;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.entity.Messages;
import cc.feitwnd.mapper.ArticleCommentMapper;
import cc.feitwnd.mapper.ArticleMapper;
import cc.feitwnd.mapper.MessageMapper;
import cc.feitwnd.properties.EmailProperties;
import cc.feitwnd.service.impl.ArticleCommentServiceImpl;
import cc.feitwnd.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 邮件通知逻辑单元测试
 * 覆盖：
 * 1. 根评论/根留言提交时通知站长，评论场景携带文章标题；
 * 2. 访客回复站长（isAdminReply=1）时，站长收到与普通用户形式一致的回复通知邮件；
 * 3. 访客回复普通用户（开启邮箱通知）时，仍按原逻辑通知该用户；
 * 4. 未配置站长通知邮箱时跳过站长通知。
 */
@ExtendWith(MockitoExtension.class)
class EmailNotificationTest {

    @Mock
    private ArticleCommentMapper articleCommentMapper;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private AsyncEmailService asyncEmailService;

    @Mock
    private EmailProperties emailProperties;

    private ArticleCommentServiceImpl commentService;

    private MessageServiceImpl messageService;

    @BeforeEach
    void setUp() {
        commentService = new ArticleCommentServiceImpl();
        ReflectionTestUtils.setField(commentService, "articleCommentMapper", articleCommentMapper);
        ReflectionTestUtils.setField(commentService, "articleMapper", articleMapper);
        ReflectionTestUtils.setField(commentService, "asyncEmailService", asyncEmailService);
        ReflectionTestUtils.setField(commentService, "emailProperties", emailProperties);

        messageService = new MessageServiceImpl();
        ReflectionTestUtils.setField(messageService, "messageMapper", messageMapper);
        ReflectionTestUtils.setField(messageService, "asyncEmailService", asyncEmailService);
        ReflectionTestUtils.setField(messageService, "emailProperties", emailProperties);
    }

    /**
     * 根评论提交时通知站长，并携带文章标题
     */
    @Test
    void rootComment_notifiesAdmin_withArticleTitle() {
        Articles article = new Articles();
        article.setTitle("测试文章标题");
        when(articleMapper.getById(100L)).thenReturn(article);

        ReflectionTestUtils.invokeMethod(commentService, "notifyAdminIfNeeded", null, "访客", "评论内容", 100L);

        verify(asyncEmailService).sendAdminContentNotificationAsync(
                eq("comment"), eq("访客"), eq("评论内容"), eq("测试文章标题"));
    }

    /**
     * 根留言提交时通知站长，留言无文章标题传 null
     */
    @Test
    void rootMessage_notifiesAdmin_withoutArticleTitle() {
        ReflectionTestUtils.invokeMethod(messageService, "notifyAdminIfNeeded", null, "访客", "留言内容");

        verify(asyncEmailService).sendAdminContentNotificationAsync(
                eq("message"), eq("访客"), eq("留言内容"), eq(null));
    }

    /**
     * 访客回复站长评论（父评论 isAdminReply=1）时，通知站长邮箱，形式与普通用户一致
     */
    @Test
    void replyToAdminComment_notifiesAdminEmail() {
        ArticleComments adminComment = new ArticleComments();
        adminComment.setIsAdminReply(1);
        adminComment.setNickname("站点标题");
        adminComment.setContent("站长原评论");
        when(articleCommentMapper.getById(50L)).thenReturn(adminComment);
        when(emailProperties.getAdminNotifyTo()).thenReturn("admin@example.com");

        ReflectionTestUtils.invokeMethod(commentService, "notifyParentIfNeeded", 50L, "访客", "访客回复", "comment");

        verify(asyncEmailService).sendReplyNotificationAsync(
                eq("admin@example.com"), eq("站点标题"), eq("站长原评论"),
                eq("访客"), eq("访客回复"), eq("comment"));
    }

    /**
     * 访客回复站长留言（父留言 isAdminReply=1）时，通知站长邮箱
     */
    @Test
    void replyToAdminMessage_notifiesAdminEmail() {
        Messages adminMessage = new Messages();
        adminMessage.setIsAdminReply(1);
        adminMessage.setNickname("站点标题");
        adminMessage.setContent("站长原留言");
        when(messageMapper.getById(60L)).thenReturn(adminMessage);
        when(emailProperties.getAdminNotifyTo()).thenReturn("admin@example.com");

        ReflectionTestUtils.invokeMethod(messageService, "notifyParentIfNeeded", 60L, "访客", "访客回复", "message");

        verify(asyncEmailService).sendReplyNotificationAsync(
                eq("admin@example.com"), eq("站点标题"), eq("站长原留言"),
                eq("访客"), eq("访客回复"), eq("message"));
    }

    /**
     * 访客回复普通用户评论（开启邮箱通知）时，仍按原逻辑通知该用户邮箱
     */
    @Test
    void replyToVisitorComment_notifiesVisitorEmail() {
        ArticleComments visitorComment = new ArticleComments();
        visitorComment.setIsAdminReply(0);
        visitorComment.setIsNotice(1);
        visitorComment.setEmailOrQq("visitor@example.com");
        visitorComment.setNickname("访客甲");
        visitorComment.setContent("访客甲原评论");
        when(articleCommentMapper.getById(70L)).thenReturn(visitorComment);

        ReflectionTestUtils.invokeMethod(commentService, "notifyParentIfNeeded", 70L, "访客乙", "访客乙回复", "comment");

        verify(asyncEmailService).sendReplyNotificationAsync(
                eq("visitor@example.com"), eq("访客甲"), eq("访客甲原评论"),
                eq("访客乙"), eq("访客乙回复"), eq("comment"));
    }

    /**
     * 回复对象不是站长回复（isAdminReply 为空）且未开启通知时，不发任何邮件
     */
    @Test
    void replyToVisitorWithoutNotice_sendsNothing() {
        ArticleComments visitorComment = new ArticleComments();
        visitorComment.setIsAdminReply(0);
        visitorComment.setIsNotice(0);
        when(articleCommentMapper.getById(80L)).thenReturn(visitorComment);

        ReflectionTestUtils.invokeMethod(commentService, "notifyParentIfNeeded", 80L, "访客乙", "访客乙回复", "comment");

        verify(asyncEmailService, never()).sendReplyNotificationAsync(
                eq("visitor@example.com"), eq("访客甲"), eq("访客甲原评论"),
                eq("访客乙"), eq("访客乙回复"), eq("comment"));
    }

    /**
     * 站长通知邮箱未配置时，回复站长也不发通知（避免空收件人）
     */
    @Test
    void replyToAdmin_withoutAdminEmail_sendsNothing() {
        ArticleComments adminComment = new ArticleComments();
        adminComment.setIsAdminReply(1);
        when(articleCommentMapper.getById(90L)).thenReturn(adminComment);
        when(emailProperties.getAdminNotifyTo()).thenReturn(null);

        ReflectionTestUtils.invokeMethod(commentService, "notifyParentIfNeeded", 90L, "访客", "访客回复", "comment");

        verify(asyncEmailService, never()).sendReplyNotificationAsync(
                eq("admin@example.com"), eq("站点标题"), eq("站长原评论"),
                eq("访客"), eq("访客回复"), eq("comment"));
    }
}
