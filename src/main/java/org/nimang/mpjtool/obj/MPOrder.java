package org.nimang.mpjtool.obj;


import org.nimang.mpjtool.fun.MPSFunction;

/**
 * 排序参数
 * @author JustHuman
 */
public class MPOrder {
    private Integer priority;
    private String alias;
    private MPSFunction<?> mask;
    private Boolean isAsc;

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

    public Boolean getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(Boolean asc) {
        isAsc = asc;
    }
}
