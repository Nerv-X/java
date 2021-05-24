package com.java.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import com.java.lang.reflect.*;

public class 反射_获取泛型类型 {
	
	private List<Integer> list1;
	private List<Set<Integer>> listSet;
	public Set<String> fun1(Map<Integer, String> map){
		return null;
	}

	/**
	 * 获取属性的泛型类型
	 */
	public static Type[] getFieldGenericType(Field field) {
		Type type = field.getGenericType(); //取得field的type
		if (type instanceof ParameterizedType) {	// 若为泛型类
			ParameterizedType paramType = (ParameterizedType) type; // 强转成具体的实现类
			return paramType.getActualTypeArguments(); // 取得包含的泛型类型
		}
		return null;
	}
	
	public static void main(String[] args) {
		try {
			getFieldGenericType(反射_获取泛型类型.class.getDeclaredField("list1"));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
}