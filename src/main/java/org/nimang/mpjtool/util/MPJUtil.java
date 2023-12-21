package org.nimang.mpjtool.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yulichang.wrapper.MPJAbstractLambdaWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.nimang.mpjtool.annotation.*;
import org.nimang.mpjtool.enums.LogicKey;
import org.nimang.mpjtool.enums.OrderKey;
import org.nimang.mpjtool.enums.PriorityKey;
import org.nimang.mpjtool.enums.RuleKey;
import org.nimang.mpjtool.fun.MPSFunction;
import org.nimang.mpjtool.obj.MPCondition;
import org.nimang.mpjtool.obj.MPOrder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

/**
 * MPJLambdaWrapper自动组装工具
 * @author JustHuman
 */
public class MPJUtil {
    /**
     * 是否必须添加注解，默认为false
     * <br>开启后，相关类中的字段必须添加相应注解，否则将直接忽略</br>
     */
    private static final boolean NEED_ANNOTATION = false;

    /**
     * 组装完整Wrapper，含 select、join、where
     * @param mainClass 主体类
     * @param query 搜索条件对象
     * @param result 返回参数类
     * @return MPJLambdaWrapper
     * @param <T>
     */
    public static <T> MPJLambdaWrapper<T> build(Class<T> mainClass, Object query, Class<?> result) {
        MPJLambdaWrapper<T> wrapper = new MPJLambdaWrapper<>(mainClass);
        buildSelect(wrapper, mainClass, result, true);
        buildJoin(wrapper, mainClass, result);
        buildWhere(wrapper, mainClass, query);
        return wrapper;
    }

    /**
     * 设置Wrapper，含 select、join、where
     * @param wrapper MPJLambdaWrapper<T>
     * @param mainClass 主体类
     * @param query 搜索条件对象
     * @param result 返回参数类
     * @return MPJLambdaWrapper
     * @param <T>
     */
    public static <T> MPJLambdaWrapper<T> build(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, Object query, Class<?> result) {
        buildSelect(wrapper, mainClass, result, true);
        buildJoin(wrapper, mainClass, result);
        buildWhere(wrapper, mainClass, query);
        return wrapper;
    }


    /***********************************  SELECT START  ***********************************/

    /**
     * 组装返回参数，仅 select，进行排序
     * @param wrapper MPJLambdaWrapper
     * @param mainClass 主体类
     * @param result 返回参数类
     * @return MPJLambdaWrapper
     * @param <T>
     */
    public static <T> MPJLambdaWrapper<T> buildSelectOrderBy(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, Class<?> result) {
        return buildSelect(wrapper, mainClass, result, true);
    }

    /**
     * 组装返回参数，仅 select，不排序
     * @param wrapper MPJLambdaWrapper
     * @param mainClass 主体类
     * @param result 返回参数类
     * @return MPJLambdaWrapper
     * @param <T>
     */
    public static <T> MPJLambdaWrapper<T> buildSelect(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, Class<?> result) {
        return buildSelect(wrapper, mainClass, result, false);
    }

