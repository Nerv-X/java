package com.java.lang;

import org.junit.Test;

import java.lang.Integer;
import java.lang.System;
import java.lang.Void;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

/**
 * Void封装了关键字void的类型，占位符类
 * @author Nerv
 *
 */
public class Void_ {

	/**
	 * 用途一：重写返回值为泛型的方法时，实际并不需要返回值
	 * 
	 * 案例：sun.misc.Cleaner.clean()
	 */
	@Test
	public void test1 () {
		// 声明返回Void，方法内返回null
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    System.exit(1);
                    return null;
        }});
	}
	
	/**
	 * 用途二：反射获取方法的返回值类型  Void.TYPE == void.class
	 */
	@Test
	public void test2() {
        for(Method method : this.getClass().getMethods()) {
            if(method.getReturnType().equals(Void.TYPE)) {
                System.out.println(method.getDeclaringClass() + "：" + method.getName());
            }
        }
	}
	
	/**
	 * 用途三：无value的Map， Map<T, Void>相当于Set<T>
	 */
	@Test
	public void test3() {
		Map<Integer, Void> map = new HashMap<>();
		map.put(3, null);
	}
}
