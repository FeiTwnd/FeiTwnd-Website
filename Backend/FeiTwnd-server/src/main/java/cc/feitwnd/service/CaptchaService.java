package cc.feitwnd.service;

/**
 * 验证码服务：服务端存储验证码结果，防止客户端泄露
 */
public interface CaptchaService {

    /**
     * 存储验证码结果
     */
    void put(String captchaId, int result);

    /**
     * 校验验证码，校验后立即删除（一次性使用）
     */
    boolean verify(String captchaId, int answer);

    /**
     * 每 5 分钟清理超过 5 分钟的验证码
     */
    void cleanExpired();
}
