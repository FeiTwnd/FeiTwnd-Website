package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.entity.Visitors;
import cc.feitwnd.exception.BlockedException;
import cc.feitwnd.mapper.VisitorMapper;
import cc.feitwnd.service.BlockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BlockServiceImpl implements BlockService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String RATE_LIMIT_KEY = "visitor:rate:";
    private static final String BLOCKED_KEY = "visitor:blocked:";

    /**
     * 检查缓存是否有被封禁记录
     * @param fingerprint
     */
    public void checkIfBlocked(String fingerprint) {
        // 先检查Redis缓存
        String blockedKey = BLOCKED_KEY + fingerprint;
        Boolean isBlocked = redisTemplate.hasKey(blockedKey);

        if(isBlocked){
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }
        // 检查数据库
        Visitors visitor = visitorMapper.findVisitorByFingerprint(fingerprint);
        if (visitor != null && visitor.getIsBlocked() == 1) {
            if (visitor.getExpiresAt() == null || visitor.getExpiresAt().isBefore(LocalDateTime.now())) {
                // 封禁有效，更新Redis缓存
                redisTemplate.opsForValue().set(blockedKey, "1", 1, TimeUnit.DAYS);
                throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
            } else {
                // 封禁已过期，解除封禁
                visitor.setIsBlocked(0);
                visitorMapper.updateById(visitor);
            }
        }
    }

    /**
     * 检查请求频率
     * @param fingerprint
     * @param ip
     */
    public void checkRateLimit(String fingerprint, String ip) {
        // IP级别限制：每分钟60次
        String ipKey = RATE_LIMIT_KEY + "ip:" + ip;
        Long ipCount = redisTemplate.opsForValue().increment(ipKey, 1);
        if (ipCount == 1) {
            redisTemplate.expire(ipKey, 1, TimeUnit.MINUTES);
        }
        if (ipCount > 60) {
            // 自动封禁
            blockVisitor(fingerprint);
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }

        // 指纹级别限制：每小时1000次
        String fpKey = RATE_LIMIT_KEY + "fp:" + fingerprint;
        Long fpCount = redisTemplate.opsForValue().increment(fpKey, 1);
        if (fpCount == 1) {
            redisTemplate.expire(fpKey, 1, TimeUnit.HOURS);
        }
        if (fpCount > 1000) {
            // 自动封禁
            blockVisitor(fingerprint);
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }
    }

    /**
     * 封禁访客
     * @param fingerprint
     */
    private void blockVisitor(String fingerprint) {
        Visitors visitor = visitorMapper.findVisitorByFingerprint(fingerprint);
        if (visitor != null) {
            visitor.setIsBlocked(1);
            // 封1天
            visitor.setExpiresAt(LocalDateTime.now().plusDays(1));
            visitorMapper.updateById(visitor);

            // 更新Redis缓存
            String blockedKey = BLOCKED_KEY + fingerprint;
            redisTemplate.opsForValue().set(blockedKey, "1", 1, TimeUnit.DAYS);
            log.warn("封禁访客: fingerprint={}",
                    fingerprint);
        }
    }
}
