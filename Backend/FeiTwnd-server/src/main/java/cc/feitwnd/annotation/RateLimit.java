package cc.feitwnd.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * 限流类型
     */
    Type type() default Type.IP;

    /**
     * 每秒令牌数
     */
    double tokens() default 10.0;

    /**
     * 突发容量
     */
    int burstCapacity() default 20;

    /**
     * 时间窗口
     */
    int timeWindow() default 1;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流提示消息
     */
    String message() default "请求过于频繁，请稍后再试";

    enum Type {
        IP,        // IP限流
        USER,      // 用户限流
        GLOBAL,    // 全局限流
        ENDPOINT   // 接口限流
    }
}
