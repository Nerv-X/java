package com.java.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;
/**
 * 架构
 * 	List(I)：允许根据索引位置操作数据。可重复，有序
 *		ArrayList：非并发
 *		CopyOnWriteArrayList：并发
 *		Vector：并发。但一个线程用Iterator迭代时，另一线程不可修改其数据否则报错ConcurrentModificationException
 *			Stack：可实现后进先出的对象堆栈
 *
 * 类/接口
 *	Collection(I)：提供集合框架最主要的操作（增删改查）
 * @author Administrator
 *
 */
public class List_ {
	
	/**
	 * ArrayList：非并发
	 * 	add()
	 * 		步骤
	 * 			①判断是否要扩容；②数组赋值；③size++
	 * 	并发问题一
	 * 		当前容量只剩一个位置，线程A、B同时判断①无需扩容，A执行②③后，B执行②时数组越界
	 * 	并发问题二
	 * 		线程A、B同时执行②③，对同一位置赋值，预期size+2，实际size+1
	 * @throws InterruptedException 
	 * 		
	 */
	public void test1() throws InterruptedException {
		List<String> list = new ArrayList<>();
		// 创建100个线程分别add()100次
		Thread[] threads = new Thread[100];
		IntStream.range(0, threads.length).forEach(i -> 
			threads[i] =  new Thread(() -> {
				IntStream.range(0, 100).forEach(j -> list.add("anyString"));
		}));
		// 验证问题一：ArrayList大概率报错
		IntStream.range(0, threads.length).forEach(i -> threads[i].start());
		Thread.sleep(3000);
		// 验证问题二：数量 < 10000
		System.out.println(list.size());
	}
	/**
	 * Vector：并发。但一个线程用Iterator迭代时，另一线程不可修改其数据否则报错ConcurrentModificationException
	 * CopyOnWriteArrayList：并发，无此问题
	 * 	注意：Iterator的问题换成非并发的集合也是如此
	 * @throws InterruptedException
	 */
	public void test2() throws InterruptedException {
		List<String> list = new Vector<>();
		// 1.准备数据
		IntStream.range(0, 50000).forEach(i -> {
			list.add("Thread" + (i + 1));
		});
		// 2.Iterator迭代
		Thread a = new Thread(() -> {
			Iterator<String> keyIterator = list.iterator();
			while(keyIterator.hasNext()) {
				System.out.println(keyIterator.next());
			}
		});
		// 3.另一线程增删数据，报错
		Thread b = new Thread(() -> {
			list.add("a");
		});
		a.start();
		Thread.sleep(300);
		b.start();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		new List_().test1();
	}
}
