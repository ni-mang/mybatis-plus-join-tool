package org.nimang.mpjtool.obj;


import org.nimang.mpjtool.enums.LogicKey;
import org.nimang.mpjtool.enums.PriorityKey;
import org.nimang.mpjtool.enums.RuleKey;

import java.util.List;

/**
 * 查询参数
 * @author JustHuman
 */
public class MPCondition {
    private String index;
    private Class<?> source;
    private String field;
    private RuleKey rule;
    private LogicKey logic;
    private Object val;
    private PriorityKey priority;
    private MPCondition partner;
    private List<MPCondition> conditions;

    public MPCondition() {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Class<?> getSource() {
        return source;
    }

    public void setSource(Class<?> source) {
        this.source = source;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
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

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
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
