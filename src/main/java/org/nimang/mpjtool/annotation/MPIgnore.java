package org.nimang.mpjtool.annotation;

import java.lang.annotation.*;

/**
 * 忽略字段
 * @author JustHuman
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MPIgnore {


}
