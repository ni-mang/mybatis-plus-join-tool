package org.nimang.mpjtool.obj;


import lombok.Data;
import org.nimang.mpjtool.enums.LogicKey;
import org.nimang.mpjtool.enums.PriorityKey;
import org.nimang.mpjtool.enums.RuleKey;
import org.nimang.mpjtool.fun.MPSFunction;

import java.util.List;

/**
 * 查询参数
 * @author JustHuman
 */
@Data
public class MPCondition {
    private String alias;
    private MPSFunction<?> mask;
    private Object val;
    private String rightAlias;
    private MPSFunction<?> rightMask;
    private RuleKey rule;
    private LogicKey logic;
    private PriorityKey priority;
    private MPCondition partner;
    private List<MPCondition> conditions;
}
