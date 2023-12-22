package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.LogicKey;

import java.lang.annotation.*;

/**
 * 搜索规则组
 * <br>当需要使用同一个参数对不同字段进行搜索时（如使用 loginNmae 搜索匹配 userName 或 mobileNo），用于配置多个条件<br/>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPWheres {

    /**
     * 查询规则组
     * <br>配置的@MPWhere组合<br/>
     * @return MPWhere[]
     */
    MPWhere[] wheres() default {};

    /**
     * 逻辑规则
     * <br>多个条件之间的逻辑关系，默认为LogicKey.OR<br/>
     * @return LogicKey
     */
    LogicKey logic() default LogicKey.OR;
}
