package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.JoinKey;

import java.lang.annotation.*;

/**
 * 连接规则
 * <br>在 Result 结果返回类中标注连接规则<br/>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPJoin {
    /**
     * 左表类
     * <br>进行 join 操作的类，不设置则默认为主类<br/>
     * @return Class<?>
     */
    Class<?> leftClass() default Void.class;

    /**
     * 左表类别名
     * <br>不设置则分别按“t1,t2,t3...”顺序命名“<br/>
     * @return String
     */
    String leftAlias() default "";

    /**
     * 连接规则
     * <br>不设置则默认为 JoinKey.LEFT_JOIN<br/>
     * @return JoinKey
     */
    JoinKey join() default JoinKey.LEFT_JOIN;

    /**
     * on规则组
     * <br>必须设置，指定连接时的 ON 条件<br/>
     * @return MPOn[]
     */
    MPOn[] ons();

}
