package org.nimang.mpjtool;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
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
            MPIgnore MPIgnore = cField.getAnnotation(MPIgnore.class);
            if(MPIgnore != null){
                continue;
            }
            MPOrder order = null;
            MPEnums enums = null;
            Class<?> source = mainClass;
            String index = "";
            String field = cField.getName();
            MPSelect MPSelect = cField.getAnnotation(MPSelect.class);
            if(MPSelect != null){
                if(!Void.class.equals(MPSelect.source())){
                    source = MPSelect.source();
                }
                if(StrUtil.isNotBlank(MPSelect.field())){
                    field = MPSelect.field();
                }
                if(!OrderKey.NONE.equals(MPSelect.orderBy().order())){
                    order = new MPOrder();
                    order.setPriority(MPSelect.orderBy().priority());
                    order.setIsAsc(OrderKey.ASC.equals(MPSelect.orderBy().order()));
                    resultList.add(order);
                }
                enums = MPSelect.enums();
                index = MPSelect.index();
            } else if (NEED_ANNOTATION){
                continue;
            }
            checkField(source, field, "SELECT", result.getName());
            MPSFunction<?> sourceMask = getMask(source, field);
            MPSFunction<?> resultMask = getMask(result, cField.getName());
            if(order != null){
                order.setMask(sourceMask);
            }

            if(enums != null && !Enum.class.equals(enums.enumClass())){
                // 枚举转换
                try {
                    String target = StrUtil.isBlank(index) ? "" : index + ".`" + field + "`";
                    StringBuilder caseInfo = getCaseInfo(target, enums.enumClass(), enums.val(), enums.msg());
                    wrapper.selectFunc(caseInfo::toString, sourceMask, resultMask);
                } catch (Exception e) {
                    throw new RuntimeException(result.getName() + " -> " + "SELECT：枚举转换错误[" + cField.getName()+ "]");
                }
            }else {
                if(StrUtil.isBlank(index)){
                    wrapper.selectAs(sourceMask, resultMask);
                }else {
                    wrapper.selectAs(index, sourceMask, resultMask);
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
        MPJoin MPJoin = result.getAnnotation(MPJoin.class);
        MPJoins MPJoins = result.getAnnotation(MPJoins.class);
        if(MPJoin != null){
            processJoin(wrapper, mainClass, MPJoin, result);
        }else if (MPJoins != null){
            if(ObjectUtil.isNotEmpty(MPJoins.joins())){
                for (MPJoin subMPJoin : MPJoins.joins()){
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
     * @param MPJoin 返回参数类
     * @param <T>
     * @param <X>
     */
    private static <T,X> void processJoin(MPJLambdaWrapper<T> wrapper, Class<T> mainClass, MPJoin MPJoin, Class<?> result){
        Class<?> joinSource = mainClass;
        Class<?> linkSource = mainClass;

        if(!Void.class.equals(MPJoin.joinSource())){
            joinSource = MPJoin.joinSource();
        }
        if(!Void.class.equals(MPJoin.rightSource())){
            linkSource = MPJoin.rightSource();
        }
        String rightAlias = StrUtil.isBlank(MPJoin.rightAlias()) ? null : MPJoin.rightAlias();
        Class<X> xJoinSource = (Class<X>) joinSource;
        checkField(xJoinSource, MPJoin.joinField(), "JOIN", result.getName());
        MPSFunction<X> joinSourceMask = getMask(xJoinSource, MPJoin.joinField());
        checkField(linkSource, MPJoin.rightField(), "JOIN", result.getName());
        MPSFunction<?> linkSourceMask = getMask(linkSource, MPJoin.rightField());

        switch (MPJoin.rule()) {
            case LEFT_JOIN : wrapper.leftJoin(xJoinSource, MPJoin.alias(), joinSourceMask, rightAlias, linkSourceMask);break;
            case RIGHT_JOIN : wrapper.rightJoin(xJoinSource, MPJoin.alias(), joinSourceMask, rightAlias, linkSourceMask);break;
            case INNER_JOIN : wrapper.innerJoin(xJoinSource, MPJoin.alias(), joinSourceMask, linkSourceMask);break;
            default : throw new RuntimeException(result.getName() + " -> " + "JOIN：不支持的类型[" + MPJoin.rule() + "]");
        }
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
            MPIgnore MPIgnore = cField.getAnnotation(MPIgnore.class);
            if(MPIgnore != null){
                continue;
            }
            MPWheres MPWheres = cField.getAnnotation(MPWheres.class);
            MPWhere MPWhere = cField.getAnnotation(MPWhere.class);
            MPCondition condition;
            if(MPWheres != null){
                condition = new MPCondition();
                List<MPCondition> subCondition = new ArrayList<>();
                MPWhere[] mpWheres = MPWheres.wheres();
                for (MPWhere subMPWhere : mpWheres){
                    subCondition.add(makeCondition(mainClass, subMPWhere, cField, val, betweenConditions));
                }
                condition.setConditions(subCondition);
                condition.setLogic(MPWheres.logic());
                processWhereLogic(wrapper, condition, queryName);
            }else if(MPWhere != null){
                condition = makeCondition(mainClass, MPWhere, cField, val, betweenConditions);
                processWhere(wrapper, condition, queryName);
            }else {
                if(!NEED_ANNOTATION){
                    condition = makeCondition(mainClass, null, cField, val, betweenConditions);
                    processWhere(wrapper, condition, queryName);
                }
            }
        }
        return wrapper;
    }

    /**
     * 生产条件对象
     * @param mainClass 主体类
     * @param MPWhere 查询注解
     * @param cField 当前字段
     * @param val 字段值
     * @param betweenConditions Between条件
     * @return MPCondition
     * @param <T>
     */
    private static <T> MPCondition makeCondition(Class<T> mainClass, MPWhere MPWhere, Field cField, Object val, Map<String, MPCondition> betweenConditions){

        String index = null;
        String field = cField.getName();
        Class<?> source = mainClass;
        RuleKey rule = RuleKey.EQ;
        PriorityKey priority = PriorityKey.BEFORE;

        if(MPWhere != null){
            if(StrUtil.isNotBlank(MPWhere.index())){
                index = MPWhere.index();
            }
            if(!Void.class.equals(MPWhere.source())){
                source = MPWhere.source();
            }
            if(StrUtil.isNotBlank(MPWhere.field())){
                field = MPWhere.field();
            }
            rule = MPWhere.rule();
            priority = MPWhere.priority();
        }
        MPCondition condition = new MPCondition();
        condition.setIndex(index);
        condition.setSource(source);
        condition.setField(field);
        condition.setRule(rule);
        condition.setVal(val);
        if(RuleKey.BETWEEN.equals(condition.getRule()) || RuleKey.NOT_BETWEEN.equals(condition.getRule())){
            condition.setPriority(priority);
            MPCondition btCondition = betweenConditions.get(field);
            if(btCondition != null){
                condition.setPartner(btCondition);
            }
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
    private static <T> void processWhereLogic(MPJLambdaWrapper<T> wrapper, MPCondition logicCondition, String queryName) {
        List<MPCondition> conditions = logicCondition.getConditions();
        if(LogicKey.AND.equals(logicCondition.getLogic())){
            wrapper.and(true, tMPJLambdaWrapper -> {
                for(MPCondition condition : conditions){
                    processWhere(tMPJLambdaWrapper, condition, queryName);
                }
            });
        }else if(LogicKey.OR.equals(logicCondition.getLogic())){
            wrapper.and(true, tMPJLambdaWrapper -> {
                for(MPCondition condition : conditions){
                    tMPJLambdaWrapper.or(tMPJLambdaWrapper1 -> processWhere(tMPJLambdaWrapper1, condition, queryName));
                }
            });
        }
    }

    /**
     * 普通工序
     * @param wrapper MPJLambdaWrapper
     * @param condition 普通条件对象
     * @param <T>
     */
    private static <T> void processWhere(MPJLambdaWrapper<T> wrapper, MPCondition condition, String queryName) {
        checkField(condition.getSource(), condition.getField(), "WHERE", queryName);
        String index = condition.getIndex();
        MPSFunction<?> mask = getMask(condition.getSource(), condition.getField());
        Object val = condition.getVal();
        switch (condition.getRule()) {
            case EQ : wrapper.eq(index, mask, val);break;
            case NE : wrapper.ne(index, mask, val);break;
            case GT : wrapper.gt(index, mask, val);break;
            case GE : wrapper.ge(index, mask, val);break;
            case LT : wrapper.lt(index, mask, val);break;
            case LE : wrapper.le(index, mask, val);break;
            case LIKE : wrapper.like(index, mask, val);break;
            case NOT_LIKE : wrapper.notLike(index, mask, val);break;
            case LIKE_LEFT : wrapper.likeLeft(index, mask, val);break;
            case LIKE_RIGHT : wrapper.likeRight(index, mask, val);break;
            case IN :
                if(val instanceof Collection){
                    wrapper.in(index, mask, (Collection<?>)val);
                }else {
                    wrapper.in(index, mask, val);
                }
                break;
            case NOT_IN :
                if(val instanceof Collection){
                    wrapper.notIn(index, mask, (Collection<?>)val);
                }else {
                    wrapper.notIn(index, mask, val);
                }
                break;
            case IS_NULL : wrapper.isNull(mask);break;
            case IS_NOT_NULL : wrapper.isNotNull(mask);break;
            case BETWEEN:
                if(condition.getPartner() != null){
                    if(PriorityKey.BEFORE.equals(condition.getPriority())){
                        wrapper.between(index, mask, val, condition.getPartner().getVal());
                    }else {
                        wrapper.between(index, mask, condition.getPartner().getVal(), val);
                    }
                }
                break;
            case NOT_BETWEEN:
                if(condition.getPartner() != null) {
                    if(PriorityKey.BEFORE.equals(condition.getPriority())){
                        wrapper.notBetween(index, mask, val, condition.getPartner().getVal());
                    }else {
                        wrapper.notBetween(index, mask, condition.getPartner().getVal(), val);
                    }
                }
                break;
            default : throw new RuntimeException(queryName + " -> " + "WHERE：不支持的类型[" + condition.getRule() + "]");
        }
    }

    /***********************************  WHERE END  ***********************************/


    /**
     * 将类、字段名转换为函数式（如：Student::getName）
     * @param clazz 类
     * @param filedName 字段名
     * @return
     * @param <T>
     */
    private static <T> MPSFunction<T> getMask(Class<T> clazz, String filedName){
        return makeMPSFun(makeFun(clazz), filedName);
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
