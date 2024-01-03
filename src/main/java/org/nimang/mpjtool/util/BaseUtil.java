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
        Class<?> clazz = tempClass;
        while (clazz != null && Object.class.isAssignableFrom(clazz)) {
            fieldList.addAll(Arrays.asList(clazz .getDeclaredFields()));
            clazz = clazz.getSuperclass(); //得到父类,然后赋给自己
        }
        return fieldList;
    }

    /**
     * 根据类和字段名获取字段对象
     * @param clazz 待搜索的类对象
     * @param fieldName 字段名
     * @return 返回字段对象，如果找不到则返回null
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        Class<?> targetClass = clazz;
        while (field == null && targetClass != null) {
            try {
                field = targetClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                targetClass = targetClass.getSuperclass();
            }
        }
        return field;
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
        Field field = getField(tClass, fieldName);
        Class<?> fieldType = field.getType();
        return val == null ? null : Convert.convert(fieldType, val);
    }
}
