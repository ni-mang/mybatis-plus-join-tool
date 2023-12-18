package org.nimang.mpjtool.annotation;

import java.lang.annotation.*;

/**
 * 联表规则组
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPJoins {
    /**
     * join规则组
     * @return MPJoin[]
     */
    MPJoin[] joins() default {};
}
