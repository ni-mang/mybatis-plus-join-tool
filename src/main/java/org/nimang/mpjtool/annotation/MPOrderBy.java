package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.OrderKey;

import java.lang.annotation.*;

/**
 * 排序
 * <br>依赖于@MPSelect，设置以当前字段排序与排序规则</br>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPOrderBy {

    /**
     * 排列规则
     * <br>正序或倒序，默认不排序</br>
     * @return OrderKey
     */
    OrderKey order() default OrderKey.ASC;

    /**
     * 排列条件优先级
     * <br>默认为0，数值越低，优先级越高</br>
     * @return int
     */
    int priority() default 0;
}
