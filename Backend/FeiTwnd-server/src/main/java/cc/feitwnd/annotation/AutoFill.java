package cc.feitwnd.annotation;

import cc.feitwnd.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 自定义注解，用于标识方法需要进行功能字段的自动填充
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 数据库操作类型: INSERT, UPDATE
    OperationType value();
}
