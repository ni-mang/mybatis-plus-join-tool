package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.JoinKey;

import java.lang.annotation.*;

/**
 * 联表规则
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPJoin {
    /**
     * join类，默认主表
     * @return Class<?>
     */
    Class<?> joinSource() default Void.class;

    /**
     * join类别名
     * @return String
     */
    String alias() default "";

    /**
     * join字段名
     * @return String
     */
    String joinField();

    /**
     * right类，默认主表
     * @return Class<?>
     */
    Class<?> rightSource() default Void.class;

    /**
     * right类别名
     * @return String
     */
    String rightAlias() default "";

    /**
     * right字段名
     * @return String
     */
    String rightField();

    /**
     * 规则，默认为 LEFT_JOIN
     * @return JoinKey
     */
    JoinKey rule() default JoinKey.LEFT_JOIN;
}
