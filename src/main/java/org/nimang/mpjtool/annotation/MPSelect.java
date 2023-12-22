package org.nimang.mpjtool.annotation;

import java.lang.annotation.*;

/**
 * 查询结果
 * <br>标注 Result 结果返回类中用于 select 的字段，如该字段在主类且字段名相同，可省略</br>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPSelect {

    /**
     * 表别名
     * <br>不设置则按默认别名，主表为“t”，连接表分别按“t1,t2,t3...”顺序命名</br>
     * @return String
     */
    String alias() default "";

    /**
     * 目标类
     * <br>返回字段所在的类，不设置则默认为主类</br>
     * @return Class<?>
     */
    Class<?> targetClass() default Void.class;

    /**
     * 字段名
     * <br>返回字段名，不设置则同当前字段名</br>
     * @return String
     */
    String field() default "";

    /**
     * 排序注解
     * <br>设置以当前字段排序与排序规则</br>
     * @return MPOrderBy
     */
    MPOrderBy orderBy() default @MPOrderBy;

    /**
     * 枚举注解
     * <br>设置当前字段与指定枚举类的数据转换</br>
     * @return MPEnums
     */
    MPEnums enums() default @MPEnums;
}
