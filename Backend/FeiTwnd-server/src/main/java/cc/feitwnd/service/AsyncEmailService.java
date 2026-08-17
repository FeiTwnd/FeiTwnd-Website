package cc.feitwnd.service;

/**
 * 异步邮件服务（通过独立 Service 确保 @Async 代理生效）
 */
public interface AsyncEmailService {

    /**
     * 异步发送评论/留言回复通知邮件
     */
    void sendReplyNotificationAsync(String toEmail, String parentNickname, String parentContent,
                                    String replyNickname, String replyContent, String type);

    /**
     * 异步发送站长的新评论/留言通知邮件
     * @param type 类型：message-留言 / comment-文章评论
     * @param nickname 提交人昵称
     * @param content 内容
     * @param articleTitle 关联的文章标题，留言无文章时传 null
     */
    void sendAdminContentNotificationAsync(String type, String nickname, String content, String articleTitle);

    /**
     * 异步发送新文章通知邮件
     */
    void sendNewArticleNotificationAsync(String toEmail, String nickname, String articleTitle,
                                         String articleSummary, String articleUrl);
}
