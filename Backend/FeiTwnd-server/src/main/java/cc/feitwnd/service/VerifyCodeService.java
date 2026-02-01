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
    public void saveCode(String code) {
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

    // 获取剩余验证码冷却时间(秒)
    public Long getRemainingCooldown() {
        Long ttl = redis.getExpire(KEY_RATE_LIMIT, TimeUnit.SECONDS);
        return ttl != null ? Math.max(ttl, 0) : 0;
    }

    // 是否被锁定
    public boolean isLocked() {
        return Boolean.TRUE.equals(redis.hasKey(KEY_LOCK));
    }

    // 获取锁定剩余时间（分钟）
    public Long getLockRemainingMinutes() {
        Long ttl = redis.getExpire(KEY_LOCK, TimeUnit.MINUTES);
        return ttl != null ? Math.max(ttl, 0) : 0;
    }

    // 是否允许尝试验证
    public boolean canAttempt() {
        // 检查是否被锁定
        if (isLocked()) {
            return false;
        }
        return true;
    }

    // 验证验证码
    public boolean verifyCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        // 检查是否被锁定
        if (isLocked()) {
            return false;
        }

        // 检查验证码是否存在
        String savedCode = (String) redis.opsForValue().get(KEY_VERIFY_CODE);
        if (savedCode == null) {
            // 验证失败，记录尝试
            recordFailedAttempt();
            return false;
        }

        // 验证
        boolean success = savedCode.equals(code.trim());

        if (success) {
            // 验证成功
            clearAll(); // 清空所有验证相关数据
            return true;
        } else {
            // 验证失败，记录尝试
            recordFailedAttempt();
            return false;
        }
    }

    // 记录失败尝试
    private void recordFailedAttempt() {
        // 增加失败计数
        Long attemptCount = redis.opsForValue().increment(KEY_ATTEMPT_COUNT, 1L);
        if (attemptCount == null) {
            attemptCount = 1L;
        }

        // 如果是第一次失败，设置过期时间
        if (attemptCount == 1) {
            Long codeTtl = redis.getExpire(KEY_VERIFY_CODE, TimeUnit.SECONDS);
            if (codeTtl > 0) {
                redis.expire(KEY_ATTEMPT_COUNT, codeTtl, TimeUnit.SECONDS);
            }
        }

        // 达到最大尝试次数，锁定
        if (attemptCount >= MAX_ATTEMPTS) {
            redis.opsForValue().set(KEY_LOCK, "1", LOCK_MINUTES, TimeUnit.MINUTES);
        }
    }

    // 获取当前尝试次数
    public Long getAttemptCount() {
        try {
            Integer count = (Integer) redis.opsForValue().get(KEY_ATTEMPT_COUNT);
            if (count == null) {
                return 0L;
            }
            // 转化类型
            if (count instanceof Integer) {
                return (count).longValue();
            }else {
                return 0L;
            }
        } catch (Exception e) {
            return 0L;
        }
    }

    // 获取剩余尝试次数
    public Long getRemainingAttempts() {
        if (isLocked()) {
            return 0L;
        }
        Long attemptCount = getAttemptCount();
        return Math.max(MAX_ATTEMPTS - attemptCount, 0);
    }

    // 重置状态
    public void clearAll() {
        redis.delete(KEY_VERIFY_CODE);
        redis.delete(KEY_RATE_LIMIT);
        redis.delete(KEY_ATTEMPT_COUNT);
        redis.delete(KEY_LOCK);
    }
}
