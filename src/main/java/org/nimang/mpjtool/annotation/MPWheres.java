package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.LogicKey;

import java.lang.annotation.*;

/**
 * 查询规则组
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPWheres {

    /**
     * 查询规则组
     * @return MPQuery[]
     */
    MPWhere[] wheres() default {};

    /**
     * 逻辑规则，默认为OR
     * @return LogicKey
     */
    LogicKey logic() default LogicKey.OR;
}
