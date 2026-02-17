package cc.feitwnd.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    /**
     * 创建自定义的 Jackson2JsonRedisSerializer，解决集合类型序列化问题
     */
    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 启用默认类型信息
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        // 配置可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 配置序列化特性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 注册 JavaTimeModule 以处理 Java 8 时间类型
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
                                                       GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置redis连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置key的序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 设置value的序列化器 - 使用自定义的序列化器
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
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
                                     GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {

        // 默认缓存配置：30分钟过期，使用自定义的序列化器
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
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