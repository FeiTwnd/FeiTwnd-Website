package cc.feitwnd.aspect;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.context.BaseContext;
import cc.feitwnd.entity.OperationLogs;
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
    private operationLogService operationLogService;

    /**
     * SpEL表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 参数名发现器
     */
    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

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
                    saveLogAsync(joinPoint, result, error, operationLog);
                } else {
                    saveLogSync(joinPoint, result, error, operationLog);
                }
            }
        }
    }

    /**
     * 异步保存日志
     */
    @Async
    public void saveLogAsync(JoinPoint joinPoint, Object result, Throwable error,
                             OperationLog operationLog) {
        try {
            saveLog(joinPoint, result, error, operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

    /**
     * 同步保存日志
     */
    public void saveLogSync(JoinPoint joinPoint, Object result,
                            Throwable error, OperationLog operationLog) {
        try {
            saveLog(joinPoint, result, error, operationLog);
        } catch (Exception e) {
            log.error("同步保存操作日志失败", e);
        }
    }

    /**
     * 保存日志
     */
    private void saveLog(JoinPoint joinPoint, Object result, Throwable error,
                         OperationLog operationLog) {

        OperationLogs operationLogs = new OperationLogs();

        try {
            // 保存基本信息
            operationLogs.setOperationType(operationLog.value().toString());
            operationLogs.setOperationTarget(operationLog.target());
            operationLogs.setOperationTime(LocalDateTime.now());

            // 记录操作结果
            if (error != null) {
                operationLogs.setResult(0); // 失败
                operationLogs.setErrorMessage(getErrorMessage(error));
            } else {
                operationLogs.setResult(1); // 成功
            }

            // 记录操作用户
            Long adminId = BaseContext.getCurrentId();
            if (adminId != null) {
                operationLogs.setAdminId(adminId);
            }

            // 获取目标ID,从SpEL表达式中解析
            if (!operationLog.targetId().isEmpty()) {
                Integer targetId = parseTargetId(joinPoint, operationLog.targetId());
                if (targetId != null) {
                    operationLogs.setTargetId(targetId);
                }
            }

            // 记录操作数据
            if (operationLog.saveData()) {
                String operateData = buildOperateData(joinPoint);
                if (operateData != null && operateData.length() > 5000) {
                    operateData = operateData.substring(0, 5000) + "...";
                }
                operationLogs.setOperateData(operateData);
            }

            // 保存到数据库
            operationLogService.save(operationLogs);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    /**
     * 获取错误信息
     */
    private String getErrorMessage(Throwable error) {
        if (error == null) {
            return null;
        }

        // 构建错误信息
        StringBuilder sb = new StringBuilder();
        sb.append(error.getClass().getSimpleName())
                .append(": ")
                .append(error.getMessage());

        // 限制错误信息长度
        String message = sb.toString();
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "...";
        }

        return message;
    }

    /**
     * 解析目标ID（SpEL表达式）
     */
    private Integer parseTargetId(JoinPoint joinPoint, String targetIdExpression) {
        try {
            if (targetIdExpression == null || targetIdExpression.isEmpty()) {
                return null;
            }

            // 创建SpEL上下文
            StandardEvaluationContext context = new StandardEvaluationContext();

            // 设置参数
            Object[] args = joinPoint.getArgs();
            String[] paramNames = discoverer.getParameterNames(
                    ((MethodSignature) joinPoint.getSignature()).getMethod()
            );

            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            // 设置方法参数（p0, p1, p2...）
            for (int i = 0; i < args.length; i++) {
                context.setVariable("p" + i, args[i]);
            }

            // 解析表达式
            Expression expression = parser.parseExpression(targetIdExpression);
            Object value = expression.getValue(context);

            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value != null) {
                try {
                    return Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    log.warn("目标ID无法转换为整数: {}", value);
                    return null;
                }
            }

            return null;

        } catch (Exception e) {
            log.warn("解析目标ID表达式失败: {}", targetIdExpression, e);
            return null;
        }
    }

    /**
     * 构建操作数据
     */
    private String buildOperateData(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return null;
            }

            // 构建参数Map
            Map<String, Object> params = new HashMap<>();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = discoverer.getParameterNames(signature.getMethod());

            for (int i = 0; i < args.length; i++) {
                String paramName = (paramNames != null && i < paramNames.length)
                        ? paramNames[i] : "arg" + i;

                // 敏感参数过滤
                Object paramValue = filterSensitiveParam(paramName, args[i]);
                params.put(paramName, paramValue);
            }

            // 转换为JSON（过滤敏感字段）
            return JSON.toJSONString(params);

        } catch (Exception e) {
            log.warn("构建操作数据失败", e);
            return null;
        }
    }

    /**
     * 过滤敏感参数
     */
    private Object filterSensitiveParam(String paramName, Object paramValue) {
        if (paramValue == null) {
            return null;
        }

        // 检查参数名是否包含敏感词
        String lowerParamName = paramName.toLowerCase();
        if (lowerParamName.contains("password") ||
                lowerParamName.contains("pwd") ||
                lowerParamName.contains("token") ||
                lowerParamName.contains("salt") ||
                lowerParamName.contains("secret")) {
            return "***";
        }

        return paramValue;
    }
}
