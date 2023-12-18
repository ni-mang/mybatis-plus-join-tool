package org.nimang.mpjtool.annotation;

import java.lang.annotation.*;

/**
 * 枚举值转换
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPEnums {

    /**
     * 枚举类
     * @return Class<? extends Enum>
     */
    Class<? extends Enum> enumClass() default Enum.class;

    /**
     * 值属性名
     * @return String
     */
    String val() default "code";

    /**
     * 描述属性名
     * @return String
     */
    String msg() default "msg";
}