    /**
     * 组装返回参数
     * @param wrapper MPJLambdaWrapper
     * @param mainClass 主体类
     * @param result 返回参数类
     * @param withOrder 是否进行排序
     * @return MPJLambdaWrapper
     * @param <T>
     */
    private static <T> MPJLambdaWrapper<T> buildSelect(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, Class<?> result, Boolean withOrder) {
        Field[] cFields = result.getDeclaredFields();
        List<MPOrder> resultList = new ArrayList<>();
        for (Field cField:cFields){
            // 忽略静态字段
            if(Modifier.isStatic(cField.getModifiers())){
                continue;
            }
            // 忽略添加 @MPIgnore 注解的字段
            MPIgnore mpIgnore = cField.getAnnotation(MPIgnore.class);
            if(mpIgnore != null){
                continue;
            }
            MPOrder mpOrder = null;
            MPEnums mpEnums = null;
            Class<?> source = mainClass;
            String alias = "";
            String field = cField.getName();
            MPSelect mpSelect = cField.getAnnotation(MPSelect.class);
            if(mpSelect != null){
                if(!Void.class.equals(mpSelect.targetClass())){
                    source = mpSelect.targetClass();
                }
                if(StrUtil.isNotBlank(mpSelect.field())){
                    field = mpSelect.field();
                }
                if(!OrderKey.NONE.equals(mpSelect.orderBy().order())){
                    mpOrder = new MPOrder();
                    mpOrder.setPriority(mpSelect.orderBy().priority());
                    mpOrder.setIsAsc(OrderKey.ASC.equals(mpSelect.orderBy().order()));
                    resultList.add(mpOrder);
                }
                mpEnums = mpSelect.enums();
                alias = mpSelect.alias();
            } else if (NEED_ANNOTATION){
                continue;
            }
            MPSFunction<?> sourceMask = getMask(source, field, "SELECT", result.getName());
            MPSFunction<?> resultMask = getMask(result, cField.getName(), "SELECT", result.getName());
            if(mpOrder != null){
                mpOrder.setMask(sourceMask);
            }

            if(mpEnums != null && !Enum.class.equals(mpEnums.enumClass())){
                // 枚举转换
                try {
                    String target = StrUtil.isBlank(alias) ? "" : alias + ".`" + field + "`";
                    StringBuilder caseInfo = getCaseInfo(target, mpEnums.enumClass(), mpEnums.val(), mpEnums.msg());
                    wrapper.selectFunc(caseInfo::toString, sourceMask, resultMask);
                } catch (Exception e) {
                    throw new RuntimeException(result.getName() + " -> " + "SELECT：枚举转换错误[" + cField.getName()+ "]");
                }
            }else {
                if(StrUtil.isBlank(alias)){
                    wrapper.selectAs(sourceMask, resultMask);
                }else {
                    wrapper.selectAs(alias, sourceMask, resultMask);
                }
            }
        }
        // 排序处理
        if(withOrder) {
            CollectionUtil.sort(resultList, Comparator.comparingInt(MPOrder::getPriority));
            resultList.forEach(o -> {
                wrapper.orderBy(true, o.getIsAsc(), o.getMask());
            });
        }
        return wrapper;
    }
    /***********************************  SELECT END  ***********************************/

    /***********************************  JOIN START  ***********************************/
    /**
     * 组装Join关系，仅 join
     * @param wrapper MPJLambdaWrapper<T>
     * @param mainClass 主体类
     * @param result 返回参数类
     * @return MPJLambdaWrapper<T>
     * @param <T>
     */
    public static <T> MPJLambdaWrapper<T> buildJoin(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, Class<?> result) {
        MPJoin mpJoin = result.getAnnotation(MPJoin.class);
        MPJoins mpJoins = result.getAnnotation(MPJoins.class);
        if(mpJoin != null){
            processJoin(wrapper, mainClass, mpJoin, result);
        }else if (mpJoins != null){
            if(ObjectUtil.isNotEmpty(mpJoins.joins())){
                for (MPJoin subMPJoin : mpJoins.joins()){
                    processJoin(wrapper, mainClass, subMPJoin, result);
                }
            }
        }
        return wrapper;
    }

