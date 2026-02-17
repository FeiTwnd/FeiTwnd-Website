package cc.feitwnd.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流配置类（基于Bucket4j本地令牌桶）
 */
@Data
@Slf4j
@Configuration
public class RateLimitConfiguration {

    /**
     * 本地Bucket缓存: key -> Bucket
     */
    private final ConcurrentHashMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    /**
     * 获取或创建令牌桶
     * @param key 限流key
     * @param burstCapacity 突发容量（桶容量）
     * @param tokens 每个时间窗口补充的令牌数
     * @param duration 时间窗口
     * @return Bucket
     */
    public Bucket resolveBucket(String key, int burstCapacity, long tokens, Duration duration) {
        return bucketCache.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(
                    burstCapacity,
                    Refill.greedy(tokens, duration)
            );
            return Bucket.builder().addLimit(limit).build();
        });
    }

    /**
     * 尝试消费一个令牌
     * @param key 限流key
     * @param burstCapacity 突发容量
     * @param tokens 每个时间窗口补充的令牌数
     * @param duration 时间窗口
     * @return true=允许通过, false=被限流
     */
    public boolean tryConsume(String key, int burstCapacity, long tokens, Duration duration) {
        Bucket bucket = resolveBucket(key, burstCapacity, tokens, duration);
        return bucket.tryConsume(1);
    }
}