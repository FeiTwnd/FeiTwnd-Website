package cc.feitwnd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置redis连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置key的序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 设置value的序列化器
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 初始化RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * 配置Spring Cache使用Redis作为缓存后端
     * 不同缓存空间使用不同的TTL策略
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 默认缓存配置：30分钟过期
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // 不同缓存空间的TTL策略
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 静态数据：很少变化，缓存1小时
        cacheConfigurations.put("personalInfo", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("socialMedia", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("skills", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("experiences", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("friendLinks", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 文章相关：适中变化频率，缓存30分钟
        cacheConfigurations.put("articleCategories", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("articleTags", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("articleList", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("articleDetail", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("articleArchive", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 统计数据：变化频繁，短时间缓存
        cacheConfigurations.put("blogReport", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Sitemap/RSS Feed：缓存30分钟
        cacheConfigurations.put("sitemap", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("rssFeed", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}