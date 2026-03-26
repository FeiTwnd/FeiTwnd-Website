package cc.feitwnd.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限流配置类（基于Bucket4j本地令牌桶）
 */
@Data
@Slf4j
@Configuration
public class RateLimitConfiguration {

    /** 桶默认最短保留时长（毫秒） */
    private static final long MIN_BUCKET_TTL_MS = 60_000L;
    /** 桶过期时间倍率（相对注解时间窗口） */
    private static final int TTL_MULTIPLIER = 3;
    /** 清理周期（毫秒） */
    private static final long CLEANUP_INTERVAL_MS = 60_000L;
    /** 桶缓存上限（防止内存无限增长） */
    private static final int MAX_BUCKET_CACHE_SIZE = 20_000;

    /**
     * 本地Bucket缓存: key -> Bucket
     */
    private final ConcurrentHashMap<String, BucketHolder> bucketCache = new ConcurrentHashMap<>();
    private final AtomicLong lastCleanupTime = new AtomicLong(0L);

    /**
     * 获取或创建令牌桶
     * @param key 限流key
     * @param burstCapacity 突发容量（桶容量）
     * @param tokens 每个时间窗口补充的令牌数
     * @param duration 时间窗口
     * @return Bucket
     */
    public Bucket resolveBucket(String key, int burstCapacity, long tokens, Duration duration) {
        long now = System.currentTimeMillis();
        cleanupIfNeeded(now);

        BucketHolder holder = bucketCache.compute(key, (k, old) -> {
            long expireAt = calculateExpireAt(now, duration);
            if (old != null && old.expireAt > now) {
                old.touch(now, expireAt);
                return old;
            }

            Bandwidth limit = Bandwidth.classic(
                    burstCapacity,
                    Refill.greedy(tokens, duration)
            );
            Bucket bucket = Bucket.builder().addLimit(limit).build();
            return new BucketHolder(bucket, now, expireAt);
        });

        // 双重保险：若扫描流量导致key瞬时暴涨，立即触发一次压缩。
        if (bucketCache.size() > MAX_BUCKET_CACHE_SIZE) {
            compactCache();
        }
        return holder.bucket;
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

    private long calculateExpireAt(long now, Duration duration) {
        long ttl = Math.max(duration.toMillis() * TTL_MULTIPLIER, MIN_BUCKET_TTL_MS);
        return now + ttl;
    }

    private void cleanupIfNeeded(long now) {
        long last = lastCleanupTime.get();
        if (now - last < CLEANUP_INTERVAL_MS) {
            return;
        }
        if (!lastCleanupTime.compareAndSet(last, now)) {
            return;
        }

        int before = bucketCache.size();
        bucketCache.entrySet().removeIf(entry -> entry.getValue().expireAt <= now);

        if (bucketCache.size() > MAX_BUCKET_CACHE_SIZE) {
            compactCache();
        }

        int after = bucketCache.size();
        if (before != after) {
            log.debug("限流桶清理完成: before={}, after={}", before, after);
        }
    }

    private void compactCache() {
        int currentSize = bucketCache.size();
        if (currentSize <= MAX_BUCKET_CACHE_SIZE) {
            return;
        }

        int targetRemove = currentSize - MAX_BUCKET_CACHE_SIZE + (MAX_BUCKET_CACHE_SIZE / 10);
        if (targetRemove <= 0) {
            return;
        }

        ArrayList<Map.Entry<String, BucketHolder>> entries = new ArrayList<>(bucketCache.entrySet());
        entries.sort(Comparator.comparingLong(e -> e.getValue().lastAccess));

        int removed = 0;
        for (Map.Entry<String, BucketHolder> entry : entries) {
            if (removed >= targetRemove) {
                break;
            }
            if (bucketCache.remove(entry.getKey(), entry.getValue())) {
                removed++;
            }
        }
        log.warn("限流桶触发压缩: removed={}, current={}", removed, bucketCache.size());
    }

    private static class BucketHolder {
        private final Bucket bucket;
        private volatile long lastAccess;
        private volatile long expireAt;

        private BucketHolder(Bucket bucket, long lastAccess, long expireAt) {
            this.bucket = bucket;
            this.lastAccess = lastAccess;
            this.expireAt = expireAt;
        }

        private void touch(long access, long expire) {
            this.lastAccess = access;
            this.expireAt = expire;
        }
    }
}