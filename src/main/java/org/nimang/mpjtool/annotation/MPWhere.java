package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.PriorityKey;
import org.nimang.mpjtool.enums.RuleKey;

import java.lang.annotation.*;

/**
 * 查询规则
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPWhere {

    /**
     * 表别名
     * <br>不指定则按默认别名，主表为“t”，连接表分别按“t1,t2,t3...”顺序命名</br>
     * @return String
     */
    String alias() default "";

    /**
     * 目标类，默认主类
     * @return Class<?>
     */
    Class<?> targetClass() default Void.class;

    /**
     * 字段名，不填默认为当前字段名
     * @return String
     */
    String field() default "";

    /**
     * 规则，默认为EQ
     * @return RuleKey
     */
    RuleKey rule() default RuleKey.EQ;

    /**
     * 优先级
     * <br>rule 为 BETWEEN 或 NOT_BETWEEN 时，需指定优先级，默认为 BEFORE</br>
     * @return PriorityKey
     */
    PriorityKey priority() default PriorityKey.BEFORE;
}