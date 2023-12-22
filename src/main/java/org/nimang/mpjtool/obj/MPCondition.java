package org.nimang.mpjtool.obj;


import org.nimang.mpjtool.enums.LogicKey;
import org.nimang.mpjtool.enums.PriorityKey;
import org.nimang.mpjtool.enums.RuleKey;
import org.nimang.mpjtool.fun.MPSFunction;

import java.util.List;

/**
 * 查询参数
 * @author JustHuman
 */
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

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public String getRightAlias() {
        return rightAlias;
    }

    public void setRightAlias(String rightAlias) {
        this.rightAlias = rightAlias;
    }

    public MPSFunction<?> getRightMask() {
        return rightMask;
    }

    public void setRightMask(MPSFunction<?> rightMask) {
        this.rightMask = rightMask;
    }

    public RuleKey getRule() {
        return rule;
    }

    public void setRule(RuleKey rule) {
        this.rule = rule;
    }

    public LogicKey getLogic() {
        return logic;
    }

    public void setLogic(LogicKey logic) {
        this.logic = logic;
    }

    public PriorityKey getPriority() {
        return priority;
    }

    public void setPriority(PriorityKey priority) {
        this.priority = priority;
    }

    public MPCondition getPartner() {
        return partner;
    }

    public void setPartner(MPCondition partner) {
        this.partner = partner;
    }

    public List<MPCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<MPCondition> conditions) {
        this.conditions = conditions;
    }
}
