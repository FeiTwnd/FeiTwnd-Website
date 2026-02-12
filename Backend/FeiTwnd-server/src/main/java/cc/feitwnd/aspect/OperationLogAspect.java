package cc.feitwnd.aspect;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.context.BaseContext;
import cc.feitwnd.entity.OperationLogs;
import cc.feitwnd.service.SaveLogAsyncService;
import cc.feitwnd.service.operationLogService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义切面类，用于记录操作日志
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private SaveLogAsyncService saveLogAsyncService;

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(cc.feitwnd.annotation.OperationLog)")
    public void operationLogPointCut() {
    }

    /**
     * 环绕通知
     */
    @Around("operationLogPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Throwable error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e; // 重新抛出异常
        } finally {
            // 获取注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象
            Method method = signature.getMethod(); // 方法对象
            OperationLog operationLog = method.getAnnotation(OperationLog.class); // 获取方法上的注解对象

            if (operationLog != null) {
                if (operationLog.async()) {
                    // 异步记录操作日志
                    saveLogAsyncService.saveLogAsync(joinPoint, result, error, operationLog);
                }
            }
        }
    }
}
