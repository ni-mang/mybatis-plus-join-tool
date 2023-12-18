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
     * 关联表索引
     * 关联表的别名，默认情况下，主表为“t”，关联表分别按“t1,t2,t3...”顺序命名
     * @return
     */
    String index() default "";

    /**
     * 源类，指向当前字段作用的对象类型
     * @return Class<?>
     */
    Class<?> source() default Void.class;

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
     * rule 为 BETWEEN 或 NOT_BETWEEN 时，需指定优先级，默认为 BEFORE
     * @return PriorityKey
     */
    PriorityKey priority() default PriorityKey.BEFORE;
}
