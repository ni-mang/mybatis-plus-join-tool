package org.nimang.mpjtool.obj;


import org.nimang.mpjtool.annotation.MPEnums;
import org.nimang.mpjtool.annotation.MPFunc;
import org.nimang.mpjtool.annotation.MPOrderBy;
import org.nimang.mpjtool.fun.MPSFunction;

/**
 * @author JustHuman
 */
public class MPSelectObj {
    private Class<?> source;
    private String alias = "";
    private String field;
    private MPSFunction<?> sourceMask;
    private MPSFunction<?> resultMask;
    private MPFunc mpFunc;
    private MPEnums mpEnums;

    public MPSelectObj() {
    }

    public MPSelectObj(Class<?> source, String field) {
        this.source = source;
        this.field = field;
    }

    public Class<?> getSource() {
        return source;
    }

    public void setSource(Class<?> source) {
        this.source = source;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public MPSFunction<?> getSourceMask() {
        return sourceMask;
    }

    public void setSourceMask(MPSFunction<?> sourceMask) {
        this.sourceMask = sourceMask;
    }

    public MPSFunction<?> getResultMask() {
        return resultMask;
    }

    public void setResultMask(MPSFunction<?> resultMask) {
        this.resultMask = resultMask;
    }

    public MPFunc getMpFunc() {
        return mpFunc;
    }

    public void setMpFunc(MPFunc mpFunc) {
        this.mpFunc = mpFunc;
    }

    public MPEnums getMpEnums() {
        return mpEnums;
    }

    public void setMpEnums(MPEnums mpEnums) {
        this.mpEnums = mpEnums;
    }
}
