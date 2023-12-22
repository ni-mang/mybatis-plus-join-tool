package org.nimang.mpjtool.annotation;

import java.lang.annotation.*;

/**
 * 连接规则组
 * <br>当需连接多张表时，可用于包裹多个 @MPJoin 配置<br/>
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
