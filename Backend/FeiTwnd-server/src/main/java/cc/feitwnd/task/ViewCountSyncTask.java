package cc.feitwnd.task;

import cc.feitwnd.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 文章浏览量定时同步任务
 * 将Redis中累积的浏览量增量批量同步到MySQL
 */
@Slf4j
@Component
public class ViewCountSyncTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ArticleMapper articleMapper;

    private static final String VIEW_COUNT_KEY = "article:viewCount";

    /**
     * 每5分钟将Redis中的浏览量增量同步到MySQL
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void syncViewCountToMySQL() {
        try {
            Map<Object, Object> viewCounts = redisTemplate.opsForHash().entries(VIEW_COUNT_KEY);
            if (viewCounts == null || viewCounts.isEmpty()) {
                return;
            }

            int syncCount = 0;
            for (Map.Entry<Object, Object> entry : viewCounts.entrySet()) {
                Long articleId = Long.parseLong(entry.getKey().toString());
                int increment = ((Number) entry.getValue()).intValue();
                if (increment <= 0) {
                    continue;
                }

                // 批量累加到MySQL
                articleMapper.addViewCount(articleId, increment);
                // 使用原子操作减去已同步的值，避免丢失同步期间新增的浏览量
                Long remaining = redisTemplate.opsForHash().increment(VIEW_COUNT_KEY, entry.getKey(), -increment);
                // 如果剩余值<=0，清理该key
                if (remaining != null && remaining <= 0) {
                    redisTemplate.opsForHash().delete(VIEW_COUNT_KEY, entry.getKey());
                }
                syncCount++;
            }

            if (syncCount > 0) {
                log.info("浏览量同步完成，共同步 {} 篇文章", syncCount);
            }
        } catch (Exception e) {
            log.error("浏览量同步异常: {}", e.getMessage());
        }
    }
}
