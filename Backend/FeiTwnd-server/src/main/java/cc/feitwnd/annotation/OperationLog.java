package cc.feitwnd.annotation;

import cc.feitwnd.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解，标识管理员的增删改操作
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 操作类型（INSERT, UPDATE, DELETE）
     */
    OperationType value();

    /**
     * 操作目标/模块（operation_target）
     */
    String target() default "";

    /**
     * 目标ID的表达式（SpEL表达式，从参数中提取）
     * 例如: "#request.id" 或 "#id"
     */
    String targetId() default "";

    /**
     * 是否记录操作数据
     */
    boolean saveData() default true;

    /**
     * 是否异步记录
     */
    boolean async() default true;
}
