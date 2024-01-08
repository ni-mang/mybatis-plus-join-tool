package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.OrderKey;

import java.lang.annotation.*;

/**
 * 分组
 * <br>依赖于@MPSelect，设置以当前字段分组规则</br>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPGroupBy {

    /**
     * 分组条件优先级
     * <br>默认为0，数值越低，优先级越高</br>
     * @return int
     */
    int priority() default 0;
}
