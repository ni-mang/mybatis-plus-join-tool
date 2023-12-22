package org.nimang.mpjtool.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;

import java.lang.reflect.Field;
import java.util.*;

public class BaseUtil {

    /**
     * 获取类的所有属性，包括父类
     * @param tempClass
     * @return
     */
    public static List<Field> getAllDeclaredFields(Class<?> tempClass){
        List<Field> fieldList = new ArrayList<>();
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        return fieldList;
    }

    /**
     * 将字符串合集转换为指定属性的类型
     * @param tClass 类
     * @param fieldName 属性名
     * @param vals 字符串合集
     * @return List<Object>
     */
    public static List<Object> convert(Class<?> tClass, String fieldName, Collection<String> vals){
        List<Object> valList = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(vals)){
            vals.forEach(v -> valList.add(convert(tClass, fieldName, v)));
        }
        return valList;
    }

    /**
     * 将字符串转换为指定属性的类型
     * @param tClass 类
     * @param fieldName 属性名
     * @param val 字符串值
     * @return Object
     */
    public static Object convert(Class<?> tClass, String fieldName, String val){
        Object result;
        try {
            Field field = tClass.getDeclaredField(fieldName);
            Class<?> fieldType = field.getType();
            if(val == null){
                return null;
            }
            result = Convert.convert(fieldType, val);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
