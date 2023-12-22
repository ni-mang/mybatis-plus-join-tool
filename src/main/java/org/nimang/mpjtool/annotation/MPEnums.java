package org.nimang.mpjtool.annotation;

import java.lang.annotation.*;

/**
 * 枚举值转换
 * <br>依赖于@MPSelect，设置当前字段与指定枚举类的数据转换<br/>
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
     * <br>枚举类中表示值的字段名，默认“code”<br/>
     * @return String
     */
    String val() default "code";

    /**
     * 描述属性名
     * <br>枚举类中表示注释描述的字段名，默认“msg”<br/>
     * @return String
     */
    String msg() default "msg";
}
