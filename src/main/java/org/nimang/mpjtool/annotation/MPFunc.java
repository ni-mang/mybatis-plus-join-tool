package org.nimang.mpjtool.annotation;


import org.nimang.mpjtool.enums.FuncKey;
import org.nimang.mpjtool.enums.OrderKey;

import java.lang.annotation.*;

/**
 * 函数
 * <br>依赖于@MPSelect，使用常用函数对当前字段进行处理</br>
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPFunc {

    /**
     * 内置函数
     * <br>LEN, SUM, COUNT, MAX, MIN, AVG</br>
     * @return FuncKey
     */
    FuncKey func();
}
