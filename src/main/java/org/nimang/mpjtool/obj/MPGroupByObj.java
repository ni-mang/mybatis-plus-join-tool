package org.nimang.mpjtool.obj;


import org.nimang.mpjtool.fun.MPSFunction;

/**
 * 分组数据对象
 * @author JustHuman
 */
public class MPGroupByObj {
    private Integer priority;
    private String alias;
    private MPSFunction<?> mask;

    public MPGroupByObj() {
    }

    public MPGroupByObj(Integer priority, String alias, MPSFunction<?> mask) {
        this.priority = priority;
        this.alias = alias;
        this.mask = mask;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public MPSFunction<?> getMask() {
        return mask;
    }

    public void setMask(MPSFunction<?> mask) {
        this.mask = mask;
    }
}
