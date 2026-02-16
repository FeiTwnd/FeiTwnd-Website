package cc.feitwnd.service.impl;

import cc.feitwnd.service.AsyncEmailService;
import cc.feitwnd.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步邮件服务实现（独立 Service 保证 @Async 代理生效）
 */
@Service
@Slf4j
public class AsyncEmailServiceImpl implements AsyncEmailService {

    @Autowired
    private EmailService emailService;

    /**
     * 异步发送评论/留言回复通知邮件
     */
    @Async("taskExecutor")
    public void sendReplyNotificationAsync(String toEmail, String parentNickname, String parentContent,
                                           String replyNickname, String replyContent, String type) {
        try {
            emailService.sendReplyNotification(toEmail, parentNickname, parentContent,
                    replyNickname, replyContent, type);
        } catch (Exception e) {
            log.error("异步发送回复通知邮件失败: to={}, type={}, ex={}", toEmail, type, e.getMessage());
        }
    }

    /**
     * 异步发送新文章通知邮件
     */
    @Async("taskExecutor")
    public void sendNewArticleNotificationAsync(String toEmail, String nickname, String articleTitle,
                                                String articleSummary, String articleUrl) {
        try {
            emailService.sendNewArticleNotification(toEmail, nickname, articleTitle, articleSummary, articleUrl);
        } catch (Exception e) {
            log.error("异步发送新文章通知邮件失败: to={}, title={}, ex={}", toEmail, articleTitle, e.getMessage());
        }
    }
}
