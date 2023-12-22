package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.PriorityKey;
import org.nimang.mpjtool.enums.RuleKey;

import java.lang.annotation.*;

/**
 * 搜索规则
 * <br>标注 Query 查询参数类中用于搜索条件的字段，如该字段在主类且字段名相同，可省略<br/>
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
     * 目标类
     * <br>搜索字段所在的类，不设置则为主类</br>
     * @return Class<?>
     */
    Class<?> targetClass() default Void.class;

    /**
     * 字段名
     * <br>搜索字段名，不设置则同当前字段名<br/>
     * @return String
     */
    String field() default "";

    /**
     * 搜索规则
     * <br>默认为 RuleKey.EQ<br/>
     * @return RuleKey
     */
    RuleKey rule() default RuleKey.EQ;

    /**
     * 优先级
     * <br>当 rule 为 BETWEEN 或 NOT_BETWEEN 时，需指定优先级，默认为 PriorityKey.BEFORE</br>
     * @return PriorityKey
     */
    PriorityKey priority() default PriorityKey.BEFORE;
}
