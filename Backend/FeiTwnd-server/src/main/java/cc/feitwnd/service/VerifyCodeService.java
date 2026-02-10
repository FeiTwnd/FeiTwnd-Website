package cc.feitwnd.service;

/**
 * 验证码服务
 */
public interface VerifyCodeService {
    
    /**
     * 生成验证码
     */
    String generateCode();
    
    /**
     * 保存验证码并设置发送频率
     */
    void saveCode(String code);
    
    /**
     * 邮箱是否可以发送验证码（频率限制）
     */
    boolean canSendCode();
    
    /**
     * 获取剩余验证码冷却时间(秒)
     */
    Long getRemainingCooldown();
    
    /**
     * 是否被锁定
     */
    boolean isLocked();
    
    /**
     * 获取锁定剩余时间（分钟）
     */
    Long getLockRemainingMinutes();
    
    /**
     * 是否允许尝试验证
     */
    boolean canAttempt();
    
    /**
     * 验证验证码
     */
    boolean verifyCode(String code);
    
    /**
     * 获取剩余尝试次数
     */
    Long getRemainingAttempts();
}
