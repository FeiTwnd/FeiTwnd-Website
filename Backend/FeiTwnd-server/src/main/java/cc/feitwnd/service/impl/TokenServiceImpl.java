package cc.feitwnd.service.impl;

import cc.feitwnd.constant.JwtClaimsConstant;
import cc.feitwnd.properties.JwtProperties;
import cc.feitwnd.service.TokenService;
import cc.feitwnd.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Override

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redis;
    @Autowired
    private JwtProperties jwtProperties;

    private static final String TOKEN_PREFIX = "token:active:";

    /**
     * 创建并保存token
     */
    public String createAndStoreToken(Long userId) {
        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID,userId);
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        // 将token存储至Redis,用set可以多端登录
        String tokenKey = TOKEN_PREFIX + userId;
        redis.opsForSet().add(tokenKey,token,jwtProperties.getTtl(), TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 验证token有效性
     */
    @Override
    public boolean isValidToken(Long userId, String token) {
        String key = TOKEN_PREFIX + userId;
        return Boolean.TRUE.equals(redis.opsForSet().isMember(key, token));
    }

    /**
     * 退出登录 - 删除token
     */
    @Override
    public void logout(Long userId, String token) {
        String key = TOKEN_PREFIX + userId;
        redis.opsForSet().remove(key, token);
    }

    /**
     * 退出登录 - 删除所有token
     */
    @Override
    public void logoutAll(Long userId) {
        String key = TOKEN_PREFIX + userId;
        redis.delete(key);
    }
}
