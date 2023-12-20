package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.JoinKey;
import org.nimang.mpjtool.enums.RuleKey;

import java.lang.annotation.*;

/**
 * ON规则
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPOn {

    /**
     * 左表连接字段名
     * @return String
     */
    String leftField();

    /**
     * 右表类，默认主表
     * @return Class<?>
     */
    Class<?> rightClass() default Void.class;

    /**
     * 右表类别名
     * @return String
     */
    String rightAlias() default "";

    /**
     * 右表连接字段名
     * @return String
     */
    String rightField() default "";

    /**
     * 值
     * @return String[]
     */
    String[] val() default {};

    /**
     * 规则，默认为 EQ
     * @return RuleKey
     */
    RuleKey rule() default RuleKey.EQ;
}
