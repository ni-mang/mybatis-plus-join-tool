package org.nimang.mpjtool.annotation;

import java.lang.annotation.*;

/**
 * 查询返回
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPSelect {

    /**
     * 关联表索引
     * 关联表的别名，默认情况下，主表为“t”，关联表分别按“t1,t2,t3...”顺序命名
     * @return
     */
    String index() default "";

    /**
     * 源类，默认主类
     * @return Class<?>
     */
    Class<?> source() default Void.class;

    /**
     * 字段名，默认同当前字段
     * @return String
     */
    String field() default "";

    /**
     * 排序注解
     * @return MPOrderBy
     */
    MPOrderBy orderBy() default @MPOrderBy;

    /**
     * 枚举注解
     * @return MPEnums
     */
    MPEnums enums() default @MPEnums;
}
