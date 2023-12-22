package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.JoinKey;
import org.nimang.mpjtool.enums.RuleKey;

import java.lang.annotation.*;

/**
 * ON规则
 * <br>依赖于@MPJoin，可指定左表字段与右表字段的连接关系，也可直接指定左表字段与具体数值的关系<br/>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPOn {

    /**
     * 左表连接字段名，必须设置
     * @return String
     */
    String leftField();

    /**
     * 右表类
     * <br>不设置则默认为主类<br/>
     * @return Class<?>
     */
    Class<?> rightClass() default Void.class;

    /**
     * 右表类别名
     * <br>如右表此前有设置别名，此项必须设置，否则可不设置<br/>
     * @return String
     */
    String rightAlias() default "";

    /**
     * 右表连接字段名
     * <br>指定左表字段与右表字段的连接关系时必须设置<br/>
     * @return String
     */
    String rightField() default "";

    /**
     * 值
     * <br>直接指定左表字段与具体数值的关系<br/>
     * <br>1. 以字符串形式传入具体值，如 "陈"，"10","3.14"，"2023-12-22 10:29:34" 等，MPJTool 将自动转型<br/>
     * <br>2. 设置有效值后，rightClass、rightAlias、rightField 将失效<br/>
     * <br>3. 当 rule 为 IS_NULL、IS_NOT_NULL 时，此项不必设置<br/>
     * <br>4. 当 rule 为 IN、NOT_IN 时，此项必须设置，以数组形式传入多个值<br/>
     * <br>5. 当 rule 为 BETWEEN、NOT_BETWEEN时，此项必须设置，至少传入两个值，且只使用到前两个值<br/>
     * <br>6. 当 rule 为 非上述规则的其它规则时，此项必须设置，只使用到第一个值<br/>
     * @return String[]
     */
    String[] val() default {};

    /**
     * 规则
     * <br>默认为 RuleKey.EQ<br/>
     * @return RuleKey
     */
    RuleKey rule() default RuleKey.EQ;
}
