package org.nimang.mpjtool.obj;


import org.nimang.mpjtool.fun.MPSFunction;

/**
 * 排序参数
 * @author JustHuman
 */
public class MPOrder {
    private Integer priority;
    private MPSFunction<?> mask;
    private Boolean isAsc;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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
