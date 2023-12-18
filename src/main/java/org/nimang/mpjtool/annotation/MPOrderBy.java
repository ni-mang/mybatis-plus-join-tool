package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.OrderKey;

import java.lang.annotation.*;

/**
 * 排序
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPOrderBy {

    /**
     * 排列规则
     * @return OrderKey
     */
    OrderKey order() default OrderKey.NONE;

    /**
     * 排列条件优先级(默认为0，数值越低，优先级越高)
     * @return int
     */
    int priority() default 0;
}
