package com.java.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sun.reflect.annotation.AnnotationParser;
// import sun.reflect.annotation.AnnotationInvocationHandler;  包内可见

import org.junit.Test;

/**
 * Annotation jdk1.5引入注解机制
 * 
 * 	注解声明
 * 		有且只有一个RetentionPolicy属性，声明本注解的保留位置
 * 		有至少一个ElementType属性，声明本注解的作用目标
 * 		用@interface定义注解类；方法定义注解属性，default定义默认值
 * 		Java源码中，注解类是接口
 * 	底层原理
 * 		注解类静态编译时转为继承了 Annotation的接口
 * 		运行时，在sun.reflect.annotationAnnotationParser.annotationForMap()中以Proxy动态代理生成实现此接口的代理类
 * 			AnnotationInvocationHandler作为句柄，annotationForMap参数：
 * 				final Class<? extends Annotation> type,	// 注解类
 * 				final Map<String, Object> memberValues)	// 注解类方法名及默认值
 * 		每个注解对应一个代理类
 * 	JDK内置注解
 * 		1.标识注解：在 java.lang
 * 			@Override - 检查该方法是否是重写方法。若父类或实现的接口没有该方法时，会报编译错误
 * 			@Deprecated - 标记过时方法。如果使用该方法，会报编译警告。
 * 			@SuppressWarnings - 指示编译器去忽略注解中声明的警告。
 * 			@SafeVarargs - Java 7添加，忽略任何使用参数为泛型变量的方法或构造函数调用产生的警告
 * 			@FunctionalInterface - Java 8 添加，标识一个匿名函数或函数式接口。
 * 		2.元注解（作用在其他注解的注解）：在 java.lang.annotation
 * 			@Retention - 声明此注解的生命周期，默认RetentionPolicy.CLASS
 * 			@Target - 声明此注解的作用目标，参数为至少一个ElementType
 * 			@Documented - 声明此注解的信息展示在javadoc生成的用户文档中。默认javadoc不包含注解信息
 * 			@Inherited - 声明此注解有继承性。默认注解不可被继承
 * 			@Repeatable - Java 8 添加，声明此注解可以在同一个声明上使用多次。
 * 
 * @author nerv
 *
 */
public class Annotation_ {

	//=================================注解=========================================
	
	/**
	 * RetentionPolicy注解的生命周期，为@Retention参数
	 * 		SOURCE		注解只存在于源码中，静态编译时被删除，默认
	 * 		CLASS		注解存在于源码和字节码文件中，JVM加载类时丢弃
	 * 		RUNTIME		注解存在于源码和字节码文件中，由JVM读入方法区，可在运行时通过反射获取
	 * @return
	 */
	public RetentionPolicy retentionPolicy() {
		return RetentionPolicy.CLASS;
	}
	
	/**
	 * ElementType注解作用目标，为@Target参数
	 * 		METHOD				方法
	 * 		TYPE				类（interface (包括@interface)、class、enum）
	 * 		PARAMETER			方法参数
	 * 		FIELD				属性（包括类属性、枚举的常量）
	 * 		CONSTRUCTOR			构造函数
	 * 		LOCAL_VARIABLE		局部变量
	 * 		ANNOTATION_TYPE		注解
	 * 		PACKAGE				package-info.java 
	 * 		TYPE_PARAMETER		类型化参数，即泛型
	 * 		TYPE_USE			任何类型，包括以上声明的所有位置
	 * @return
	 */
	public ElementType elementType() {
		return ElementType.ANNOTATION_TYPE;
	}
	
	/**
	 * @Inherited 可继承，默认注解不可被继承
	 */
	@Test
	public void inherited() {
		// A标注的@InheritedTest是可继承的
		System.out.println(A.class.isAnnotationPresent(InheritedAnno.class));
		// B隐式标注了@InheritedTest
		System.out.println(B.class.isAnnotationPresent(InheritedAnno.class));
	}
	
	/**
	 * @SuppressWarnings 指示编译器去忽略注解中声明的警告
	 */
	public void suppressWarnings() {
		// 忽略两个警告
		@SuppressWarnings(value={"deprecation", "unused"})
		int year = new Date().getYear();
	}
	
	//=================================反射获取注解=========================================
	
	@Test
	public void getAnnotationReflect() {
		// 动态代理生成的代理类转为本地文件
		System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
		Class<?> c = A.class;
		for(Annotation a : c.getAnnotations()) {
			System.out.println(a.annotationType().getName());
		}
		// 判断是否包含注解
		if (c.isAnnotationPresent(InheritedAnno.class)) {
			// 获取注解
			System.out.println(c.getAnnotation(InheritedAnno.class));
		}
	}
	
	//=================================模拟注解实现原理=========================================
	
	/** 模拟注解类 */
	private interface MyAnnotation extends Annotation {
		int count();//AnnotationInvocationHandler t;
	}
	
	/** 注解代理句柄，模拟AnnotationInvocationHandler */
	private class MyAnnotationInvocationHandler implements InvocationHandler {
		private final Map<String, Object> memberValues;
		private final Class<? extends Annotation> clazz;

		public MyAnnotationInvocationHandler(Map<String, Object> memberValues, Class<? extends Annotation> clazz) {
			this.memberValues = memberValues;
			this.clazz = clazz;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			Object value;
			switch (methodName) {
				case "toString":
					value = super.toString();
					break;
				case "hashCode":
					value = super.hashCode();
					break;
				case "equals":
					value = super.equals(args[0]);
					break;
				case "annotationType":
					value = clazz;
					break;
				default:
					value = memberValues.get(methodName);
			}
			return value;
		}
	}
	
	/**
	 * 模拟Class.getAnnotation()
	 */
	@Test
	public void getAnnotation() {
		// 模拟注解成员属性从常量池解析的过程
		Map<String,Object> values = new HashMap<>(8);
		values.put("count", 1);
		// 模拟AnnotationParser.annotationForMap()
		MyAnnotation proxy = (MyAnnotation)Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[]{MyAnnotation.class},
				new MyAnnotationInvocationHandler(values, MyAnnotation.class));
		// 使用注解
		System.out.println(proxy.count());
	}
	
	
	//=================================自定义对象=========================================
	
	/** 定义可被继承的注解 */
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	private @interface InheritedAnno {}

	@InheritedAnno
	private class A {}	// A标注的@InheritedTest是可继承的
	
	private class B extends A {}	// B隐式标注了@InheritedTest
}
