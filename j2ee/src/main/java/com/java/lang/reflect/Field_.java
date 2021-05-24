package com.java.lang.reflect;

import java.lang.reflect.Field;

/**
 * 反射-属性
 * 	实例方法
 * 			getFields()								当前类及父类的public属性
 * 			getDeclaredMethods()					当前类声明的全部属性
 * @author nerv
 *
 */
public class Field_ {

	/**
	 * 获取类的属性，包括本类及祖类全部属性
	 * @param c
	 * @param name
	 * @return
	 * @throws NoSuchFieldException 
	 */
	public <T> Field getField(Class<T> c, String name) throws NoSuchFieldException {
		if (c == null) {
			throw new NoSuchFieldException(name);
		}
		Field f;
		try {
			f = c.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			// 递归向上查找
			return getField(c.getSuperclass(), name);
		}
		return f;
	}
}
