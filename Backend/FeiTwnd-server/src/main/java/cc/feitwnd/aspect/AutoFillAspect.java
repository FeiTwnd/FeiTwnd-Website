package cc.feitwnd.aspect;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.constant.AutoFillConstant;
import cc.feitwnd.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，用于自动填充功能字段
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* cc.feitwnd.mapper.*.*(..)) && @annotation(cc.feitwnd.annotation.AutoFill)")
    public void autoFillPointcut() {
    }

    /**
     * 前置通知
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("AOP填充公共字段");
        // 获取当前方法的操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();// 方法签名对象
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);// 获取方法上的注解对象
        OperationType operationType = autoFill.value();// 获取数据库操作类型

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();

        // 根据对应的操作类型，利用反射为对应的字段赋值
        if(operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.invoke(entity, now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
