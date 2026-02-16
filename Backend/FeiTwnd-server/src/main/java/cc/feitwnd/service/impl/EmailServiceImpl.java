package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.exception.EmailSendErrorException;
import cc.feitwnd.properties.EmailProperties;
import cc.feitwnd.properties.WebsiteProperties;
import cc.feitwnd.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 邮件服务
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailProperties emailProperties;
    @Autowired
    private WebsiteProperties websiteProperties;

    /**
     * 发送验证码邮件
     * @param code
     * @return
     */
    public void sendVerifyCode(String toEmail, String code){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            helper.setSubject(websiteProperties.getTitle() + "管理端 - 验证码");
            helper.setText(buildSendVerifyCodeEmailContent(code), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送验证码邮件失败 to={}, ex={}", toEmail, e.getMessage());
            throw new EmailSendErrorException(MessageConstant.EMAIL_SEND_ERROR);
        }
    }

    /**
     * 构建发送验证码的邮件内容
     * @param code
     * @return
     */
    private String buildSendVerifyCodeEmailContent(String code) {
        String year = String.valueOf(LocalDate.now().getYear());
        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>"+ websiteProperties.getTitle() +"验证码</title>" +
                "    <style>" +
                "        body {" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            font-family: \"PingFang SC\", \"Microsoft YaHei\", sans-serif;" +
                "            background-color: #f5f5f5;" +
                "        }" +
                "        .email-container {" +
                "            max-width: 600px;" +
                "            margin: 40px auto;" +
                "            background: white;" +
                "            border-radius: 8px;" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);" +
                "            overflow: hidden;" +
                "        }" +
                "        .email-header {" +
                "            background: #333;" +
                "            padding: 24px 0;" +
                "            text-align: center;" +
                "            border-bottom: 1px solid #222;" +
                "        }" +
                "        .email-content {" +
                "            padding: 36px 30px;" +
                "        }" +
                "        .verification-code {" +
                "            background: #f9f9f9;" +
                "            border-radius: 8px;" +
                "            padding: 18px;" +
                "            text-align: center;" +
                "            margin: 26px 0;" +
                "            border: 1px solid #ddd;" +
                "        }" +
                "        .code-display {" +
                "            display: inline-block;" +
                "            background: white;" +
                "            padding: 12px 24px;" +
                "            border-radius: 6px;" +
                "            border: 1px solid #333;" +
                "            margin: 10px 0;" +
                "        }" +
                "        .footer {" +
                "            background: #222;" +
                "            padding: 16px;" +
                "            text-align: center;" +
                "            color: #aaa;" +
                "            font-size: 12px;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='email-container'>" +
                "        <div class='email-header'>" +
                "            <h1 style='color: white; margin: 0; font-size: 24px; font-weight: 500;'>"+websiteProperties.getTitle()+"</h1>" +
                "            <p style='color: #bbb; margin: 8px 0 0; font-size: 14px;'>—— 邮箱验证 ——</p>" +
                "        </div>" +
                "        " +
                "        <div class='email-content'>" +
                "            <h2 style='color: #333; margin: 0 0 20px; font-size: 20px; font-weight: 500;'>您好！</h2>" +
                "            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 25px;'>" +
                "                请使用以下验证码完成邮箱验证：" +
                "            </p>" +
                "            " +
                "            <div class='verification-code'>" +
                "                <p style='color: #666; margin: 0 0 12px; font-size: 14px;'>验证码</p>" +
                "                <div class='code-display'>" +
                "                    <span style='color: #333; font-size: 28px; font-weight: bold; letter-spacing: 6px; font-family: \"Courier New\", monospace;'>" + code + "</span>" +
                "                </div>" +
                "                <p style='color: #888; margin: 12px 0 0; font-size: 13px; font-weight: 500;'>有效期 5 分钟，请及时使用</p>" +
                "            </div>" +
                "        </div>" +
                "        " +
                "        <div class='footer'>" +
                "            <p style='margin: 0; line-height: 1.5;'>" +
                "                此为系统自动发送的邮件，请勿直接回复<br>" +
                "                © "+ year +" "+ websiteProperties.getTitle() +" - 保留所有权利" +
                "            </p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 发送评论/留言回复通知邮件
     */
    public void sendReplyNotification(String toEmail, String parentNickname, String parentContent,
                                      String replyNickname, String replyContent, String type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            String typeText = "comment".equals(type) ? "文章评论" : "留言";
            helper.setSubject(websiteProperties.getTitle() + " - 您的" + typeText + "收到了新回复");
            helper.setText(buildReplyNotificationEmailContent(parentNickname, parentContent,
                    replyNickname, replyContent, typeText), true);
            mailSender.send(message);
            log.info("发送回复通知邮件成功: to={}, type={}", toEmail, type);
        } catch (Exception e) {
            log.error("发送回复通知邮件失败 to={}, type={}, ex={}", toEmail, type, e.getMessage());
        }
    }

    /**
     * 构建回复通知邮件内容
     */
    private String buildReplyNotificationEmailContent(String parentNickname, String parentContent,
                                                       String replyNickname, String replyContent, String typeText) {
        String year = String.valueOf(LocalDate.now().getYear());
        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>"+ websiteProperties.getTitle() +"回复通知</title>" +
                "    <style>" +
                "        body { margin: 0; padding: 0; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; background-color: #f5f5f5; }" +
                "        .email-container { max-width: 600px; margin: 40px auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }" +
                "        .email-header { background: #333; padding: 24px 0; text-align: center; border-bottom: 1px solid #222; }" +
                "        .email-content { padding: 36px 30px; }" +
                "        .quote-block { background: #f9f9f9; border-left: 4px solid #333; padding: 16px 20px; margin: 16px 0; border-radius: 0 8px 8px 0; }" +
                "        .reply-block { background: #f0f7ff; border-left: 4px solid #4a90d9; padding: 16px 20px; margin: 16px 0; border-radius: 0 8px 8px 0; }" +
                "        .footer { background: #222; padding: 16px; text-align: center; color: #aaa; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='email-container'>" +
                "        <div class='email-header'>" +
                "            <h1 style='color: white; margin: 0; font-size: 24px; font-weight: 500;'>"+websiteProperties.getTitle()+"</h1>" +
                "            <p style='color: #bbb; margin: 8px 0 0; font-size: 14px;'>—— " + typeText + "回复通知 ——</p>" +
                "        </div>" +
                "        <div class='email-content'>" +
                "            <h2 style='color: #333; margin: 0 0 20px; font-size: 20px; font-weight: 500;'>" + parentNickname + "，您好！</h2>" +
                "            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 16px;'>您的" + typeText + "收到了来自 <strong>" + replyNickname + "</strong> 的回复：</p>" +
                "            <div class='quote-block'>" +
                "                <p style='color: #888; margin: 0 0 6px; font-size: 13px;'>您的原始内容：</p>" +
                "                <p style='color: #555; margin: 0; font-size: 14px; line-height: 1.6;'>" + parentContent + "</p>" +
                "            </div>" +
                "            <div class='reply-block'>" +
                "                <p style='color: #666; margin: 0 0 6px; font-size: 13px;'>" + replyNickname + " 的回复：</p>" +
                "                <p style='color: #333; margin: 0; font-size: 14px; line-height: 1.6;'>" + replyContent + "</p>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p style='margin: 0; line-height: 1.5;'>此为系统自动发送的邮件，请勿直接回复<br>© " + year + " "+websiteProperties.getTitle()+" - 保留所有权利</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 发送新文章通知邮件
     */
    public void sendNewArticleNotification(String toEmail, String nickname, String articleTitle,
                                           String articleSummary, String articleUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            helper.setSubject(websiteProperties.getTitle() + " - 新文章发布：" + articleTitle);
            helper.setText(buildNewArticleNotificationEmailContent(nickname, articleTitle, articleSummary, articleUrl), true);
            mailSender.send(message);
            log.info("发送新文章通知邮件成功: to={}, title={}", toEmail, articleTitle);
        } catch (Exception e) {
            log.error("发送新文章通知邮件失败 to={}, title={}, ex={}", toEmail, articleTitle, e.getMessage());
        }
    }

    /**
     * 构建新文章通知邮件内容
     */
    private String buildNewArticleNotificationEmailContent(String nickname, String articleTitle,
                                                           String articleSummary, String articleUrl) {
        String year = String.valueOf(LocalDate.now().getYear());
        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>"+websiteProperties.getTitle()+"新文章通知</title>" +
                "    <style>" +
                "        body { margin: 0; padding: 0; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; background-color: #f5f5f5; }" +
                "        .email-container { max-width: 600px; margin: 40px auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }" +
                "        .email-header { background: #333; padding: 24px 0; text-align: center; border-bottom: 1px solid #222; }" +
                "        .email-content { padding: 36px 30px; }" +
                "        .article-card { background: #f9f9f9; border-radius: 8px; padding: 20px 24px; margin: 20px 0; border: 1px solid #eee; }" +
                "        .read-btn { display: inline-block; background: #333; color: white; padding: 12px 28px; border-radius: 6px; text-decoration: none; font-size: 15px; font-weight: 500; margin-top: 10px; }" +
                "        .footer { background: #222; padding: 16px; text-align: center; color: #aaa; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='email-container'>" +
                "        <div class='email-header'>" +
                "            <h1 style='color: white; margin: 0; font-size: 24px; font-weight: 500;'>"+websiteProperties.getTitle()+"</h1>" +
                "            <p style='color: #bbb; margin: 8px 0 0; font-size: 14px;'>—— 新文章发布通知 ——</p>" +
                "        </div>" +
                "        <div class='email-content'>" +
                "            <h2 style='color: #333; margin: 0 0 20px; font-size: 20px; font-weight: 500;'>" + nickname + "，您好！</h2>" +
                "            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 16px;'>您订阅的博客有新文章发布了：</p>" +
                "            <div class='article-card'>" +
                "                <h3 style='color: #333; margin: 0 0 12px; font-size: 18px; font-weight: 600;'>" + articleTitle + "</h3>" +
                "                <p style='color: #666; margin: 0 0 16px; font-size: 14px; line-height: 1.6;'>" + (articleSummary != null ? articleSummary : "") + "</p>" +
                "                <a href='" + articleUrl + "' class='read-btn' style='color: white;'>阅读全文 →</a>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p style='margin: 0; line-height: 1.5;'>此为系统自动发送的邮件，请勿直接回复<br>© " + year + " "+websiteProperties.getTitle()+" - 保留所有权利</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

}