    /**
     * join组装工序
     * @param wrapper MPJLambdaWrapper<T>
     * @param mainClass 主体类
     * @param mpJoin 返回参数类
     * @param <T>
     * @param <X>
     */
    private static <T,X> void processJoin(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, MPJoin mpJoin, Class<?> result){
        MPOn[] mpOns = mpJoin.ons();
        if(ObjectUtil.isEmpty(mpOns)){
            throw new RuntimeException(result.getName() + " -> " + "JOIN：缺少ON条件语句");
        }
        Class<X> leftClass = (Class<X>)(Void.class.equals(mpJoin.leftClass()) ? mainClass : mpJoin.leftClass());
        List<MPCondition> funConditions = new ArrayList<>();
        List<MPCondition> valConditions = new ArrayList<>();
        for (MPOn mpOn : mpOns) {
            MPSFunction<X> leftMask = getMask(leftClass, mpOn.leftField(), "JOIN", result.getName());

            MPCondition condition = new MPCondition();
            condition.setAlias(mpJoin.leftAlias());
            condition.setMask(leftMask);
            condition.setRule(mpOn.rule());

            if(RuleKey.IS_NULL.equals(mpOn.rule()) || RuleKey.IS_NOT_NULL.equals(mpOn.rule())){
                valConditions.add(condition);
            }else if(RuleKey.IN.equals(condition.getRule()) || RuleKey.NOT_IN.equals(condition.getRule())){
                condition.setVal(ConvertUtils.convert(leftClass, mpOn.leftField(), Arrays.asList(mpOn.val())));
                valConditions.add(condition);
            }else if(RuleKey.BETWEEN.equals(condition.getRule()) || RuleKey.NOT_BETWEEN.equals(condition.getRule())){
                MPCondition aftCondition = BeanUtil.copyProperties(condition, MPCondition.class);
                aftCondition.setPriority(PriorityKey.AFTER);
                aftCondition.setVal(ConvertUtils.convert(leftClass, mpOn.leftField(), mpOn.val()[1]));
                condition.setPriority(PriorityKey.BEFORE);
                condition.setVal(ConvertUtils.convert(leftClass, mpOn.leftField(), mpOn.val()[0]));
                condition.setPartner(aftCondition);
                valConditions.add(condition);
            }else if(ObjectUtil.isEmpty(mpOn.val())){
                Class<?> rightClass = Void.class.equals(mpOn.rightClass()) ? mainClass : mpOn.rightClass();
                MPSFunction<?> rightMask = getMask(rightClass, mpOn.rightField(), "JOIN", result.getName());
                condition.setRightMask(rightMask);
                condition.setRightAlias(StrUtil.isBlank(mpOn.rightAlias()) ? null : mpOn.rightAlias());
                funConditions.add(condition);
            }else {
                condition.setVal(ConvertUtils.convert(leftClass, mpOn.leftField(), mpOn.val()[0]));
                valConditions.add(condition);
            }
        }

        String keyWord;
        switch (mpJoin.join()) {
            case LEFT_JOIN : keyWord = "LEFT JOIN";break;
            case RIGHT_JOIN : keyWord = "RIGHT JOIN";break;
            case INNER_JOIN : keyWord = "INNER JOIN";break;
            default : throw new RuntimeException(result.getName() + " -> " + "JOIN：不支持的类型[" + mpJoin.join() + "]");
        }

        wrapper.join(keyWord, leftClass, mpJoin.leftAlias(), on -> {
            funConditions.forEach(condition -> {
                funCondition(on, condition);
            });
            valConditions.forEach(condition -> {
                valCondition(on, condition);
            });
            return on;
        });
    }

    /***********************************  JOIN END  ***********************************/

    /***********************************  WHERE START  ***********************************/

    /**
     * 组装搜索条件，仅 where
     * @param wrapper MPJLambdaWrapper
     * @param mainClass 主体类
     * @param query 搜索条件对象
     * @return
     * @param <T>
     */
    public static <T> MPJLambdaWrapper<T> buildWhere(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, Object query) {
        Field[] cFields = query.getClass().getDeclaredFields();
        Map<String, MPCondition> betweenConditions = new LinkedHashMap<>();
        String queryName = query.getClass().getName();
        for (Field cField:cFields){
            // 忽略静态字段
            if(Modifier.isStatic(cField.getModifiers())){
                continue;
            }
            // 如字段值为空则忽略
            Object val = ReflectUtil.getFieldValue(query, cField);
            if(ObjectUtil.isEmpty(val)){
                continue;
            }
            // 忽略添加 @MPIgnore 注解的字段
            MPIgnore mpIgnore = cField.getAnnotation(MPIgnore.class);
            if(mpIgnore != null){
                continue;
            }
            MPWheres mpWheres = cField.getAnnotation(MPWheres.class);
            MPWhere mpWhere = cField.getAnnotation(MPWhere.class);
            MPCondition condition;
            if(mpWheres != null){
                condition = new MPCondition();
                List<MPCondition> subCondition = new ArrayList<>();
                for (MPWhere subMPWhere : mpWheres.wheres()){
                    subCondition.add(makeCondition(mainClass, subMPWhere, cField, val, queryName, betweenConditions));
                }
                condition.setConditions(subCondition);
                condition.setLogic(mpWheres.logic());
                processWhereLogic(wrapper, condition);
            }else if(mpWhere != null){
                condition = makeCondition(mainClass, mpWhere, cField, val, queryName, betweenConditions);
                valCondition(wrapper, condition);
            }else {
                if(!NEED_ANNOTATION){
                    condition = makeCondition(mainClass, null, cField, val, queryName, betweenConditions);
                    valCondition(wrapper, condition);
                }
            }
        }
        return wrapper;
    }

