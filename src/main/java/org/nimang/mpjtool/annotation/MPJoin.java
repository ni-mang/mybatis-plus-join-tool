package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.JoinKey;

import java.lang.annotation.*;

/**
 * 连接规则
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPJoin {
    /**
     * 左表类，默认主表
     * @return Class<?>
     */
    Class<?> leftClass() default Void.class;

    /**
     * 左表类别名
     * @return String
     */
    String leftAlias() default "";

    /**
     * 规则，默认为 LEFT_JOIN
     * @return JoinKey
     */
    JoinKey join() default JoinKey.LEFT_JOIN;

    /**
     * on规则组
     * @return MPOn[]
     */
    MPOn[] ons();

}
