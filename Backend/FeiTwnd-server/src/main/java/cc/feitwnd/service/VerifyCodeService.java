package cc.feitwnd.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VerifyCodeService {

    // Redis key
    private static final String KEY_VERIFY_CODE = "verify_code";
    private static final String KEY_RATE_LIMIT = "rate_limit";
    private static final String KEY_ATTEMPT_COUNT = "attempt_count";
    private static final String KEY_LOCK = "lock";

    // 时间常量
    private static final int RATE_LIMIT_SECONDS = 60; // 发送频率限制60秒
    private static final int CODE_TTL_MINUTES = 5;    // 验证码有效期5分钟
    private static final int MAX_ATTEMPTS = 5;        // 最大尝试次数
    private static final int LOCK_MINUTES = 30;       // 锁定30分钟

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redis;

    // 生成验证码
    public String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    // 保存验证码并设置发送频率
    public void saveCode(String code){
        // 保存验证码
        redis.opsForValue().set(KEY_VERIFY_CODE, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        // 设置发送频率限制
        redis.opsForValue().set(KEY_RATE_LIMIT, "1", RATE_LIMIT_SECONDS, TimeUnit.SECONDS);
        // 重置尝试计数和锁定状态
        redis.delete(KEY_ATTEMPT_COUNT);
        redis.delete(KEY_LOCK);
    }

    // 邮箱是否可以发送验证码（频率限制）
    public boolean canSendCode() {
        return redis.opsForValue().get(KEY_RATE_LIMIT) == null;
    }


     // 获取剩余冷却时间(秒)
    public Long getRemainingCooldown() {
        return redis.getExpire(KEY_RATE_LIMIT, TimeUnit.SECONDS);
    }

    // 验证验证码
    public boolean verifyCode(String code) {
        if(code == null || code.trim().isEmpty()){
            return false;
        }

        String savedCode = (String) redis.opsForValue().get(KEY_VERIFY_CODE);
        if (savedCode == null) {
            return false;
        }

        return savedCode.equals(code.trim());
    }

    // 是否允许尝试验证
    public boolean canAttempt() {
        // 检查是否被锁定
        if (redis.opsForValue().get(KEY_LOCK) != null) {
            return false;
        }

        // 检查验证码是否存在
        return redis.opsForValue().get(KEY_VERIFY_CODE) != null;
    }

    // 获取锁定剩余时间（分钟）
    public Long getLockRemainingMinutes() {
        return redis.getExpire(KEY_LOCK, TimeUnit.MINUTES);
    }

    // 记录尝试验证结果
    public void recordAttempt(boolean success) {
        if (success) {
            // 验证成功，清除计数和锁定
            redis.delete(KEY_ATTEMPT_COUNT);
            redis.delete(KEY_LOCK);
            log.info("验证成功，已重置安全计数");
            return;
        }

        // 验证失败，增加失败计数
        Long attemptCount = redis.opsForValue().increment(KEY_ATTEMPT_COUNT);
        if (attemptCount == 1) {
            // 首次失败，设置过期时间与验证码同步
            Long codeTtl = redis.getExpire(KEY_VERIFY_CODE, TimeUnit.SECONDS);
            if (codeTtl != null && codeTtl > 0) {
                redis.expire(KEY_ATTEMPT_COUNT, codeTtl, TimeUnit.SECONDS);
            }
        }

        log.info("验证失败，当前失败次数: {}", attemptCount);

        // 达到最大尝试次数，锁定
        if (attemptCount != null && attemptCount >= MAX_ATTEMPTS) {
            redis.opsForValue().set(KEY_LOCK, "1", LOCK_MINUTES, TimeUnit.MINUTES);
            log.warn("验证失败次数过多，已锁定 {} 分钟", LOCK_MINUTES);
        }
    }

    // 重置状态
    public void clearAll() {
        redis.delete(KEY_VERIFY_CODE);
        redis.delete(KEY_RATE_LIMIT);
        redis.delete(KEY_ATTEMPT_COUNT);
        redis.delete(KEY_LOCK);
    }
}
