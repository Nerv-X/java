package com.java.lang;

import java.lang.Class;
import java.lang.ClassNotFoundException;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.System;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import org.junit.Test;

import sun.reflect.annotation.AnnotationParser;

/**
 * 反射
 * 		运行时动态获取信息及调用方法的机制。主要用于框架技术
 * 		三个动态性质
 * 			运行时生产对象实例
 * 			运行时调用方法
 * 			运行时更改属性
 * 		Java程序运行时有编译时类型（声明对象的类型）、运行时类型（运行时实际对象的类型）
 * 
 * 	结构
 * 		Class		核心类，可获取属性、方法
 * 			getMethods()							当前类及父类的public方法
 * 			getMethod(name, parameterTypes)			根据名称和参数类型获取当前及父类的public方法
 * 			getDeclaredMethods()					当前类全部方法
 * 			getDeclaredMethod(name, parameterTypes)	根据名称和参数类型获取当前类的全部方法
 * 			getFields/Constructors()				属性/构造	同上
 * 			getSuperclass()							父类class
 * 			getInterfaces()							实现的接口class[]
 * 			getGenericSuperclass()					父类type（可获得泛型参数类型）
 * 			getGenericInterfaces()					实现的接口type[]
 * 			getResource(path)						加载资源
 * 		Field		属性类
 * 			getType()								属性类型class
 * 			getGenericType()						属性类型type
 * 		Method		方法类
 * 			getParameterTypes()						参数类型class[]
 * 			getReturnType()							返回值类型class
 * 			getExceptionTypes()						声明异常类型class[]
 * 			getGenericParameterTypes()				参数类型type[]
 * 			getGenericReturnType()					返回值类型type
 * 			getGenericExceptionTypes()				声明异常类型type[]
 * 		Constructor	构造方法类
 * 
 * @author nerv
 *
 */
public class Class_ {

	Class<?> c = String.class;
	
	/**
	 * Class对象3种获取方式
	 */
	public void test1() {
		// 1.对象.getClass()
		c = new String().getClass();
		// 2.对象.类名
		c = String.class;
		// 3.Class.forName(类名)
		try {
			c = Class.forName("java.lang.String");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 反射创建普通对象
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception {
		// 1.调用无参构造
		String s = (String) c.newInstance();
		s = new String("李智大帝".getBytes(), "utf-8");
		// 2.调用有参构造
		s = (String) c.getConstructor(byte[].class, String.class).newInstance("李智大帝".getBytes(), "utf-8");
		System.out.println(s);
        System.out.println(Inner.class.getConstructor(Inner.class).getName());
	}
	
	/**
	 * 反射创建子类对象：要将外部类对象作为第一个构造参数传入
	 * @throws Exception
	 */
	@Test
	public void test3() throws Exception {
		// 内部类无参构造
		Inner inner = (Inner) Inner.class.getConstructor(Class_.class).newInstance(new Class_());
		System.out.println(inner.name);
		// 内部类有参构造
		inner = (Inner) Inner.class.getConstructor(Class_.class,String.class).newInstance(new Class_(),"你好");
		System.out.println(inner.name);
	}
	public class Inner {
        public String name = "默认值";
        public Inner() { }	// 无参构造
        public Inner(String name) {	// 有参构造
        	this.name = name;
        }
    }

	/**
	 * 私有访问权限
	 * @throws Exception 
	 */
	@Test
	public void test4() throws Exception {
		try {
			// 可获取私有属性
			Field f = c.getDeclaredField("hash"); 
			System.out.println(f.getName());
			// 反射不能直接使用私有属性/方法/构造，必须赋权限
			f.setAccessible(true);
			f.set(new String(), 12);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载资源：path以/开头则以classpath为根目录，否则从当前class文件目录开始，返回第一个匹配到的资源
	 */
	@Test
	public void test5() {
		URL url = this.getClass().getResource("");
		System.out.println(url.getPath());
		url = this.getClass().getResource("/");
		System.out.println(url.getPath());
	}
	
	/**
	 * 获取注解类：调用栈
	 * 		Class.createAnnotationData()
	 * 		AnnotationParser.parseAnnotations()	// sun.reflect.annotation.AnnotationParser
	 * 		AnnotationParser.annotationForMap()	// 动态代理Proxy动态生成注解类的代理实现类
	 * 参考Annotation_模拟注解底层实现原理
	 */
	@Test
	public void getAnnotation() {
		this.getClass().getAnnotation(Override.class);
	}
}