package spring.springBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // 字段级别的注释
@Retention(RetentionPolicy.RUNTIME) // 在运行时保留注释信息
public @interface Resource {
}
