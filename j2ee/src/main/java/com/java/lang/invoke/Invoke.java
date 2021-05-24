package com.java.lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.junit.Test;

/**
 * invoke包，动态语言支持
 * @author Nerv
 *
 */
public class Invoke {

	private MethodHandles.Lookup lookup = MethodHandles.lookup();
	
	static class A {
		public void println(String s) {
			System.out.println(s);
		}
		
		public static int add(int a, int b) {
			return a + b;
		}
	}
	public static void main(String[] args) throws Throwable {
		Object obj = System.currentTimeMillis() % 2 == 0 ? System.out: new A();
		// 动态类型语言，无论obj最终是哪个实现类，都能调用其println方法
		getPrintlnMH(obj).invokeExact("李智大帝万岁万万岁");
	}
	
	public static MethodHandle getPrintlnMH(Object receiver) throws Throwable {
		MethodType mt = MethodType.methodType(void.class, String.class);
		return MethodHandles.lookup().findVirtual(receiver.getClass(), "println", mt).bindTo(receiver);
	}

	/**
	 * MethodType：方法类型，包含返回值类型（第一个参数），形参类型（剩余参数）
	 * 
	 * 	获取MethodType对象方式
	 * 		1. MethodType.methodType()			返回值类型（第一个参数），形参类型（剩余参数）
	 * 		2. MethodType.genericMethodType()	通用，参数、返回值Object
	 * 		3. methodHandle.type()				获取方法句柄的参数类型
	 * 
	 * 	实例方法：对MethodType对象的任何修改都会返回新的对象（类似String）
	 * 		wrap()		参数、返回值中基本类型转包装类型
	 * 		unwrap()	参数、返回值中包装类型转基本类型
	 * 		generic()	参数、返回值都变为Object
	 * 		erase()		参数、返回值中引用类型变为Object，基本类型不变
	 * @throws Throwable
	 */
	@Test
	public void t1() throws Throwable {
		/*
		 * 创建MethodType对象
		 */
		// 1. 返回值void，参数String；注意void不能用java.lang.Void
		MethodType type = MethodType.methodType(void.class, String.class);
		// 2. 返回值Object，参数2个Object、1个Object[]
		type = MethodType.genericMethodType(2, true);
		// 3. 返回值Object，参数3个Object；等价于MethodType.genericMethodType(3, false);
		type = MethodType.genericMethodType(3);
		// 4. 方法句柄的参数类型
		MethodHandle handle = getPrintlnMH(new A());
		type = handle.type();
		
		/*
		 * 实例方法
		 */
		type = type.wrap();
		type = type.unwrap();
		type = type.generic();
		type = type.erase();
		System.out.println(handle.type());
	}
	
	/**
	 * MethodHandle.Lookup: MethodHandle工厂类
	 * 
	 * 	对应字节码指令的执行权限校验行为
	 * 		findSpecial()		对应invokespecial指令
	 * 		findVirtual()		对应invokevirtual、invokeinterface指令
	 * 		findStatic()		对应invokestatic指令
	 * 	特殊的MethodHandle
	 * 		findConstructor()
	 * 		findGetter()
	 * 		findSetter()
	 * 
	 * 	配合反射API创建MethodHandle，对应的方法有unreflect、unreflectSpecial等。
	 * 		unreflect(Method m)
	 * 		unreflectConstructor(Constructor<?> c)
	 * 		unreflectGetter(Field f)
	 * 		unreflectSetter(Field f)
	 * 		unreflectSpecial(Method m, Class<?> specialCaller)
	 * 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	@Test
	public void t3() throws NoSuchMethodException, IllegalAccessException {
		// find时即检查方法是否有效
		this.lookup.findVirtual(A.class, "println", MethodType.methodType(void.class, String.class));
	}
	
	/**
	 * asType(newType)：支持模糊匹配并返回新的MethodHandle，newType范围不能比实际MethodType更小
	 * 		Lookup.findXX()时MethodType必须严格匹配方法签名；asType()则松散匹配（自动转型）
	 * 		实例方法需先bindTo()绑定方法接收者，再asType()
	 * 		匹配时，自动转型支持：
	 * 			1）	向上转型，如形参Object，实参String
	 * 			2） 基本类型返回扩大，如形参long，实参int
	 * 			3） 基本类型装箱、拆箱
	 * 			4）	如果S有返回值，T的返回值void，则S的返回值被丢弃（不接收inovke()返回值）
	 * 			5） 如果S的返回值void，T的返回值是引用类型，则T的返回值是null
	 * 			6）	如果S的返回值void，T的返回值是基本类型，则T的返回值是0
	 * @throws Throwable
	 */
	@Test
	public void t4() throws Throwable {
		MethodType type = MethodType.methodType(void.class, String.class);
		MethodHandle handle = lookup.findVirtual(A.class, "println", type);
		type = MethodType.methodType(Object.class, Object.class);
		// 实例方法需先bindTo()
		handle = handle.bindTo(new A()).asType(type);
		
		type = MethodType.methodType(int.class, int.class, int.class);
		handle = lookup.findStatic(A.class, "add", type);
		type = MethodType.genericMethodType(2);
		handle = handle.asType(type);
	}
	
	/**
	 * MethodHandle：方法调用
	 * 
	 * 	调用
	 * 		invokeExact()		严格的类型匹配：基本类型和包装类型不通用；如果方法有返回值则必须用同类型的参数接收，不能向上转型
	 * 		invoke()			C++底层：先将实参自动类型转换为新的MethodType，再通过asType()以新的MethodType生成新的MethodHandle，最后执行invokeExact()。类型转换见asType()
	 * 		invokeWithArguments()	先asType()将形参、返回值类型全部按Object生成新的MethodType及MethodHandle，再将实参转为Object传入invokeExact
	 * 								可被反射API获取和调用，invokeExact和invoke不可以。作为反射API和方法句柄之间的桥梁
	 * @throws Throwable
	 */
	@Test
	public void t2() throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		// 1. invokeExact()：invokeExact(方法接收者+参数) 或 bind(方法接收者).invokeExact(参数)
		MethodType type = MethodType.methodType(String.class, int.class, int.class);
		MethodHandle mh = lookup.findVirtual(String.class, "substring", type);
			// 1）invokeExact(方法接收者,实参)
			// 必须用参数接收返回值，否则认为返回void；返回值必须强制转为String，否则认为返回Object
		Object str =  (String) mh.invokeExact("Hello World", 1, 3);
			// 2）bindTo(方法接收者).invokeExact(实参)
			// 此处虚方法，Java语言规则，方法第一形参是隐式的代表方法接收者（this），用bindTo()绑定
		str = (String)mh.bindTo("Hello World").invokeExact(1,3);
		// 2. invokeWithArguments
		str = mh.bindTo("hello").invokeWithArguments(1,3);
		System.out.println(str);
		// 3. invoke
		type = MethodType.methodType(long.class, long.class, long.class);
		mh = lookup.findStatic(Math.class, "min", type);
		type = MethodType.methodType(Object.class, Object.class, Object.class);
		mh = mh.asType(type);
		// int转long，Long转long，返回值long转Object
		Object o = mh.invoke(2, new Long(1));
		System.out.println(o);
	}

}