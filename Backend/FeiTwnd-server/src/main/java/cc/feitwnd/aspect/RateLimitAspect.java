package cc.feitwnd.aspect;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.config.RateLimitConfig;
import cc.feitwnd.exception.BlockedException;
import cc.feitwnd.utils.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * 限流切面，处理 @RateLimit 注解
 * 基于Bucket4j令牌桶算法实现接口级限流
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private RateLimitConfig rateLimitConfig;

    /**
     * 环绕通知：拦截带有 @RateLimit 注解的方法
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = buildRateLimitKey(joinPoint, rateLimit);
        Duration duration = Duration.of(rateLimit.timeWindow(), rateLimit.timeUnit().toChronoUnit());

        boolean allowed = rateLimitConfig.tryConsume(
                key,
                rateLimit.burstCapacity(),
                (long) rateLimit.tokens(),
                duration
        );

        if (allowed) {
            return joinPoint.proceed();
        } else {
            log.warn("限流触发: key={}, type={}, message={}", key, rateLimit.type(), rateLimit.message());
            // 403封禁异常
            throw new BlockedException(rateLimit.message());
        }
    }

    /**
     * 构建限流Key
     */
    private String buildRateLimitKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getMethod().getName();
        String endpointKey = className + "." + methodName;

        StringBuilder key = new StringBuilder("rate_limit:");

        switch (rateLimit.type()) {
            case IP:
                String ip = getClientIp();
                key.append("ip:").append(endpointKey).append(":").append(ip);
                break;

            case ENDPOINT:
                key.append("endpoint:").append(endpointKey);
                break;

            case GLOBAL:
                key.append("global");
                break;
        }

        return key.toString();
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return IpUtil.getClientIp(request);
        }
        return "unknown";
    }
}
