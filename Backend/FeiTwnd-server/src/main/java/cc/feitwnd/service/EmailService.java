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
}
