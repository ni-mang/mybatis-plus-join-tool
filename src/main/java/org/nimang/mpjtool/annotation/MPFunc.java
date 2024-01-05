package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.FuncKey;
import org.nimang.mpjtool.enums.OrderKey;

import java.lang.annotation.*;

/**
 * 函数
 * <br>依赖于@MPSelect，指定函数操作当前字段</br>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPFunc {

    /**
     * 函数
     * <br>支持函数：LEN, SUM, COUNT, MAX, MIN, AVG</br>
     * @return FuncKey
     */
    FuncKey func();
}
