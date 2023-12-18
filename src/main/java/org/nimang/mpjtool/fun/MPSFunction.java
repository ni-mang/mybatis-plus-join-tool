package org.nimang.mpjtool.fun;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.lang.invoke.SerializedLambda;

/**
 * @author fangzhigang
 */
public class MPSFunction<T> implements SFunction<T, Object> {
    private String fieldName;
    private String instantiatedMethodType;

    public MPSFunction(String fieldName, String instantiatedMethodType) {
        this.fieldName = fieldName;
        this.instantiatedMethodType = instantiatedMethodType;
    }

    @Override
    public Object apply(T t) {
        return null;
    }

    @SuppressWarnings("unused")
    private SerializedLambda writeReplace() {
        return new SerializedLambda(
                this.getClass(),
                "",
                "",
                "",
                0,
                "",
                "get" + fieldName,
                "",
                "LY" + instantiatedMethodType + ";",
                new Object[0]
        );
    }
}