    /**
     * 生产条件对象
     * @param mainClass 主体类
     * @param mpWhere 查询注解
     * @param cField 当前字段
     * @param val 字段值
     * @param betweenConditions Between条件
     * @return MPCondition
     * @param <T>
     */
    private static <T> MPCondition makeCondition(Class<T> mainClass, MPWhere mpWhere, Field cField, Object val, String queryName, Map<String, MPCondition> betweenConditions){

        String alias = null;
        String field = cField.getName();
        Class<?> targetClass = mainClass;
        RuleKey rule = RuleKey.EQ;
        PriorityKey priority = PriorityKey.BEFORE;

        if(mpWhere != null){
            if(StrUtil.isNotBlank(mpWhere.alias())){
                alias = mpWhere.alias();
            }
            if(!Void.class.equals(mpWhere.targetClass())){
                targetClass = mpWhere.targetClass();
            }
            if(StrUtil.isNotBlank(mpWhere.field())){
                field = mpWhere.field();
            }
            rule = mpWhere.rule();
            priority = mpWhere.priority();
        }
        MPCondition condition = new MPCondition();
        condition.setAlias(alias);
        condition.setMask(getMask(targetClass, field, "WHERE", queryName));
        condition.setRule(rule);
        condition.setVal(val);
        if(RuleKey.BETWEEN.equals(condition.getRule()) || RuleKey.NOT_BETWEEN.equals(condition.getRule())){
            condition.setPriority(priority);
            MPCondition btCondition = betweenConditions.get(field);
            Optional.ofNullable(btCondition).ifPresent(condition::setPartner);
            betweenConditions.put(field, condition);
        }
        return condition;
    }

    /**
     * 逻辑工序
     * @param wrapper MPJLambdaWrapper
     * @param logicCondition 逻辑条件对象
     * @param <T>
     */
    private static <T> void processWhereLogic(MPJLambdaWrapper<T> wrapper, MPCondition logicCondition) {
        List<MPCondition> conditions = logicCondition.getConditions();
        if(LogicKey.AND.equals(logicCondition.getLogic())){
            wrapper.and(true, tMPJLambdaWrapper -> {
                for(MPCondition condition : conditions){
                    valCondition(tMPJLambdaWrapper, condition);
                }
            });
        }else if(LogicKey.OR.equals(logicCondition.getLogic())){
            wrapper.and(true, tMPJLambdaWrapper -> {
                for(MPCondition condition : conditions){
                    tMPJLambdaWrapper.or(tMPJLambdaWrapper1 -> valCondition(tMPJLambdaWrapper1, condition));
                }
            });
        }
    }


    /***********************************  WHERE END  ***********************************/

    /***********************************  Condition START  ***********************************/
    /**
     * 条件拼接-具体值
     * @param wrapper MPJLambdaWrapper<T>
     * @param condition MPCondition
     */
    private static <T> void valCondition(MPJAbstractLambdaWrapper<T,?> wrapper, MPCondition condition){
        String alias = StrUtil.isBlank(condition.getAlias()) ? null : condition.getAlias();
        MPSFunction<?> mask = condition.getMask();
        Object val = condition.getVal();
        switch (condition.getRule()) {
            case EQ : wrapper.eq(alias, mask, val);break;
            case NE : wrapper.ne(alias, mask, val);break;
            case GT : wrapper.gt(alias, mask, val);break;
            case GE : wrapper.ge(alias, mask, val);break;
            case LT : wrapper.lt(alias, mask, val);break;
            case LE : wrapper.le(alias, mask, val);break;
            case LIKE : wrapper.like(alias, mask, val);break;
            case NOT_LIKE : wrapper.notLike(alias, mask, val);break;
            case LIKE_LEFT : wrapper.likeLeft(alias, mask, val);break;
            case LIKE_RIGHT : wrapper.likeRight(alias, mask, val);break;
            case IN :
                if(val instanceof Collection){
                    wrapper.in(alias, mask, (Collection<?>)val);
                }else {
                    wrapper.in(alias, mask, val);
                }
                break;
            case NOT_IN :
                if(val instanceof Collection){
                    wrapper.notIn(alias, mask, (Collection<?>)val);
                }else {
                    wrapper.notIn(alias, mask, val);
                }
                break;
            case IS_NULL : wrapper.isNull(mask);break;
            case IS_NOT_NULL : wrapper.isNotNull(mask);break;
            case BETWEEN:
                if(condition.getPartner() != null){
                    if(PriorityKey.BEFORE.equals(condition.getPriority())){
                        wrapper.between(alias, mask, val, condition.getPartner().getVal());
                    }else {
                        wrapper.between(alias, mask, condition.getPartner().getVal(), val);
                    }
                }
                break;
            case NOT_BETWEEN:
                if(condition.getPartner() != null) {
                    if(PriorityKey.BEFORE.equals(condition.getPriority())){
                        wrapper.notBetween(alias, mask, val, condition.getPartner().getVal());
                    }else {
                        wrapper.notBetween(alias, mask, condition.getPartner().getVal(), val);
                    }
                }
                break;
        }
    }

