package org.nimang.mpjtool.obj;


import lombok.Data;
import org.nimang.mpjtool.fun.MPSFunction;

/**
 * 排序参数
 * @author JustHuman
 */
@Data
public class MPOrder {
    private Integer priority;
    private MPSFunction<?> mask;
    private Boolean isAsc;
}
