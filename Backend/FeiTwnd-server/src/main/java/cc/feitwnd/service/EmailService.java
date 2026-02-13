package cc.feitwnd.service;

/**
 * 邮件服务
 */
public interface EmailService {
    
    /**
     * 发送验证码邮件
     * @param toEmail
     * @param code
     */
    void sendVerifyCode(String toEmail, String code);

    /**
     * 发送评论/留言回复通知邮件
     * @param toEmail 收件人邮箱
     * @param parentNickname 被回复人昵称
     * @param parentContent 被回复的内容
     * @param replyNickname 回复人昵称
     * @param replyContent 回复内容
     * @param type 类型：message-留言 / comment-文章评论
     */
    void sendReplyNotification(String toEmail, String parentNickname, String parentContent,
                               String replyNickname, String replyContent, String type);
}