    /**
     * 条件拼接-表字段信息
     * @param wrapper MPJLambdaWrapper<T>
     * @param condition MPCondition
     */
    private static <T> void funCondition(MPJAbstractLambdaWrapper<T,?> wrapper, MPCondition condition){
        String alias = StrUtil.isBlank(condition.getAlias()) ? null : condition.getAlias();
        MPSFunction<?> mask = condition.getMask();
        String rightAlias = StrUtil.isBlank(condition.getRightAlias()) ? null : condition.getRightAlias();
        MPSFunction<?> rightMask = condition.getRightMask();
        switch (condition.getRule()) {
            case EQ : wrapper.eq(alias, mask, rightAlias, rightMask);break;
            case NE : wrapper.ne(alias, mask, rightAlias, rightMask);break;
            case GT : wrapper.gt(alias, mask, rightAlias, rightMask);break;
            case GE : wrapper.ge(alias, mask, rightAlias, rightMask);break;
            case LT : wrapper.lt(alias, mask, rightAlias, rightMask);break;
            case LE : wrapper.le(alias, mask, rightAlias, rightMask);break;
            case IN : wrapper.in(alias, mask, rightAlias, rightMask);break;
            case NOT_IN : wrapper.notIn(alias, mask, rightAlias, rightMask);break;
            case IS_NULL : wrapper.isNull(mask);break;
            case IS_NOT_NULL : wrapper.isNotNull(mask);break;
        }
    }

    /***********************************  Condition END  ***********************************/


    private static <T> MPSFunction<T> getMask(Class<T> clazz, String filedName, String point, String configClassName){
        checkField(clazz, filedName, point, configClassName);
        return getMask(clazz, filedName);
    }

    /**
     * 将类、字段名转换为函数式（如：Student::getName）
     * @param clazz 类
     * @param filedName 字段名
     * @return
     * @param <T>
     */
    private static <T> MPSFunction<T> getMask(Class<T> clazz, String filedName){
        return new MPSFunction<>(filedName, clazz.getName());
    }

    private static <T> Function<String, MPSFunction<T>> makeFun(Class<T> clazz){
        return fieldName -> new MPSFunction<>(fieldName, clazz.getName());
    }

    private static <T> MPSFunction<T> makeMPSFun(Function<String, MPSFunction<T>> mask, String filedName){
        return mask.apply(filedName);
    }

    private static void checkField(Class<?> checkClass, String field, String point, String configClassName){
        // 判断属性是否存在
        if(!ReflectUtil.hasField(checkClass, field)){
            throw new RuntimeException(configClassName + " -> " + point + "：类[" + checkClass.getName() + "]中不存在属性[" + field + "]，请检查相关配置");
        }
    }

    /**
     * 迭代枚举组装case语句
     * @param target
     * @param enumClass
     * @param valField
     * @param msgField
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private static StringBuilder getCaseInfo(String target, Class<?> enumClass, String valField, String msgField) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        StringBuilder caseInfo = new StringBuilder();
        if(StrUtil.isBlank(target)){
            caseInfo.append("CASE %s");
        }else {
            caseInfo.append("CASE ").append(target);
        }

        Object[] enumConstants = enumClass.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            Method evfMethod = enumConstant.getClass().getMethod(MessageFormat.format("get{0}", StrUtil.upperFirst(valField)));
            Object evf = evfMethod.invoke(enumConstant, null);
            Method enfMethod = enumConstant.getClass().getMethod(MessageFormat.format("get{0}", StrUtil.upperFirst(msgField)));
            Object enf = enfMethod.invoke(enumConstant, null);

            String valStr = evf.toString();
            if(evf instanceof String){
                valStr = "'" + valStr + "'";
            }
            caseInfo.append(" WHEN ").append(valStr).append(" THEN '").append(enf.toString()).append("'");
        }
        caseInfo.append(" ELSE '' END");
        return caseInfo;
    }


}