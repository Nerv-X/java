package com.java.lang;

import java.lang.Integer;
import java.lang.InterruptedException;
import java.lang.System;
import java.lang.Thread;
import java.lang.ThreadLocal;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
/**
 * ThreadLocal 为每一个使用该变量的线程提供独立的副本，以保证线程间数据隔离
 * 	原理
 * 		线程Thread内部有一ThreadLocalMap对象，Key是ThreadLocal对象，Value是该ThreadLocal set()的值
 * 	实例方法
 * 		initalValue()		设置初始化值，默认返回null
 * 		set()				指定存储的值，遍历Entry时发现key==null的会直接替换到此位置，防止内存泄露
 * 		get()				获取值，若未执行过set()，则返回initalValue()的返回值
 * 	静态方法
 * 		withInital()		相当于重写initalValue()
 * 
 * ThreadLocalMap.Entry 继承了WeakReference以确保JVM回收时，自动回收防止内存溢出
 * @author nerv
 *
 */
public class ThreadLocal_ {

	/**
	 * 线程安全，set设置1秒后，get()值不变。不受其它线程影响
	 */
	@Test
	public void test1() {
		ThreadLocal<Integer> local = new ThreadLocal<Integer>();
		IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			local.set(i);
			System.out.println(Thread.currentThread() + " set i " + local.get());
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread() + " get i " + local.get());
		}))
		.peek(t -> t.start())
		.collect(Collectors.toList())
		.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	
	
}
